//package ltd.vastchain.jsbridge.util
//
//import android.Manifest
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Build
//import android.provider.Settings
//import androidx.appcompat.app.AlertDialog
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import com.dxy.core.R
//import com.dxy.core.widget.PermissionRequestTipDialog
//import com.dxy.core.widget.asTo
//import com.dxy.core.widget.ktTry
//import com.dxy.core.widget.showSafely
//
///**
// * 权限工具类
// */
//object PermissionUtils {
//    @Suppress("ClassName")
//    object permission {
//        const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
//        const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
//        const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
//        const val CAMERA = Manifest.permission.CAMERA
//        const val GPS_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
//        const val WIFI_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
//
//        private const val GROUP_CAMERA = CAMERA
//
//        // android.permission_group.STORAGE
//        private const val GROUP_STORAGE = Manifest.permission_group.STORAGE
//
//        // android.permission-group.PHONE
//        private val GROUP_PHONE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Manifest.permission_group.PHONE
//        } else {
//            "android.permission-group.PHONE"
//        }
//
//        private const val GROUP_LOCATION = Manifest.permission_group.LOCATION
//
////        fun toPermissionGroupText(permissions: List<String>, grantResults: IntArray? = null): List<String> {
////            return mapPermissionToGroupList(permissions)
////                    .filterIndexed { index, _ ->
////                        if (grantResults == null) return@filterIndexed true
////                        ktTry { return@filterIndexed grantResults[index] == -1 }
////                        return@filterIndexed true
////                    }
////                    .toSet()
////                    .mapNotNull {
////                        when (it) {
////                            GROUP_STORAGE ->
////                                "存储权限"
////                            GROUP_CAMERA ->
////                                "相机权限"
////                            GROUP_PHONE ->
////                                "设备权限"
////                            GROUP_LOCATION ->
////                                "定位权限"
////                            else -> null
////                        }
////                    }
////        }
////
////        fun toPermissionGroupIconRes(permissions: List<String>, grantResults: IntArray? = null): List<Int> {
////            return mapPermissionToGroupList(permissions)
////                    .filterIndexed { index, _ ->
////                        if (grantResults == null) return@filterIndexed true
////                        ktTry { return@filterIndexed grantResults[index] == -1 }
////                        return@filterIndexed true
////                    }
////                    .toSet()
////                    .map {
////                        when (it) {
////                            GROUP_STORAGE ->
////                                R.drawable.download_small
////                            GROUP_CAMERA ->
////                                R.drawable.camera_ad
////                            GROUP_PHONE ->
////                                R.drawable.phone
////                            else -> 0
////                        }
////                    }
////        }
////
////        fun toFunctionText(permissions: List<String>, isRequest: Boolean, grantResults: IntArray? = null): List<String> {
////            return mapPermissionToGroupList(permissions)
////                    .filterIndexed { index, _ ->
////                        if (grantResults == null) return@filterIndexed true
////                        ktTry { return@filterIndexed grantResults[index] == -1 }
////                        return@filterIndexed true
////                    }
////                    .toSet()
////                    .mapNotNull {
////                        when (it) {
////                            GROUP_STORAGE ->
////                                if (isRequest) {
////                                    "为了您的浏览体验，我们会申请储存权限，保存图片或下载课程"
////                                } else {
////                                    "保存图片或下载课程"
////                                }
////                            GROUP_CAMERA ->
////                                if (isRequest) {
////                                    "为了您的使用体验，我们会申请相机权限，拍照上传头像或图片"
////                                } else {
////                                    "拍照上传头像或图片"
////                                }
////                            GROUP_PHONE ->
////                                if (isRequest) {
////                                    "为了您的账号安全，我们会申请使用设备标识码进行统计，提供视频投屏"
////                                } else {
////                                    "保证账号安全，提供视频投屏"
////                                }
////                            GROUP_LOCATION ->
////                                if (isRequest) {
////                                    "定位权限"
////                                } else {
////                                    "定位权限"
////                                }
////                            else -> null
////                        }
////                    }
////        }
////
////        private fun mapPermissionToGroupList(permissions: List<String>): List<String> {
////            return permissions.map {
////                when (it) {
////                    WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> GROUP_STORAGE
////                    READ_PHONE_STATE -> GROUP_PHONE
////                    CAMERA -> GROUP_CAMERA
////                    GPS_LOCATION, WIFI_LOCATION -> GROUP_LOCATION
////                    else -> it
////                }
////            }
////        }
////    }
//
//    fun checkPermission(context: Context?, vararg permissions: String): Boolean {
//        if (context == null) {
//            return false
//        }
//        for (permission in permissions) {
//            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun requestPermissionWithoutTipDialog(activity: Activity, requestCode: Int, vararg permissions: String) {
//        ActivityCompat.requestPermissions(activity, permissions, requestCode)
//    }
//
//    fun requestPermission(activity: Activity, requestCode: Int, vararg permissions: String) {
//        showPermissionRequestTipDialog(activity, permissions.toList()) {
//            ActivityCompat.requestPermissions(activity, permissions, requestCode)
//        }
//    }
//
//    fun requestPermission(fragment: Fragment, requestCode: Int, vararg permissions: String) {
//        showPermissionRequestTipDialog(fragment, permissions.toList()) {
//            fragment.requestPermissions(permissions, requestCode)
//        }
//    }
//
//    fun handlePermissionRequest(activity: Activity, grantResults: IntArray, permissions: Array<out String>, onGranted: (() -> Unit)?, onDenied: ((goSettings: Boolean) -> Unit)? = null) {
//        if (isGranted(grantResults)) {
//            onGranted?.invoke()
//        } else {
//            showPermissionDeniedTipDialog(activity, permissions.toList(), grantResults, onDenied)
//        }
//    }
//
//    fun handlePermissionRequestSilence(activity: Activity, grantResults: IntArray, permissions: Array<out String>, showTipDialog: (() -> Boolean), onGranted: (() -> Unit)?, onDenied: ((goSettings: Boolean) -> Unit)? = null) {
//        if (isGranted(grantResults)) {
//            onGranted?.invoke()
//        } else {
//            if (showTipDialog.invoke()) {
//                showPermissionDeniedTipDialog(activity, permissions.toList(), grantResults, onDenied)
//            } else {
//                onDenied?.invoke(false)
//            }
//        }
//    }
//
//    private fun isGranted(grantResults: IntArray): Boolean {
//        if (grantResults.isEmpty()) {
//            return false
//        }
//        grantResults.forEach {
//            if (it != PackageManager.PERMISSION_GRANTED) {
//                return false
//            }
//        }
//        return true
//    }
//
//    private fun showPermissionRequestTipDialog(fragmentOrActivity: Any, permissions: List<String>, onConfirm: () -> Unit) {
//        if (permissions.isEmpty()) {
//            onConfirm()
//            return
//        }
//        val fragment: Fragment? = fragmentOrActivity.asTo()
//        val activity: Activity? = fragmentOrActivity.asTo()
//        val context: Activity? = activity ?: fragment?.activity
//        if (context == null) {
//            onConfirm()
//            return
//        }
//        val permissionGroups = permission.toPermissionGroupText(permissions)
//        if (permissionGroups.isNullOrEmpty()) {
//            onConfirm()
//            return
//        }
//        val fm = when (fragmentOrActivity) {
//            is Fragment -> fragmentOrActivity.childFragmentManager
//            is FragmentActivity -> fragmentOrActivity.supportFragmentManager
//            else -> ActivityCollector.getTopActivity().asTo<FragmentActivity>()?.supportFragmentManager
//        }
//        if (fm == null) {
//            onConfirm()
//            return
//        }
//        PermissionRequestTipDialog.newInstance(ArrayList(permissions.toList())).apply {
//            dismissListener = { onConfirm() }
//        }.showSafely(fm)
//    }
//
//    private fun showPermissionDeniedTipDialog(activity: Activity, permissions: List<String>, grantResults: IntArray?, onDenied: ((goSettings: Boolean) -> Unit)? = null) {
//        AlertDialog.Builder(activity).setTitle("权限申请")
//                .setMessage(activity.getString(R.string.core_permission_deny_tip, permission.toPermissionGroupText(permissions, grantResults = grantResults).joinToString("，"), permission.toFunctionText(permissions, isRequest = false, grantResults = grantResults).joinToString("，")))
//                .setPositiveButton("去设置") { dialog, _ ->
//                    jumpToSystemSettings(activity)
//                    dialog.dismiss()
//                    onDenied?.invoke(true)
//                }
//                .setNegativeButton(activity.getString(R.string.core_cancel)) { dialog, _ ->
//                    dialog.dismiss()
//                    onDenied?.invoke(false)
//                }
//                .setCancelable(false)
//                .show()
//    }
//
//    fun jumpToSystemSettings(activity: Activity) {
//        ktTry {
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            intent.data = Uri.parse("package:" + activity.packageName)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            activity.startActivity(intent)
//        }
//    }
//
//    private fun permissionDeniedAlways(activity: Activity, permissions: Array<out String>): Boolean {
//        permissions.forEach { _ ->
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                            activity,
//                            Manifest.permission.ACCESS_FINE_LOCATION).not()) {
//                return true
//            }
//
//        }
//        return false
//    }
//
//    const val REQUEST_CODE_ACTION_LOCATION_SOURCE_SETTINGS = 1001
//    fun goGpsSetting(activity: Activity?, cancel: (() -> Unit)? = null) {
//        activity ?: return
//        AlertDialog.Builder(activity).setTitle("权限申请")
//                .setMessage("丁香妈妈需要使用您的位置\n以便为您精确推荐附近的优质服务")
//                .setPositiveButton("去设置") { dialog, _ ->
//                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                    activity.startActivityForResult(intent, REQUEST_CODE_ACTION_LOCATION_SOURCE_SETTINGS)
//                    dialog.dismiss()
//                }
//                .setNegativeButton(activity.getString(R.string.core_cancel)) { dialog, _ ->
//                    dialog.dismiss()
//                    cancel?.invoke()
//                }
//                .setCancelable(false)
//                .show()
//    }
//
//}
//
//
