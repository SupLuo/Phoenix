package droid.rva.animation

import android.view.View
import androidx.annotation.IntDef
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * item view 动画基类
 */
interface ItemAnimation {

    /**
     * 执行item进入动画:此位置还未执行过动画，类似于列表向后滑动显示的item执行的动画
     */
    fun onItemEntrantAnimation(view: View)

    /**
     * item重新进入执行的动画：此位置之前已执行过动画，类似于列表向上滑动item进入显示的item执行的动画
     */
    fun onItemReEntrantAnimation(view: View) = onItemEntrantAnimation(view)

    companion object {

        /**
         * 动画默认时间
         */
        internal const val DURATION = 300L

        const val ALPHA = 0

        const val SCALE = 1

        const val LEFT_SLIDE = 10
        const val LEFT_REVERS_SLIDE = 11

        const val RIGHT_SLIDE = 20
        const val RIGHT_REVERS_SLIDE = 21

        const val TOP_SLIDE = 30
        const val TOP_REVERS_SLIDE = 31

        const val BOTTOM_SLIDE = 40
        const val BOTTOM_REVERS_SLIDE = 41

        /**
         * 渐变动画和缩放动画都不用区分滚动方向，因此比较适合绝大多数情况，像滑入动画需要区分方向还需要区分rv滚动方向，需要根据情况而定
         * 因此本方法提供的动画，用于不用区分方向的动画，类似于无脑动画
         */
        fun defaultItemAnimation(): ItemAnimation {
            return AlphaInAnimation()
        }

        /**
         * 根据类型创建默认的动画
         */
        fun createDefaultAnimation(@DefaultAnimation type: Int): ItemAnimation {
            return when (type) {
                LEFT_SLIDE -> {
                    LeftSlideInAnimation()
                }
                LEFT_REVERS_SLIDE -> {
                    LeftSlideInReversibleAnimation()
                }
                RIGHT_SLIDE -> {
                    RightSlideInAnimation()
                }
                RIGHT_REVERS_SLIDE -> {
                    RightSlideInReversibleAnimation()
                }
                TOP_SLIDE -> {
                    TopSlideInAnimation()
                }
                TOP_REVERS_SLIDE -> {
                    TopSlideInReversibleAnimation()
                }
                BOTTOM_SLIDE -> {
                    BottomSlideInAnimation()
                }
                BOTTOM_REVERS_SLIDE -> {
                    BottomSlideInReversibleAnimation()
                }
                SCALE -> {
                    ScaleInAnimation()
                }
                else -> {
                    AlphaInAnimation()
                }
            }
        }


        /**
         * 根据LayoutManager创建推荐的动画
         * 动画解释说明太复杂？那我来帮你做推荐
         */
        fun preferredItemAnimation(recyclerView: RecyclerView): ItemAnimation {

            with(recyclerView.layoutManager ?: return defaultItemAnimation()) {
                return when (this) {
                    is GridLayoutManager, is StaggeredGridLayoutManager -> {
                        ScaleInAnimation()
                    }
                    is LinearLayoutManager -> {
                        val isRevers = reverseLayout || stackFromEnd
                        if (orientation == LinearLayoutManager.VERTICAL) {
                            if (isRevers) {
                                TopSlideInReversibleAnimation()
                            } else {
                                BottomSlideInReversibleAnimation()
                            }
                        } else {
                            if (isRevers) {
                                LeftSlideInReversibleAnimation()
                            } else {
                                RightSlideInReversibleAnimation()
                            }
                        }
                    }
                    else -> {
                        if ( this is GridLayoutManager) {
                            ScaleInAnimation()
                        } else {
                            AlphaInAnimation()
                        }
                    }
                }
            }
        }

    }

    /**
     * 内置动画类型
     */
    @IntDef(
        ALPHA,
        SCALE,
        LEFT_SLIDE,
        LEFT_REVERS_SLIDE,
        RIGHT_SLIDE,
        RIGHT_REVERS_SLIDE,
        TOP_SLIDE,
        TOP_REVERS_SLIDE,
        BOTTOM_SLIDE,
        BOTTOM_REVERS_SLIDE
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class DefaultAnimation


}