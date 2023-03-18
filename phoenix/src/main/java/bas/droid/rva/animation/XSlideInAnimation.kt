package droid.rva.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

/**
 * 横向滑入动画
 * 比较适合横向单例滚动列表
 */
abstract class XSlideInAnimation internal constructor(
    private val duration: Long = 400L,
    private val interpolator: Interpolator = DecelerateInterpolator(1.8f),
    private val withAlpha: Boolean = true
) : ItemAnimation {

    /**
     * @param flag 方向：如果从左往右 则为-1f，从右往左则为1f
     */
    protected fun createAnimator(view: View, flag: Float = -1f): Animator {
        val animator = ObjectAnimator.ofFloat(view, "translationX", flag * view.rootView.width, 0f)
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