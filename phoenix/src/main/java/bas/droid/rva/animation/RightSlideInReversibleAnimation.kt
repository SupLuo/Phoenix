package droid.rva.animation

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

/**
 * 适合场景：
 *      较佳使用场景：横向（列表排布方向：从左往右，就是最为普通的横向rv）滚动的列表
 *      通用场景：垂直滚动列表
 *
 * 新的item从右往左滑入，旧的item从左往右滑入
 *
 * 适合场景：
 * a、垂直滚动的列表
 *
 * b、【正向横向排布】的列表(推荐场景)，即LayoutManager放置item的顺序是从左往右，
 * 比如LinearLayoutManager或者GridLayoutManager设置 orientation = LinearLayoutManager.HORIZONTAL的场景

 *
 * 不推荐使用场景：
 *      【反向横向排布】的列表:比如通常情况下使用LinearLayoutManager或者GridLayoutManager设置 orientation = LinearLayoutManager.HORIZONTAL 并且 reverseLayout = true 的场景
 */
class RightSlideInReversibleAnimation @JvmOverloads constructor(
    duration: Long = 400L,
    interpolator: Interpolator = DecelerateInterpolator(1.8f),
    withAlpha: Boolean = true
) : XReversibleSlideInAnimation(1f, duration, interpolator, withAlpha)