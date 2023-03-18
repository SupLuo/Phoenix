package droid.rva.animation

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

open class YReversibleSlideInAnimation @JvmOverloads constructor(
    private val flag: Float,
    duration: Long = 400L,
    interpolator: Interpolator = DecelerateInterpolator(1.3f),
    withAlpha: Boolean = true
) : YSlideInAnimation(duration, interpolator, withAlpha) {

    override fun onItemEntrantAnimation(view: View) {
        createAnimator(view, flag).start()
    }

    override fun onItemReEntrantAnimation(view: View) {
        createAnimator(view, -flag).start()
    }

}