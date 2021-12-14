package ltd.vastchain.face.intercept

/**
 * Created by admin on 2021/9/30.
 */
class InterceptChain(
	private val interceptors: List<Interceptor>,
	private val index: Int
) {

	fun proceed(file: String) {
		if (index >= interceptors.size)
			return
		val next = InterceptChain(interceptors, index + 1)
		val interpolator = interceptors[index]
		return interpolator.proceed(file, next)
	}

	fun isLast(): Boolean {
		if (index >= interceptors.size) {
			return true
		}
		return false
	}

	//procced中调用
	fun getNextInterceptor(): Interceptor? {
		if (index >= interceptors.size)
			return null
		return interceptors[index]
	}

	fun currentType(): String {
		var interceptor: Interceptor? = interceptors.firstOrNull { !it.checked() }

		return if (interceptor is VerifyInterceptor) {
			"compare/"
		} else if (interceptor is EyeInterceptor) {
			"eye/"
		} else if (interceptor is MouthInterceptor) {
			"mouth/"
		} else {
			"normal/"
		}
		return "normal/"
	}

	fun isInCompare(): Boolean {
		var interceptor: Interceptor? = interceptors.firstOrNull { !it.checked() }
		return  interceptor is VerifyInterceptor
	}
}