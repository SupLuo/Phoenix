package droid.rva.animation

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

/**
 * 新的item从上往下滑入，旧的item从下往上滑入
 *
 * 适合场景：
 * a、横向滚动列表
 *
 * b、【反向垂直排布】的列表(推荐场景) ：即LayoutManager放置item的顺序是从下往上：比如LinearLayoutManager或者GridLayoutManager设置reverseLayout = true的场景

 * 不推荐使用场景：
 *      【正向垂直排布】的列表（比如通常情况下使用LinearLayoutManager或者GridLayoutManager的场景）
 */
class TopSlideInReversibleAnimation @JvmOverloads constructor(
    duration: Long = 400L,
    interpolator: Interpolator = DecelerateInterpolator(1.3f),
    withAlpha: Boolean = true
) : YReversibleSlideInAnimation(-1f, duration, interpolator, withAlpha)