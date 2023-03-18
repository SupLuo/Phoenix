package bas.droid.core

import android.view.View

/**
 * 放抖动点击
 */
class ThrottleClickListener(
    private val period: Long = 500,
    private var source: View.OnClickListener
) : View.OnClickListener {

    private var lastTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > period) {
            lastTime = currentTime
            source.onClick(v)
        }
    }
}