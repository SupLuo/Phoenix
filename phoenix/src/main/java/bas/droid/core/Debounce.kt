/**
 * 防抖动事件
 */
@file:JvmName("DebounceKt")

package bas.droid.core

import android.view.View

const val DEFAULT_CLICK_THRESHOLD = 300L


/**
 * 防抖动点击，效果等同于[debounceClickFlow]
 *
 * 没有使用[JvmOverloads]方式提供扩展，不方便调用
 */
fun View.onDebounceClick(
    click: (View) -> Unit
) {
    onDebounceClick(DEFAULT_CLICK_THRESHOLD, click)
}

/**
 * @param threshold 限定时间
 */
fun View.onDebounceClick(
    threshold: Long,
    click: (View) -> Unit
) {
    setOnClickListener(ThrottleClickListener(threshold, click))
}
