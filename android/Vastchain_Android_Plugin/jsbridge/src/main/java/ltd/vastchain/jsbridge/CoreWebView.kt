package ltd.vastchain.jsbridge

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import ltd.vastchain.jsbridge.util.LogUtil


//import com.github.lzyzsd.library.BuildConfig


/**
 * Created by admin on 2021/9/10.
 */
class CoreWebView: WebView {

	var handleWebViewClient: WebChromeClient? = null

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
		webSettings.javaScriptCanOpenWindowsAutomatically = true
		webSettings.allowFileAccess = true
		webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
		webSettings.setSupportZoom(true)
		webSettings.setAppCacheEnabled(true)
		webSettings.domStorageEnabled = true
		webSettings.setAppCacheMaxSize(Long.MAX_VALUE)
		webSettings.loadsImagesAutomatically = true
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

		webSettings.allowContentAccess = true
		webSettings.allowFileAccess = true

		//UserAgent
//		val userAgent = StringBuilder()
//		userAgent.append(AppUtils.getWebViewUserAgent(context))
//		webSettings.userAgentString = userAgent.toString()

//		this.webViewClient = object : WebViewClient() {
//			override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//				Log.e("cxd", url)
//				view.loadUrl(url)
//				return true
//			}
//
//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
//                println("开始加载了")
//            }
//
//            //设置结束加载函数
//            override fun onPageFinished(view: WebView, url: String) {
//                println("结束加载了")
//            }
//		}
		this.webChromeClient = object : WebChromeClient() {
			override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
				LogUtil.i("console", "$message($sourceID:$lineNumber)")
				super.onConsoleMessage(message, lineNumber, sourceID)
			}

			override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
				LogUtil.i(
					"console",
					"[" + consoleMessage.messageLevel() + "] " + consoleMessage.message() + "(" + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber() + ")"
				)
				return super.onConsoleMessage(consoleMessage)
			}

			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				handleWebViewClient?.onProgressChanged(view, newProgress)
			}

			@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
			override fun onShowFileChooser(
				webView: WebView?,
				valueCallback: ValueCallback<Array<Uri?>?>,
				fileChooserParams: FileChooserParams?
			): Boolean {
				super.onShowFileChooser(webView, valueCallback, fileChooserParams)
				return  handleWebViewClient?.onShowFileChooser(webView, valueCallback, fileChooserParams)?:true
			}
		}
	}
}