package droid.rva.animation

import android.animation.Animator
import android.util.Log
import androidx.recyclerview.widget.RecyclerView

/**
 * item动画代理,用于为[RecyclerView.Adapter]设置动画
 *
 * 如果你分不清用什么动画更好，可以调用[ItemAnimation.preferredItemAnimation]方法，会根据您当前使用的基本类型管理器选择一个较为合适的动画
 *
 * 关于动画的说明：
 * 使用诸如渐变[AlphaInAnimation]、缩放[ScaleInAnimation]一类的动画，使用范围更广，与列表形状、滚动方向没什么关联关系，因此几乎适用所有列表情况
 *
 * 如果用户设置动画只执行一次，那么其他动画也不会有什么大的问题
 *
 * 如果用户未设置动画只执行一次，那么像[XSlideInAnimation]和[YSlideInAnimation]之类的动画需要区别设置才能有更好的效果：
 * 1、如果列表垂直滚动，那么下面的item应该是使用[BottomSlideInAnimation]动画，而上面的item应该使用[TopSlideInAnimation]动画，这种情况动画过渡才更顺畅
 * 2、如果列表横向滚动，那么[LeftSlideInAnimation]和[RightSlideInAnimation]与前一种情况类似
 *
 * 因此针对第一种情况，提供了[BottomSlideInReversibleAnimation]和[TopSlideInReversibleAnimation]
 * 针对第二种情况提供了[LeftSlideInReversibleAnimation] 和 [RightSlideInReversibleAnimation]
 *
 * @param itemFilter 过滤器，用于过滤不需要执行动画的view holder
 *
 * @see animationEnable
 * @see isAnimationFirstOnly
 * @see setAnimation
 * @see onViewAttachedToWindow
 * @see onViewDetachedFromWindow
 * @see reset
 */
class ItemAnimatorProxy(
    /**
     * 是否打开动画
     */
    var animationEnable: Boolean = false,

    /**
     * 动画是否仅第一次执行
     */
    var isAnimationFirstOnly: Boolean = true,

    private val itemFilter: AnimationItemViewTypeFilter = object : AnimationItemViewTypeFilter {}

) {

    /**
     * 上次执行过动画的最大位置
     */
    private var maxAnimLayoutPosition: Int = -1

    /**
     * 上次执行动画的位置
     */
    private var lastAnimLayoutPosition: Int = -1

    //当前使用的动画
    private var itemAnimation: ItemAnimation = ItemAnimation.defaultItemAnimation()

    /**
     * 设置动画
     * 建议明确之前和之后的动画差异，做到区别处理从而达到更好的动画实现
     */
    fun setAnimation(animation: ItemAnimation, firstOnly: Boolean = isAnimationFirstOnly) {
        itemAnimation = animation
        isAnimationFirstOnly = firstOnly
    }

    /**
     * 设置内部提供的动画类型
     */
    fun setAnimation(
        @ItemAnimation.DefaultAnimation type: Int,
        firstOnly: Boolean = isAnimationFirstOnly
    ) {
        setAnimation(ItemAnimation.createDefaultAnimation(type), firstOnly)
    }

    /**
     * 在[RecyclerView.Adapter.onViewAttachedToWindow]方法中调用此方法
     */
    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        Log.d(
            "AnimatorProxy",
            "onViewAttachedToWindow layoutPosition = ${holder.layoutPosition} bindingAdapterPosition = ${holder.bindingAdapterPosition} absoluteAdapterPosition = ${holder.absoluteAdapterPosition}"
        )
        if (!itemFilter.isAnimationItemViewType(holder))
            return
        tryAnim(holder)
    }

    /**
     * 在[RecyclerView.Adapter.onViewDetachedFromWindow]方法中调用此方法
     */
    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        //清除item view的动画：通常没有任何问题，是否有必要主动调用该方法？
        holder.itemView.clearAnimation()
    }

    /**
     * 重置
     */
    fun reset() {
        maxAnimLayoutPosition = -1
        lastAnimLayoutPosition = -1
    }

    private fun tryAnim(holder: RecyclerView.ViewHolder) {
        if (!animationEnable)
            return
        val layoutPosition = holder.layoutPosition
        if (isAnimationFirstOnly) {
            //只执行一次动画
            if (maxAnimLayoutPosition < lastAnimLayoutPosition) {
                //这种情况说明是从非一次动画切换为一次动画
                maxAnimLayoutPosition = lastAnimLayoutPosition
            }
            if (layoutPosition > maxAnimLayoutPosition) {
                //说明是之后的item，肯定要执行动画的
                itemAnimation.onItemEntrantAnimation(holder.itemView)
                maxAnimLayoutPosition = layoutPosition
            }
        } else {
            //可以重复执行动画
            if (layoutPosition > lastAnimLayoutPosition) {
                itemAnimation.onItemEntrantAnimation(holder.itemView)
            } else {
                itemAnimation.onItemReEntrantAnimation(holder.itemView)
            }
            lastAnimLayoutPosition = layoutPosition
        }
    }

    /**
     * 开始执行动画方法
     * 可以重写此方法，实行更多行为
     *
     * @param anim
     * @param layoutPosition
     */
    private fun startAnim(anim: Animator, layoutPosition: Int) {
        anim.start()
    }

    interface AnimationItemViewTypeFilter {
        fun isAnimationItemViewType(holder: RecyclerView.ViewHolder): Boolean {
            return true
        }
    }

}