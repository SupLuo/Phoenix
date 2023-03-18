package bas.droid.rva.internal

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @see BaseSpecPagingLoadStateAdapter 基于[LoadStateAdapter]实现的相同功能的适配器
 *
 * @param itemAsFlow 是否作为普通的item数据流,默认false
 * 本属性仅当布局管理器是[GridLayoutManager]并且设置为false时才会生效，其效果是会将本item view的span count设置为[GridLayoutManager.getSpanCount]以达到本item垮整行的作用
 * @param isAssociatedData 适配器是否与数据变化相关：比如本适配器需要根据数据适配器的数据是否为空做出一些变化
 * @param useStrictMode 是否使用严格模式：默认不开启，严格模式就是使用者非常清楚的明白每一个item位置的改变到底是新增、移除、还是刷新
 * 参考该问题： https://juejin.cn/post/7064856244125138952#heading-14
 */
abstract class BaseSpecAdapter<VH : RecyclerView.ViewHolder>(
    protected val itemAsFlow: Boolean = false,
    protected val isAssociatedData: Boolean = true,
    protected var useStrictMode: Boolean = false
) : RecyclerView.Adapter<VH>() {

    /**
     * 整个列表数据的数量
     */
    private var cachedDataCount: Int = 0

    private lateinit var sAdapter: RecyclerView.Adapter<*>

    /**
     * 通过一些系统方法回调设置的绑定的recyclerview
     */
    protected var attachedRecyclerView: RecyclerView? = null

    //先停用此变量：是否需要一开始传入rv？感觉不用，就走内部绑定即可
    private var recyclerView: RecyclerView? = null

    protected val preferredRecycleView: RecyclerView? get() = recyclerView ?: attachedRecyclerView

    /**
     * 数据监听器用于[loadState]未发生变化，但是数据数据列表发生了变化，并且内部的某些判断与数据列表的数据变更有关联的情况下;
     * 比如[loadState]未改变，但是数据列表从有数据变成没数据 或者从没数据变为有数据，本适配器内部会根据是否有数据做一些现实操作的情况下就需要这么处理
     */
    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            dataCountMayChanged(sAdapter.itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            if (!useStrictMode) {
                dataCountMayChanged(sAdapter.itemCount)
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            if (!useStrictMode) {
                dataCountMayChanged(sAdapter.itemCount)
            } else {
                val oldCount = cachedDataCount
                val newCount = cachedDataCount + itemCount
                cachedDataCount = newCount
                onDataCountChanged(oldCount, newCount)
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            if (!useStrictMode) {
                dataCountMayChanged(sAdapter.itemCount)
            } else {
                val oldCount = cachedDataCount
                val newCount = cachedDataCount - itemCount
                cachedDataCount = newCount
                onDataCountChanged(oldCount, newCount)
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            if (!useStrictMode) {
                dataCountMayChanged(sAdapter.itemCount)
            }
        }
    }

    /**
     * 用于设置关联的数据适配器
     */
    fun setDataAdapter(adapter: RecyclerView.Adapter<*>) {
        if (!isAssociatedData) {
            return
        }
        dataCountMayChanged(adapter.itemCount)
        //解绑之前的adapter
        if (::sAdapter.isInitialized) {
            sAdapter.unregisterAdapterDataObserver(dataObserver)
        }
        sAdapter = adapter
        adapter.registerAdapterDataObserver(dataObserver)
    }

    private fun wrapSpanSizeLookup(manager: GridLayoutManager) {
        val userSpanSizeLookup = manager.spanSizeLookup
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val adapter = preferredRecycleView?.adapter
                val itemCount = this@BaseSpecAdapter.itemCount
                if (itemCount < 0 || adapter == null)//本Adapter不可见
                    return userSpanSizeLookup.getSpanSize(position)

                val bindingPare = adapter.getBindingAdapterAndPosition(position)
                bindingPare.first ?: return userSpanSizeLookup.getSpanSize(position)
                val bindingPosition = bindingPare.second
                if (bindingPosition < 0 || bindingPosition > itemCount - 1)
                    return userSpanSizeLookup.getSpanSize(position)

                val bindingItemViewType = bindingPare.first!!.getItemViewType(bindingPosition)
                return if (bindingItemViewType == this@BaseSpecAdapter.getItemViewType(
                        bindingPosition
                    )
                ) {
                    //说明是当前footer行
                    manager.spanCount
                } else {
                    userSpanSizeLookup.getSpanSize(position)
                }
            }
        }
    }

    /**
     * 一般情况不会调用该方法
     * 设置RecyclerView的布局管理器：用于在[RecyclerView]已经绑定[RecyclerView.setAdapter]之后，修改了[RecyclerView.LayoutManager]的情况;
     * 即[onAttachedToRecyclerView]已经回调之后，[RecyclerView]又修改了布局管理器，此时得不到监听，所以需要手动设置
     */
    fun setLayoutManager(manager: RecyclerView.LayoutManager?) {
        if (manager is GridLayoutManager && !itemAsFlow) {
            wrapSpanSizeLookup(manager)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.attachedRecyclerView = recyclerView
        setLayoutManager(recyclerView.layoutManager)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.attachedRecyclerView = null
    }

    private fun dataCountMayChanged(newCount: Int) {
        val oldCount = cachedDataCount
        if (newCount != oldCount) {
            cachedDataCount = newCount
            onDataCountChanged(oldCount, newCount)
        }
    }

    protected abstract fun onDataCountChanged(oldCount: Int, newCount: Int)
}