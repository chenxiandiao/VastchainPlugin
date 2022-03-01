package ltd.vastchain.qrscan.huawei

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import ltd.vastchain.jsbridge.util.LogUtil
import ltd.vastchain.qrscan.QrScanManager

import ltd.vastchain.qrscan.databinding.ActivityEmptyBinding

/**
 * Created by admin on 2022/2/7.
 */
class HwQrScanActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
        const val REQUEST_CODE_SCAN_ONE = 0X01
    }

    private lateinit var binding: ActivityEmptyBinding
    private var permission = arrayOf(Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            startQrScan()
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
            HwQrScanActivity.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (checkPermission(context = this, permission)) {
                    startQrScan()
                } else {
                    Toast.makeText(this, "请授权应用权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startQrScan() {
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, HmsScanAnalyzerOptions.Creator().create())
        finish()
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            val obj: HmsScan? = data.getParcelableExtra(ScanUtil.RESULT)
            if (obj != null) {
                LogUtil.e(obj.getOriginalValue())
                QrScanManager.invoke(obj.getOriginalValue())
                finish()
            }
        }
    }

}