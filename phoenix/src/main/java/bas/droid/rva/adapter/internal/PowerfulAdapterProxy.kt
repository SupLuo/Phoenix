//这里：必须保持包名，否则无法修改[canRestoreState]方法
//package androidx.recyclerview.widget
package androidx.recyclerview.widget

import android.view.ViewGroup
import bas.droid.rva.Power

/**
 * 代理已有的数据适配器，提供Power的附带能力
 */
internal class PowerfulAdapterProxy<VH : RecyclerView.ViewHolder>(
    internal val base: RecyclerView.Adapter<VH>,
    private val power: Power
) : RecyclerView.Adapter<VH>(), Power by power {

    override fun equals(other: Any?): Boolean {
        if (other is PowerfulAdapterProxy<*>) {
            return base.equals(other.base)
        } else {
            return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return base.hashCode()
    }

    override fun toString(): String {
        return base.toString()
    }

    init {

        base.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                notifyItemRangeChanged(positionStart, itemCount, payload)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }
        })

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return base.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        base.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        base.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return base.itemCount
    }

    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ): Int {
        return base.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition)
    }

    override fun getItemViewType(position: Int): Int {
        return base.getItemViewType(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        base.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        return base.getItemId(position)
    }

    override fun onViewRecycled(holder: VH) {
        base.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        return base.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        base.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        base.onViewDetachedFromWindow(holder)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        base.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        base.unregisterAdapterDataObserver(observer)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        base.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        base.onDetachedFromRecyclerView(recyclerView)
    }

    override fun setStateRestorationPolicy(strategy: StateRestorationPolicy) {
        base.stateRestorationPolicy = strategy
        super.setStateRestorationPolicy(strategy)
    }

    override fun canRestoreState(): Boolean {
        return base.canRestoreState()
    }

}