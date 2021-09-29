package ltd.vastchain.face.http

import ltd.vastchain.http.HttpRequest

/**
 * Created by admin on 2021/9/28.
 */
object FaceApi {

	val faceApi: IFaceApi by lazy {
		HttpRequest.getRetrofit().create(IFaceApi::class.java)
	}

}