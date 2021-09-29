package ltd.vastchain.http

import android.util.Log
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author huang
 * @ProjectName student
 * @Copyright Hangzhou ShuoChuang Technology Co.,Ltd All Right Reserved
 * @Description 这里是对文件的描述
 * @data 2017/9/19
 * @note 这里写文件的详细功能和改动
 * @note
 */
object HttpsFactory {

    /**
     * 添加https认证
     *
     * @param builder
     */
    @JvmStatic
    fun sslSocketFactory(builder: OkHttpClient.Builder) {
        try {
            val sslContext = SSLContext.getInstance("TLS")
            val trustManager: X509TrustManager = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return null
                }
            }
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            builder.sslSocketFactory(sslContext.socketFactory, trustManager)
            builder.hostnameVerifier { _: String?, _: SSLSession? -> true }
        } catch (e: Exception) {
            Log.e("http", e.message ?: "")
        }
    }
}