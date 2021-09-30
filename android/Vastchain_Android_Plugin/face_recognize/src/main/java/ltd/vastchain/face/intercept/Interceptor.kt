package ltd.vastchain.face.intercept

/**
 * Created by admin on 2021/9/30.
 */
interface Interceptor {

	fun proceed(file: String, interceptChain: InterceptChain)

	fun checked(): Boolean

	fun proceedBegin()
}