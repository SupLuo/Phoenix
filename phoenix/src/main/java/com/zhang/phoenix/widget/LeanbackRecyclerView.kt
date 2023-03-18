package com.zhang.phoenix.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.recyclerview.widget.RecyclerView

class LeanbackRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    //是否在滑动的时候分发KeyEvent，默认不分发，避免NPE导致崩溃
    private var dispatchKeyEventOnScrolling: Boolean = false

    /**
     * 设置是否在滚动的时候分发KeyEvent，默认不分发，消耗掉事件（避免出现按键不放导致滚动出现空异常）
     */
    fun setDispatchKeyEventOnScrolling(isEnable: Boolean) {
        this.dispatchKeyEventOnScrolling = isEnable
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (!dispatchKeyEventOnScrolling && isScrolling())
            return true
        return super.dispatchKeyEvent(event)
    }

    private fun isScrolling(): Boolean {
        return scrollState != SCROLL_STATE_IDLE
    }
}