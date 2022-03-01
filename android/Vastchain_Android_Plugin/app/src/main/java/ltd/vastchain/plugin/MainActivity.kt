package ltd.vastchain.plugin

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ltd.vastchain.bluetooth.BlueManager
import ltd.vastchain.camera.record.IRecordListener
import ltd.vastchain.camera.record.activity.CameraRecordActivity
import ltd.vastchain.face.FaceManager
import ltd.vastchain.face.IFaceCallBack
import ltd.vastchain.face.PersonInfoActivity
import ltd.vastchain.plugin.databinding.ActivityMainBinding
import ltd.vastchain.qrscan.QrScanManager
import net.arvin.selector.utils.FMediaUtils

class MainActivity : AppCompatActivity() {

	private lateinit var activityMainBinding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(activityMainBinding.root)

		activityMainBinding.tvGoWeb.setOnClickListener {
//			val intent = Intent(this, BluetoothActivity::class.java)
//			startActivity(intent)
//			BlueManager.start(this, "http://10.144.2.172:10086/#/subPackage/warehouseManage/pages/wareHouseOperation/index?token=MmoXuOXOnvy8_r0Qstk4al1pHgdq-mmH&orgID=139723245184659456")
//			BlueManager.start(this, "https://nimiq.github.io/qr-scanner/demo/")
//			BlueManager.start(this, "https://cozmo.github.io/jsQR")
//			BlueManager.start(this, "http://10.144.1.116:8000")

//			BlueManager.start(this, "https://patrol-test.tmp.vastchain.ltd/public/h5/index.html#/orderList?token=ImLZFS5TuM7bUPv_KFZI6OGmj4INi0ci&org_id=241886752633565184")
//				BlueManager.start(this, "http://10.144.1.116:80/#/orderList?token=ImLZFS5TuM7bUPv_KFZI6OGmj4INi0ci&org_id=241886752633565184")
//				BlueManager.start(this, "http://10.144.1.116:8000/storehouse")
//			BlueManager.start(this, "http://10.144.1.116/#/qrCode/device/discover")
//			264072416197742592
//			BlueManager.start(this, "http://10.144.1.116/#/qrCode/home?org_id=241574126971662336&token=m6fL_cPQicrdwJHXZ3BZvAJ3dLRz486w")
//			BlueManager.start(this, "http://10.144.2.167:8090/#/ordinaryScan?qr_code_id=264717701021556736&token=n5zVhfXqdewWtdnsj_AMD5l8w9aDYh5Q")
//			BlueManager.start(this, "http://10.144.2.167:8090/#/softwareScan?org_id=241574126971662336&token=n5zVhfXqdewWtdnsj_AMD5l8w9aDYh5Q&qr_code_id=264717701021556736")
//			BlueManager.start(this, "https://patrol-test.tmp.vastchain.ltd/public/h5/index.html#/formRepord?structId=158020541405261824&token=Toc2rcuZvgvXdsL72K04loCtiCtSUCOV&org_id=129940902556438528")
//			BlueManager.start(this, "http://10.144.1.116:80/#/test")
//			BlueManager.start(this, "http://10.144.1.116:8080/examples/webgl_loader_gltf.html")
			BlueManager.start(this, "http://10.144.1.116:10086/#/pages/tabs/home")
//			BlueManager.start(this, "https://threejs.org/examples/?q=gltf#webgl_loader_gltf_compressed")
		}


		activityMainBinding.tvGoScan.setOnClickListener {
//			val intent = Intent(this, TestScanActivity::class.java)
//			startActivity(intent)
			QrScanManager.start(this)
		}

		activityMainBinding.tvGoFaceHome.setOnClickListener {
			FaceManager.init(this)
			PersonInfoActivity.start(this)
			FaceManager.setFaceCallBack(object : IFaceCallBack{
				override fun success() {
					Log.e("cxd", "人脸识别成功")
				}

				override fun fail(msg: String) {

				}
			})
		}

		activityMainBinding.tvRecord.setOnClickListener {
			CameraRecordActivity.start(this, 300,30, object : IRecordListener{
				override fun success(uri: Uri?, duration: String) {
					Log.e("cxd", uri?.toString().orEmpty())
					Log.e("cxd", duration)
					FMediaUtils.getCameraFirstBucketId(this@MainActivity)
				}
			})
		}

//		activityMainBinding.tvGoFaceEyeMouth.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = false, mouthSkip = false)
//		}
//
//		activityMainBinding.tvGoFaceEye.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = false, mouthSkip = true)
//		}
//
//		activityMainBinding.tvGoFaceMouth.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = true, mouthSkip = false)
//		}
//
//		activityMainBinding.tvGoFace.setOnClickListener {
//			FaceActivity.start(this, eyeSkip = true, mouthSkip = true)
//		}

	}


}