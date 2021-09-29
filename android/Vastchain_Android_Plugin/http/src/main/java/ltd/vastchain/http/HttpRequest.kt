package ltd.vastchain.http

import android.util.Log

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author huang
 * @ProjectName BaseApp
 * @Copyright Hangzhou QiYi Technology Co.,Ltd All Right Reserved
 * @Description 使用Retrofit的网络请求
 * @data 16/3/24
 * @note 这里写文件的详细功能和改动
 * @note
 */
object HttpRequest {

    /**
     * retrofit对象
     */
    private var retrofit: Retrofit? = null

    fun getRetrofit(): Retrofit {
        if (retrofit == null) {

            val builder = OkHttpClient.Builder()
            builder.readTimeout(30, TimeUnit.SECONDS)
            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.writeTimeout(30, TimeUnit.SECONDS)

            //设置拦截器
//            HttpInterceptor.addInterceptor(builder)
            //https
            HttpsFactory.sslSocketFactory(builder)
            // 添加日志
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.i("http","HttpRequest：$message")
            }

            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)

            retrofit = Retrofit.Builder()
                .baseUrl("http://10.144.1.84:12222")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }


//    val faceApi: FaceApi by lazy {
//        getRetrofit().create(AliOssApi::class.java)
//    }


    /**
     * 处理异常情况
     */
    fun dealErrorBody(e: Exception) {
//        when (e) {
//            is HttpException -> {
//                try {
//                    val errorObj = GsonUtils.fromJson(
//                        e.response()?.errorBody()?.string(),
//                        HttpError::class.java
//                    )
//                    if (errorObj.code == "40101") {
//                        return
//                    }
//                    Log.e("cxd",errorObj.description)
//                } catch (ignore: Exception) {
//                    Log.e("cxd", ignore.message.toString())
//                }
//            }
//        }
    }

}