package ltd.vastchain.pay

/**
 * Created by zyh on 2018/7/9
 */
interface PayCallback {

    fun onPaySuccess()

    fun onPayFailure()

    fun onPayCancel()
}