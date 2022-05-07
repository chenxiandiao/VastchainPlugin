package ltd.vastchain.pay

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ltd.vastchain.jsbridge.CoreJsCallback
import ltd.vastchain.jsbridge.CoreWebProgressBar
import ltd.vastchain.jsbridge.CoreWebView
import ltd.vastchain.pay.bridge.NativeBridge
import ltd.vastchain.pay.wx.WXPayBroadcastReceiver
import ltd.vastchain.pay.wx.WxPayHandler

/**
 * Created by admin on 2021/9/10.
 */


class PayWebActivity : AppCompatActivity(), PayCallback {

    companion object {
        const val URL = "http://10.155.87.121:10086/#/subPackage/warehouseManage/pages/wareHouseOperation/index?token=MmoXuOXOnvy8_r0Qstk4al1pHgdq-mmH&orgID=139723245184659456"
//		const val URL = "http://www.baidu.com"

        const val SOFT_WARE_SCAN = "softwareScan"
    }

    private val REQUEST_ENABLE_BT: Int = 100
    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124

    private var mCoreJsBridge: NativeBridge? = null
    private var webView: CoreWebView? = null

    private var mTvTitle: TextView? = null

    private var callback: CoreJsCallback? = null
    private var progressBar: CoreWebProgressBar? = null

    private var permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private var url: String = URL
    private var firstInit = true

    private var wxPayBroadcastReceiver: WXPayBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_web)

        window.statusBarColor =   ContextCompat.getColor(this
            ,android.R.color.white)
        window.decorView.systemUiVisibility =  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        mTvTitle = findViewById(R.id.tv_title)
        mTvTitle?.text = intent.getStringExtra("title")
        progressBar = findViewById(R.id.web_progressbar)

        url = intent.getStringExtra("url") ?: URL
        webView = findViewById(R.id.webView)
        mCoreJsBridge = NativeBridge(webView!!, this)
        webView?.addJavascriptInterface(mCoreJsBridge!!, "nativeBridge")

        webView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                println("开始加载了")
                progressBar?.visibility = View.VISIBLE
            }

            //设置结束加载函数
            override fun onPageFinished(view: WebView, url: String) {
                println("结束加载了")
                progressBar?.startProgressAnimation(100, true)
                var js = """
                    console.log = (function(oriLogFunc){
                            return function(str){
                                        oriLogFunc.call(console,str);
                                        //这里，在执行自定义console.log的时候，将str传递出去。
                                        window.BlueJSBridge.blueInvoke("log",str);
                                    }
                            })(console.log);
                """
                webView?.evaluateJavascript("javascript:$js") {
                }
            }
        }
        webView?.handleWebViewClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar?.startProgressAnimation(newProgress, false)

            }
        }
        initListener()
        initData()
    }

    private fun initListener() {
        findViewById<View>(R.id.v_back).setOnClickListener{
            onBackPressed()
        }
    }

    private fun initData() {
        webView?.loadUrl(url)
        val appId = intent.getStringExtra(NftManager.APP_ID)
        WxPayHandler.init(this, appId)
        registerReceiver(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        WxPayHandler.release()
        unRegisterReceiver(this, wxPayBroadcastReceiver)
    }


    override fun onBackPressed() {
        // 控制页面回退
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun registerReceiver(context: Context, callback: PayCallback) {
        wxPayBroadcastReceiver = WXPayBroadcastReceiver()
        wxPayBroadcastReceiver?.mPayCallback = callback
        val intentFilter = IntentFilter(WXPayBroadcastReceiver.ACTION)
        LocalBroadcastManager.getInstance(context).registerReceiver(wxPayBroadcastReceiver!!, intentFilter)
    }

    private fun unRegisterReceiver(context: Context, receiver: BroadcastReceiver?) {
        receiver?.let {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    override fun onPaySuccess() {
        Log.e("cxd", "支付成功")
    }

    override fun onPayFailure() {
        Log.e("cxd", "支付失败")
    }

    override fun onPayCancel() {
        Log.e("cxd", "取消支付")
    }
}