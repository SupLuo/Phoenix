package bas.droid.rva

import droid.rva.animation.ItemAnimation

interface Power {

    /**
     * 启用或禁用item动画
     */
    var itemAnimationEnable: Boolean

    /**
     * item动画是否只执行一次
     */
    var itemAnimationFirstOnly: Boolean

    /**
     * 添加item动画
     * @param animationType 内置动画类型
     */
    fun setItemAnimation(
        @ItemAnimation.DefaultAnimation animationType: Int
    ): Power = setItemAnimation(animationType, itemAnimationFirstOnly)

    /**
     * 添加item动画
     * @param animationType 内置动画类型
     * @param firstOnly 动画是否仅第一次执行
     */
    fun setItemAnimation(
        @ItemAnimation.DefaultAnimation animationType: Int,
        firstOnly: Boolean
    ): Power

    /**
     * 添加item动画
     */
    fun setItemAnimation(
        animation: ItemAnimation
    ): Power = setItemAnimation(animation, itemAnimationFirstOnly)

    /**
     * 添加item动画
     * @param firstOnly 动画是否仅第一次执行
     */
    fun setItemAnimation(
        animation: ItemAnimation,
        firstOnly: Boolean
    ): Power

}