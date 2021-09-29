package ltd.vastchain.http


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException

/**
 * @author huang
 * @ProjectName RainCow
 * @Copyright Hangzhou QiYi Technology Co.,Ltd All Right Reserved
 * @Description 主要用于打印Log日志
 * @data 16/3/24
 * @note 这里写文件的详细功能和改动
 * @note
 */
class HttpInterceptor : Interceptor {
    @Throws(IOException::class)

    override fun intercept(chain: Interceptor.Chain): Response {

        //这里可以添加请求头和修改请求体等等
        val request = chain.request()
        val newBuilder = request.newBuilder()
        val requestBody = request.body()
        newBuilder.method(request.method(), requestBody)
        return chain.proceed(newBuilder.build())
    }

    companion object {
        /**
         * 添加拦截器
         *
         * @param builder
         */
        fun addInterceptor(builder: OkHttpClient.Builder) {
            builder.addInterceptor(HttpInterceptor())
        }
    }


}