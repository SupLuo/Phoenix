package droid.rva.internal

import android.view.View


internal fun View.throttleClick(period: Long = 500, block: View.OnClickListener) {
    setOnClickListener(ThrottleClickListener(period, block))
}

private class ThrottleClickListener(
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