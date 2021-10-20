/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ltd.vastchain.face

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.window.WindowManager
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.ArrayDeque
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

import android.view.*
import androidx.camera.core.*
import androidx.camera.core.Camera
import ltd.vastchain.face.databinding.CameraUiContainerBinding
import ltd.vastchain.face.databinding.FragmentCameraBinding
import ltd.vastchain.face.utils.ANIMATION_FAST_MILLIS
import ltd.vastchain.face.utils.ANIMATION_SLOW_MILLIS
import ltd.vastchain.face.utils.simulateClick
import java.io.*


/** Helper type alias used for analysis use case callbacks */
typealias LumaListener = (luma: Double) -> Unit

/**
 * Main fragment for this app. Implements all camera operations including:
 * - Viewfinder
 * - Photo taking
 * - Image analysis
 */
class CameraFragment : Fragment() {

	private var _fragmentCameraBinding: FragmentCameraBinding? = null

	private val fragmentCameraBinding get() = _fragmentCameraBinding!!

	private var cameraUiContainerBinding: CameraUiContainerBinding? = null

	private lateinit var outputDirectory: File
	private lateinit var broadcastManager: LocalBroadcastManager

	private var displayId: Int = -1
	private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
	private var preview: Preview? = null
	private var imageCapture: ImageCapture? = null
	private var imageAnalyzer: ImageAnalysis? = null
	private var camera: Camera? = null
	private var cameraProvider: ProcessCameraProvider? = null
	private lateinit var windowManager: WindowManager

	private val displayManager by lazy {
		requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
	}

	/** Blocking camera operations are performed using this executor */
	private lateinit var cameraExecutor: ExecutorService

