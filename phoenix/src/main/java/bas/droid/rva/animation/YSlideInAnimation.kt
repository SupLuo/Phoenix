package droid.rva.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

/**
 * 竖向滑入动画
 * 比较适合垂直单例滚动列表
 */
abstract class YSlideInAnimation internal constructor(
    private val duration: Long = 400L,
    private val interpolator: Interpolator = DecelerateInterpolator(1.3f),
    private val withAlpha: Boolean = true
) : ItemAnimation {

    /**
     * @param flag 方向：如果从上往下 则为-1f，从下往上则为1f
     */
    protected fun createAnimator(view: View, flag: Float = -1f): Animator {
        val animator = ObjectAnimator.ofFloat(view, "translationY", flag * view.measuredHeight, 0f)
        return if (withAlpha) {
            val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            AnimatorSet().also {
                it.duration = duration
                it.interpolator = interpolator
                it.playTogether(animator, alpha)
            }
        } else {
            animator.also {
                it.duration = duration
                it.interpolator = interpolator
            }
        }
    }
}