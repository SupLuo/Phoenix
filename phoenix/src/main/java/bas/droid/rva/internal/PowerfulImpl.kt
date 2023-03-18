package bas.droid.rva.internal

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import bas.droid.rva.Configs
import bas.droid.rva.Power
import bas.droid.rva.adapter.internal.BaseAdapter
import droid.rva.animation.ItemAnimation
import droid.rva.animation.ItemAnimatorProxy

/**
 * 适配器代理
 * 基于数据适配器的情况下提供了header、footer、load state（上拉加载的各种状态管理）
 *
 * @see asPowerfulAdapter 将普通的适配器转换成本类型
 *
 * @param dataAdapter 用户的数据适配器；只需要关注自己的数据即可
 *
 * @see addHeader
 * @see setHeader
 * @see removeHeader
 * @see removeAllHeader
 * @see addFooter
 * @see setFooter
 * @see removeFooter
 * @see removeAllFooter
 * @see addLoadStateFooter 添加底部加载更多指示器
 *
 * @see setItemAnimation 设置item动画
 * @see itemAnimationEnable
 * @see itemAnimationFirstOnly
 *
 */
internal class PowerfulImpl internal constructor(
    private val configs: Configs = Configs()
) : Power {

    private lateinit var dataAdapter: RecyclerView.Adapter<*>

    /**
     * 真正对外访问的适配器
     */
    private lateinit var adapter: ConcatAdapter

    /**
     * item动画
     */
    private val itemAnimationProxy = ItemAnimatorProxy()

    /**
     * 启用或禁用item动画
     */
    override var itemAnimationEnable: Boolean
        get() = itemAnimationProxy.animationEnable
        set(value) {
            itemAnimationProxy.animationEnable = value
        }

    /**
     * item动画是否只执行一次
     */
    override var itemAnimationFirstOnly: Boolean
        get() = itemAnimationProxy.isAnimationFirstOnly
        set(value) {
            itemAnimationProxy.isAnimationFirstOnly = value
        }

    /**
     * 绑定适配器，进行初始化
     */
    internal fun bindDataAdapter(dataAdapter: RecyclerView.Adapter<*>) {
        this.dataAdapter = dataAdapter
        this.adapter = ConcatAdapter(dataAdapter)
        (dataAdapter as? BaseAdapter)?.addAdapterListener(object : BaseAdapter.AdapterListener {
            override fun onAttachedToWindow(viewHolder: RecyclerView.ViewHolder) {
                super.onAttachedToWindow(viewHolder)
                itemAnimationProxy.onViewAttachedToWindow(viewHolder)
            }

            override fun onDetachedFromWindow(viewHolder: RecyclerView.ViewHolder) {
                super.onDetachedFromWindow(viewHolder)
                itemAnimationProxy.onViewDetachedFromWindow(viewHolder)
            }
        })
    }

    override fun setItemAnimation(
        @ItemAnimation.DefaultAnimation animationType: Int,
        firstOnly: Boolean
    ): Power {
        itemAnimationProxy.setAnimation(animationType, firstOnly)
        return this
    }

    override fun setItemAnimation(
        animation: ItemAnimation,
        firstOnly: Boolean
    ): Power {
        itemAnimationProxy.setAnimation(animation, firstOnly)
        return this
    }

    fun apply(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
    }

}