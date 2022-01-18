package ltd.vastchain.jsbridge.util;

import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.webkit.WebSettings

object AppUAUtils {

    lateinit var context: Context
    private var versionName: String? = null
    private var versionCode = 0
    private var appSessionId: String? = null
    private var appOSVersion: String? = null
    private var appDeviceName: String? = null

    // Mozilla/5.0 (Linux; Android 10; LYA-AL00 Build/HUAWEILYA-AL00L; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.106 Mobile Safari/537.36 dxyapp_name/gaia dxyapp_version/4.18.0 dxyapp_system_version/10 dxyapp_client_id/47f66a687941405cb239e6647af0d7ec dxyapp_ac/3e1876d3-d315-4e15-8350-e9a2ed0576ac dxyapp_sid/e9bd3413-d660-4270-bc99-01188898539a
    fun getWebViewUserAgent(): String {
        val userAgent = try {
            WebSettings.getDefaultUserAgent(context);
        } catch (t: Throwable) {
            System.getProperty("http.agent")
        }

        return formatChinese(userAgent + " " + userAgentDefault() )
    }


    fun userAgentDefault(): String {
        try {
            val appName = "VastchainApp"
            val appCode = getAppVersionCode()
            return "$appName/${appCode}"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }


    // 提前初始化UserAgent，减少闪屏等待时间
    fun preLoadDefaultUserAgent() {
        WebSettings.getDefaultUserAgent(context)
    }

    fun formatChinese(str: String?): String {
        val sb = StringBuffer()
        str?.forEach {
            if (it <= '\u001f' || it >= '\u007f') {
                try {
                    sb.append(String.format("\\u%04x", it.toInt()))
                } catch (e: Exception) {
                    sb.append("")
                }
            } else {
                sb.append(it)
            }
        }
        return sb.toString()
    }


    fun getAppVersionName(): String {
        if (TextUtils.isEmpty(versionName)) {
            versionName = ""
            try {
                val pi = context.packageManager.getPackageInfo(context.packageName, 0)
                versionName = pi.versionName ?: ""
            } catch (ignored: Exception) {
            }
        }
        return versionName!!
    }

    fun getAppVersionCode(): Int {
        if (versionCode == 0) {
            try {
                val pi = context.packageManager.getPackageInfo(context.packageName, 0)
                versionCode = pi.versionCode
                return versionCode
            } catch (ignored: Exception) {
            }
        }
        return versionCode
    }


    fun getOSVersion(): String {
        if (TextUtils.isEmpty(appOSVersion)) {
            appOSVersion = android.os.Build.VERSION.RELEASE ?: "unknown"
        }
        return formatChinese((appOSVersion!!))
    }


    fun getMetaData(name: String): String {
        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            return appInfo.metaData.getString(name).orEmpty()
        } catch (ignored: Exception) {
        }
        return ""
    }

}