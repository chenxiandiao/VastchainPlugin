package ltd.vastchain.jsbridge

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import ltd.vastchain.jsbridge.BuildConfig
//import com.github.lzyzsd.library.BuildConfig


/**
 * Created by admin on 2021/9/10.
 */
class CoreWebView: WebView {

	constructor(context: Context) : super(context) {
		initView()
	}

	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		initView()
	}

	constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
		initView()
	}

	private fun initView() {
		setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
		setWebSettings()
		isNestedScrollingEnabled = false
	}

	private fun setWebSettings() {
		val webSettings = this.settings
		webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
		webSettings.defaultTextEncodingName = "UTF-8"
		webSettings.loadWithOverviewMode = true
		webSettings.useWideViewPort = true
		webSettings.textZoom = 100
		//允许js代码
		webSettings.javaScriptEnabled = true
		webSettings.javaScriptCanOpenWindowsAutomatically = true
		//允许SessionStorage/LocalStorage存储
		webSettings.domStorageEnabled = true
		//自动加载图片
		webSettings.loadsImagesAutomatically = true
		webSettings.mediaPlaybackRequiresUserGesture = true

		//UserAgent
//		val userAgent = StringBuilder()
//		userAgent.append(AppUtils.getWebViewUserAgent(context))
//		webSettings.userAgentString = userAgent.toString()

		this.webViewClient = object : WebViewClient() {
			override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
				Log.e("cxd", url)
				view.loadUrl(url)
				return true
			}

//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                println("开始加载了")
//            }
//
//            //设置结束加载函数
//            override fun onPageFinished(view: WebView, url: String) {
//                println("结束加载了")
//            }
		}
	}
}