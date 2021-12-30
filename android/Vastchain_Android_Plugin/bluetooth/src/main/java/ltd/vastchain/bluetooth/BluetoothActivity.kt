package ltd.vastchain.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ltd.vastchain.jsbridge.CoreJsCallback
import ltd.vastchain.jsbridge.CoreWebProgressBar
import ltd.vastchain.jsbridge.CoreWebView
import ltd.vastchain.jsbridge.util.JSONUtil
import ltd.vastchain.jsbridge.util.LogUtil


/**
 * Created by admin on 2021/9/10.
 */


class BluetoothActivity : AppCompatActivity() {

	companion object {
		const val URL = "http://10.155.87.121:10086/#/subPackage/warehouseManage/pages/wareHouseOperation/index?token=MmoXuOXOnvy8_r0Qstk4al1pHgdq-mmH&orgID=139723245184659456"
//		const val URL = "http://www.baidu.com"
	}

	private val REQUEST_ENABLE_BT: Int = 100
	private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
	private var mJSBridge: BlueJSBridge? = null
	private var webView: CoreWebView? = null
	private var bluetoothPlugin: IBluePlugin? = null

	private var callback: CoreJsCallback? = null
	private var progressBar: CoreWebProgressBar? = null

	private var permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

	private var url: String = URL

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.bluetooth_main_activity)
		if (intent.getStringExtra("url").isNullOrEmpty()) {
			LogUtil.e("intent无传递参数")
		}
		progressBar = findViewById(R.id.web_progressbar)
		url = intent.getStringExtra("url") ?: URL
		webView = findViewById(R.id.webView)
		mJSBridge = BlueJSBridge(webView!!)
		initData()
		webView?.addJavascriptInterface(mJSBridge!!, "BlueJSBridge")
//		webView?.loadUrl("http://10.150.229.13:8000/")
		webView?.loadUrl(url)
		webView?.webViewClient = object : WebViewClient() {
			override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
				println("开始加载了")
				progressBar?.visibility = View.VISIBLE
			}

			//设置结束加载函数
			override fun onPageFinished(view: WebView, url: String) {
				println("结束加载了")
				progressBar?.startProgressAnimation(100, true)
			}

		}
		webView?.handleWebViewClient = object : WebChromeClient(){
			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				progressBar?.startProgressAnimation(newProgress, false)

			}
		}
		initListener()
	}

	private fun initListener() {
		findViewById<View>(R.id.v_back).setOnClickListener{
			if(webView?.canGoBack() == true) {
				webView?.goBack()
			} else {
				finish()
			}
		}
	}

	private fun initData() {
		bluetoothPlugin = TraditionBluetoothPlugin(application, this)
		mJSBridge?.apply {
			bluetoothPlugin = this@BluetoothActivity.bluetoothPlugin
			checkPermission = { callback ->
				this@BluetoothActivity.callback = callback
				if (bluetoothPlugin?.isBleSupported() == true) {
					if (bluetoothPlugin?.isEnabled() == false) {
						val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					} else {
						LogUtil.e("checkLocation")
						checkLocation()
					}
				} else {
					callback.invoke(BlueJSBridge.SET_UP, JSONUtil.error(message = "该设备不支持蓝牙"))
					Toast.makeText(this@BluetoothActivity, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
			checkLocation()
		}
	}

	private fun checkLocation() {
		if (checkPermission(context = this@BluetoothActivity, permission).not()) {
			ActivityCompat.requestPermissions(
				this@BluetoothActivity,
				permission,
				REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
			)
		} else {
			if (this.callback!= null ) {
				LogUtil.e("callback is not null")
			} else {
				LogUtil.e("callback is null")
			}
			this.callback?.invoke(BlueJSBridge.SET_UP, JSONUtil.success())
		}
	}


	override fun onDestroy() {
		super.onDestroy()
		bluetoothPlugin?.disconnect()
	}

	private fun checkPermission(context: Context?, permissions: Array<String>): Boolean {
		if (context == null) {
			return false
		}
		for (permission in permissions) {
			if (ActivityCompat.checkSelfPermission(
					context,
					permission
				) != PackageManager.PERMISSION_GRANTED
			) {
				return false
			}
		}
		return true
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
				if (checkPermission(context = this, permission)) {
					bluetoothPlugin?.setUp()
				} else {
					Toast.makeText(this, "请授权应用权限", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}
}