package ltd.vastchain.face.http

import ltd.vastchain.face.model.BasicResponse
import ltd.vastchain.face.model.Sessions
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author huang
 * @ProjectName patrol-apk
 * @Copyright Hangzhou ShuoChuang Technology Co.,Ltd All Right Reserved
 * @Description 这里是对文件的描述
 * @data 3/11/21
 * @note 这里写文件的详细功能和改动
 * @note
 */
interface IFaceApi {



    /**
     * 上传文件
     *
     * @return
     */
    @POST("/common/check_living")
    @Multipart
    fun uploadFile(
        @Part("request_id") requestId: RequestBody,
        @Part("type") type: RequestBody,
        @Part file: List<MultipartBody.Part>?,
    ): Call<BasicResponse>


    @POST("/common/face_compare")
    @Multipart
    fun faceCompare( @Part("request_id") requestId: RequestBody,
                     @Part file: MultipartBody.Part?): Call<BasicResponse>

    @GET("/common/get_request_id")
    suspend fun getRequestId(@Query("app_id") appId: String, @Query("id_card") idCard: String, @Query("user_name") userName: String): Sessions

}