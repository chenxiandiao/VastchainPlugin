package ltd.vastchain.camera.record.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ltd.vastchain.camera.record.IRecordListener
import ltd.vastchain.camera.record.R
import ltd.vastchain.camera.record.databinding.ActivityCameraRecordBinding
import ltd.vastchain.camera.record.fragments.CameraFragment

/**
 * Created by admin on 2021/12/20.
 */
class CameraRecordActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityCameraRecordBinding

    companion object {
        private var callBack: IRecordListener? = null
        private var recordDuration = 30
        fun start(
            context: Activity,
            requestCode: Int,
            duration: Int = 30,
            listener: IRecordListener
        ) {
            callBack = listener
            recordDuration = duration
            val intent = Intent(context, CameraRecordActivity::class.java)
            context.startActivityForResult(intent, requestCode)
        }
    }

    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
    private var permission = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityCameraRecordBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        checkCamera()
    }

    private fun checkCamera() {
        if (checkPermission(this, permission).not()) {
            ActivityCompat.requestPermissions(
                this,
                permission,
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
            )
        } else {
            addCameraFragment()
        }
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
                    addCameraFragment()
                } else {
                    Toast.makeText(this, "请授权应用权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addCameraFragment() {
        supportFragmentManager.beginTransaction().add(R.id.fragment, CameraFragment().apply {
            this.callBack = CameraRecordActivity.callBack
            this.recordDuration = CameraRecordActivity.recordDuration
        }).commit()
    }
}