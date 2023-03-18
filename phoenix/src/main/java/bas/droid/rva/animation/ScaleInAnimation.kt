package droid.rva.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.FloatRange

/**
 * 缩放动画
 * 适合任意的LayoutManager
 * @param fromScale 开始的缩放系数
 * @param endScale 结束的缩放系数
 */
class ScaleInAnimation @JvmOverloads constructor(
    @FloatRange(
        from = 0.0,
        to = 1.0
    )
    private val fromScale: Float = 0.5f,

    @FloatRange(
        from = 0.0,
        to = 1.0
    )
    private val endScale: Float = 1f,
    private val duration: Long = ItemAnimation.DURATION,
    private val interpolator: Interpolator = DecelerateInterpolator()
) : ItemAnimation {

    override fun onItemEntrantAnimation(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", fromScale, endScale)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", fromScale, endScale)
        val animatorSet = AnimatorSet().also {
            it.duration = duration
            it.interpolator = interpolator
            it.playTogether(scaleX, scaleY)
        }
        animatorSet.start()
    }
}