	/** Volume down button receiver used to trigger shutter */
	private val volumeDownReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
				// When the volume down button is pressed, simulate a shutter button click
				KeyEvent.KEYCODE_VOLUME_DOWN -> {
					cameraUiContainerBinding?.cameraCaptureButton?.simulateClick()
				}
			}
		}
	}

	/**
	 * We need a display listener for orientation changes that do not trigger a configuration
	 * change, for example if we choose to override config change in manifest or for 180-degree
	 * orientation changes.
	 */
	private val displayListener = object : DisplayManager.DisplayListener {
		override fun onDisplayAdded(displayId: Int) = Unit
		override fun onDisplayRemoved(displayId: Int) = Unit
		override fun onDisplayChanged(displayId: Int) = view?.let { view ->
			if (displayId == this@CameraFragment.displayId) {
				Log.d(TAG, "Rotation changed: ${view.display.rotation}")
				imageCapture?.targetRotation = view.display.rotation
				imageAnalyzer?.targetRotation = view.display.rotation
			}
		} ?: Unit
	}

	override fun onResume() {
		super.onResume()
//        // Make sure that all permissions are still present, since the
//        // user could have removed them while the app was in paused state.
//        if (!PermissionsFragment.hasPermissions(requireContext())) {
//            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
//                    CameraFragmentDirections.actionCameraToPermissions()
//            )
//        }
	}

	override fun onDestroyView() {
		_fragmentCameraBinding = null
		super.onDestroyView()

		// Shut down our background executor
		cameraExecutor.shutdown()

		// Unregister the broadcast receivers and listeners
		broadcastManager.unregisterReceiver(volumeDownReceiver)
		displayManager.unregisterDisplayListener(displayListener)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
		return fragmentCameraBinding.root
	}

	private fun setGalleryThumbnail(uri: Uri) {
		// Run the operations in the view's thread
//        cameraUiContainerBinding?.photoViewButton?.let { photoViewButton ->
//            photoViewButton.post {
//                // Remove thumbnail padding
//                photoViewButton.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
//
//                // Load thumbnail into circular button using Glide
//                Glide.with(photoViewButton)
//                        .load(uri)
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(photoViewButton)
//            }
//        }
	}

	@SuppressLint("MissingPermission")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Initialize our background executor
		cameraExecutor = Executors.newSingleThreadExecutor()

		broadcastManager = LocalBroadcastManager.getInstance(view.context)

		// Set up the intent filter that will receive events from our main activity
		val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
		broadcastManager.registerReceiver(volumeDownReceiver, filter)

		// Every time the orientation of device changes, update rotation for use cases
		displayManager.registerDisplayListener(displayListener, null)

		//Initialize WindowManager to retrieve display metrics
		windowManager = WindowManager(view.context)

		// Determine the output directory
		outputDirectory = FaceActivity.getOutputDirectory(requireContext())

		// Wait for the views to be properly laid out
		fragmentCameraBinding.viewFinder.post {

			// Keep track of the display in which this view is attached
			displayId = fragmentCameraBinding.viewFinder.display.displayId

			// Set up the camera and its use cases
			setUpCamera()
		}
	}

	/**
	 * Inflate camera controls and update the UI manually upon config changes to avoid removing
	 * and re-adding the view finder from the view hierarchy; this provides a seamless rotation
	 * transition on devices that support it.
	 *
	 * NOTE: The flag is supported starting in Android 8 but there still is a small flash on the
	 * screen for devices that run Android 9 or below.
	 */
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)

		// Rebind the camera with the updated display metrics
		bindCameraUseCases()

		// Enable or disable switching between cameras
		updateCameraSwitchButton()
	}

	/** Initialize CameraX, and prepare to bind the camera use cases  */
	private fun setUpCamera() {
		val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
		cameraProviderFuture.addListener(Runnable {

			// CameraProvider
			cameraProvider = cameraProviderFuture.get()

			// Select lensFacing depending on the available cameras
			lensFacing = when {
				hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
				hasBackCamera() -> CameraSelector.LENS_FACING_BACK
				else -> throw IllegalStateException("Back and front camera are unavailable")
			}

			// Enable or disable switching between cameras
			updateCameraSwitchButton()

			// Build and bind the camera use cases
			bindCameraUseCases()
		}, ContextCompat.getMainExecutor(requireContext()))
	}

	/** Declare and bind preview, capture and analysis use cases */
	private fun bindCameraUseCases() {

		// Get screen metrics used to setup camera for full screen resolution
		val metrics = windowManager.getCurrentWindowMetrics().bounds
		Log.d(TAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")

		val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
		Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

		val rotation = fragmentCameraBinding.viewFinder.display.rotation

		// CameraProvider
		val cameraProvider = cameraProvider
			?: throw IllegalStateException("Camera initialization failed.")

		// CameraSelector
		val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

		// Preview
		preview = Preview.Builder()
			// We request aspect ratio but no resolution
			.setTargetAspectRatio(screenAspectRatio)
			// Set initial target rotation
			.setTargetRotation(rotation)
			.build()


		// ImageCapture
		imageCapture = ImageCapture.Builder()
			.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
			// We request aspect ratio but no resolution to match preview config, but letting
			// CameraX optimize for whatever specific resolution best fits our use cases
			.setTargetAspectRatio(screenAspectRatio)
			// Set initial target rotation, we will have to call this again if rotation changes
			// during the lifecycle of this use case
			.setTargetRotation(rotation)
			.build()

		// ImageAnalysis
		imageAnalyzer = ImageAnalysis.Builder()
			// We request aspect ratio but no resolution
			.setTargetAspectRatio(screenAspectRatio)
			// Set initial target rotation, we will have to call this again if rotation changes
			// during the lifecycle of this use case
			.setTargetRotation(rotation)
//			.setTargetResolution(Size(320,240))
//			.setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
			.build()
			// The analyzer can then be assigned to the instance
			.also {
				it.setAnalyzer(cameraExecutor, LuminosityAnalyzer(context) { luma ->
					// Values returned from our analyzer are passed to the attached listener
					// We log image analysis results here - you should do something useful
					// instead!
//					Log.d(TAG, "Average luminosity: $luma")
				})
			}
		// Must unbind the use-cases before rebinding them
		cameraProvider.unbindAll()

		try {
			// A variable number of use-cases can be passed here -
			// camera provides access to CameraControl & CameraInfo
			camera = cameraProvider.bindToLifecycle(
				this, cameraSelector, preview, imageCapture, imageAnalyzer
			)

			// Attach the viewfinder's surface provider to preview use case
			preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)



			Log.e("cxd", camera?.cameraInfo?.zoomState?.value?.zoomRatio?.toString()?:"")
			Log.e("cxd", camera?.cameraInfo?.zoomState?.value?.maxZoomRatio?.toString()?:"")
			Log.e("cxd", camera?.cameraInfo?.zoomState?.value?.minZoomRatio?.toString()?:"")

//			camera?.cameraControl?.setZoomRatio(1f);
		} catch (exc: Exception) {
			Log.e(TAG, "Use case binding failed", exc)
		}
	}

	/**
	 *  [androidx.camera.core.ImageAnalysis.Builder] requires enum value of
	 *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
	 *
	 *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
	 *  of preview ratio to one of the provided values.
	 *
	 *  @param width - preview width
	 *  @param height - preview height
	 *  @return suitable aspect ratio
	 */
	private fun aspectRatio(width: Int, height: Int): Int {
		val previewRatio = max(width, height).toDouble() / min(width, height)
		if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
			return AspectRatio.RATIO_4_3
		}
		return AspectRatio.RATIO_16_9
	}


	/** Enabled or disabled a button to switch cameras depending on the available cameras */
	private fun updateCameraSwitchButton() {
		try {
			cameraUiContainerBinding?.cameraSwitchButton?.isEnabled =
				hasBackCamera() && hasFrontCamera()
		} catch (exception: CameraInfoUnavailableException) {
			cameraUiContainerBinding?.cameraSwitchButton?.isEnabled = false
		}
	}

	/** Returns true if the device has an available back camera. False otherwise */
	private fun hasBackCamera(): Boolean {
		return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
	}

	/** Returns true if the device has an available front camera. False otherwise */
	private fun hasFrontCamera(): Boolean {
		return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
	}

	/**
	 * Our custom image analysis class.
	 *
	 * <p>All we need to do is override the function `analyze` with our desired operations. Here,
	 * we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
	 */
	private class LuminosityAnalyzer(
		private val context: Context? = null,
		listener: LumaListener? = null
	) : ImageAnalysis.Analyzer {
		private var testOnce = false;
		private var frames = 1
		private var beginFrames = 5
		private var period = 2
		private val frameRateWindow = 8
		private val frameTimestamps = ArrayDeque<Long>(5)
		private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
		private var lastAnalyzedTimestamp = 0L
		var framesPerSecond: Double = -1.0
			private set

		/**
		 * Used to add listeners that will be called with each luma computed
		 */
		fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)

		/**
		 * Helper extension function used to extract a byte array from an image plane buffer
		 */
		private fun ByteBuffer.toByteArray(): ByteArray {
			rewind()    // Rewind the buffer to zero
			val data = ByteArray(remaining())
			get(data)   // Copy the buffer into a byte array
			return data // Return the byte array
		}

		/**
		 * Analyzes an image to produce a result.
		 *
		 * <p>The caller is responsible for ensuring this analysis method can be executed quickly
		 * enough to prevent stalls in the image acquisition pipeline. Otherwise, newly available
		 * images will not be acquired and analyzed.
		 *
		 * <p>The image passed to this method becomes invalid after this method returns. The caller
		 * should not store external references to this image, as these references will become
		 * invalid.
		 *
		 * @param image image being analyzed VERY IMPORTANT: Analyzer method implementation must
		 * call image.close() on received images when finished using them. Otherwise, new images
		 * may not be received or the camera may stall, depending on back pressure setting.
		 *
		 */
		override fun analyze(image: ImageProxy) {
			// If there are no listeners attached, we don't need to perform analysis
			if (listeners.isEmpty()) {
				image.close()
				return
			}

			// Keep track of frames analyzed
			val currentTime = System.currentTimeMillis()
			frameTimestamps.push(currentTime)

			// Compute the FPS using a moving average
			while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
			val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
			val timestampLast = frameTimestamps.peekLast() ?: currentTime
			framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
					frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0
			// Analysis could take an arbitrarily long amount of time
			// Since we are running in a different thread, it won't stall other use cases

			lastAnalyzedTimestamp = frameTimestamps.first

			if (FaceManager.needTakePhoto()) {
				if (frames > beginFrames && frames % period == 0) {
					FaceManager.analyse(image)
				}
				frames += 1
			}

			// Extract image data from callback object
//            val data = buffer.toByteArray()
//
//            // Convert the data into an array of pixel values ranging 0-255
//            val pixels = data.map { it.toInt() and 0xFF }
//
//
//            // Compute average luminance for the image
//            val luma = pixels.average()
//
//            // Call all listeners with new value
//            listeners.forEach { it(luma) }

			image.close()
		}
	}


	companion object {

		private const val TAG = "CameraXBasic"
		private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
		private const val PHOTO_EXTENSION = ".jpg"
		private const val RATIO_4_3_VALUE = 4.0 / 3.0
		private const val RATIO_16_9_VALUE = 16.0 / 9.0

		/** Helper function used to create a timestamped file */
		private fun createFile(baseFolder: File, format: String, extension: String) =
			File(
				baseFolder, SimpleDateFormat(format, Locale.US)
					.format(System.currentTimeMillis()) + extension
			)
	}
}
