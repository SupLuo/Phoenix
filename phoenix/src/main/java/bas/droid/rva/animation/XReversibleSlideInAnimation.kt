package droid.rva.animation

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

open class XReversibleSlideInAnimation @JvmOverloads constructor(
    private val flag: Float,
    duration: Long = 400L,
    interpolator: Interpolator = DecelerateInterpolator(1.8f),
    withAlpha: Boolean = true
) : XSlideInAnimation(duration, interpolator, withAlpha) {

    override fun onItemEntrantAnimation(view: View) {
        createAnimator(view, flag).start()
    }

    override fun onItemReEntrantAnimation(view: View) {
        createAnimator(view, -flag).start()
    }
}