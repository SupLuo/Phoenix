package droid.rva.animation

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import droid.rva.animation.ItemAnimation.Companion.DURATION

/**
 * 渐变动画
 * 适合任意的LayoutManager
 * @param fromAlpha 开始的alpha
 * @param endAlpha 结束的alpha
 */
class AlphaInAnimation @JvmOverloads constructor(

    @FloatRange(
        from = 0.0,
        to = 1.0
    )
    private val fromAlpha: Float = 0f,

    @FloatRange(
        from = 0.0,
        to = 1.0
    )
    private val endAlpha: Float = 1f,

    private val duration: Long = DURATION,

    private val interpolator: Interpolator = LinearInterpolator()

) : ItemAnimation {

    override fun onItemEntrantAnimation(view: View) {
        ObjectAnimator.ofFloat(view, "alpha", fromAlpha, endAlpha).also {
            it.duration = duration
            it.interpolator = interpolator
        }.start()
    }

}