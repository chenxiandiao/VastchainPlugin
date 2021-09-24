package ltd.vastchain.jsbridge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar


/**
 * Web ProgressBar
 * Created by zyh on 2018/8/9
 */
class CoreWebProgressBar : ProgressBar {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    private fun initView() {
        progressDrawable = resources.getDrawable(R.drawable.core_web_progressbar)
    }

    fun startProgressAnimation(newProgress: Int, loadFinished: Boolean) {
        val animator = ObjectAnimator.ofInt(this, "progress", progress, newProgress)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (loadFinished) {
                    progress = 0
                    visibility = View.GONE
                }
            }
        })
        animator.start()
    }

}
