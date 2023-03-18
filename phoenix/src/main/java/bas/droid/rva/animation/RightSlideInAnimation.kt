package droid.rva.animation

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

/**
 * 新旧item均从右向左滑入
 *
 * 适合场景：
 * a、垂直滚动的列表
 *
 * b、只执行一次Item动画的【正向横向排布】的列表(推荐场景)
 *      两个条件：1、列表开启只执行一次item动画（即显示过动画的item位置不再执行动画）
 *              2、LayoutManager放置item的顺序是从左往右：
 *              比如LinearLayoutManager或者GridLayoutManager设置 orientation = LinearLayoutManager.HORIZONTAL的场景
 *
 * 不推荐使用场景：
 *      关闭“只执行一次动画”的【正向横向排布】列表
 *      【反向横向排布】的列表（比如通常情况下使用LinearLayoutManager或者GridLayoutManager设置 orientation = LinearLayoutManager.HORIZONTAL 并且 reverseLayout = true 的场景）
 *
 * @see LeftSlideInAnimation 与该动画相似，两者在动画方向相反
 *
 * @see RightSlideInReversibleAnimation 推荐使用该动画，此动画无论是否开启“只执行一次动画”，都比较适合【正向横向排布】的列表
 */
@Deprecated(
    "建议使用RightSlideInReversibleAnimation", replaceWith = ReplaceWith(
        "RightSlideInReversibleAnimation",
        ".RightSlideInReversibleAnimation"
    )
)
class RightSlideInAnimation @JvmOverloads constructor(
    duration: Long = 400L,
    interpolator: Interpolator = DecelerateInterpolator(1.8f),
    withAlpha: Boolean = true
) : XSlideInAnimation(duration, interpolator, withAlpha) {

    override fun onItemEntrantAnimation(view: View) {
        createAnimator(view, 1f).start()
    }

    override fun onItemReEntrantAnimation(view: View) {
        onItemEntrantAnimation(view)
    }
}