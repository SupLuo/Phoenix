package droid.rva.animation

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

/**
 *
 * 新旧item均从上往下滑入
 *
 * 适合场景：
 * a、横向滚动列表
 *
 * b、只执行一次Item动画的【反向垂直排布】的列表(推荐场景)
 *      两个条件：1、列表开启只执行一次item动画（即显示过动画的item位置不再执行动画）
 *              2、LayoutManager放置item的顺序是从下往上：比如LinearLayoutManager或者GridLayoutManager设置reverseLayout = true的场景
 *
 * 不推荐使用场景：
 *      关闭“只执行一次动画”的垂直滚动列表或【正向垂直排布】的列表（比如通常情况下使用LinearLayoutManager或者GridLayoutManager的场景）
 *
 * @see BottomSlideInAnimation 与该动画相似，两者在动画方向相反
 *
 * @see TopSlideInReversibleAnimation 推荐使用该动画，此动画无论是否开启“只执行一次动画”，都比较适合【反向垂直排布】的列表
 */
@Deprecated(
    "建议使用TopSlideInReversibleAnimation", replaceWith = ReplaceWith(
        "TopSlideInReversibleAnimation",
        ".TopSlideInReversibleAnimation"
    )
)
class TopSlideInAnimation @JvmOverloads constructor(
    duration: Long = 400L,
    interpolator: Interpolator = DecelerateInterpolator(1.3f),
    withAlpha: Boolean = true
) : YSlideInAnimation(duration, interpolator, withAlpha) {

    override fun onItemEntrantAnimation(view: View) {
        createAnimator(view, -1f).start()
    }

    override fun onItemReEntrantAnimation(view: View) {
        onItemEntrantAnimation(view)
    }

}