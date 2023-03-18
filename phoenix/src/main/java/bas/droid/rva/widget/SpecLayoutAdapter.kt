package droid.rva.widget

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bas.droid.rva.adapter.ViewHolderFactory
import bas.droid.rva.internal.BaseSpecAdapter
import bas.droid.rva.internal.layoutInflater

/**
 * 特别的容器适配器;可以作为适配器Header、Footer或者其他缺省Item使用
 *
 * @param itemAsFlow 是否作为普通的item数据流,默认false
 * 本属性仅当布局管理器是[GridLayoutManager]并且设置为false时才会生效，其效果是会将本item view的span count设置为[GridLayoutManager.getSpanCount]以达到本item垮整行的作用
 * @param displayWhenEmpty 显示空状态视图的时候是否可用
 */
class SpecLayoutAdapter(
    dataAdapter: RecyclerView.Adapter<*>,
    itemAsFlow: Boolean = false,
    private val displayWhenEmpty: Boolean = true
) : BaseSpecAdapter<RecyclerView.ViewHolder>(itemAsFlow) {

    companion object {

        @JvmStatic
        fun newHeaderAdapter(
            dataAdapter: RecyclerView.Adapter<*>,
            itemAsFlow: Boolean = false,
            enableWhenEmpty: Boolean = true
        ): SpecLayoutAdapter {
            return SpecLayoutAdapter(
                dataAdapter, itemAsFlow, enableWhenEmpty
            )
        }

        @JvmStatic
        fun newFooterAdapter(
            dataAdapter: RecyclerView.Adapter<*>,
            itemAsFlow: Boolean = false,
            enableWhenEmpty: Boolean = true
        ): SpecLayoutAdapter {
            return SpecLayoutAdapter(
                dataAdapter, itemAsFlow, enableWhenEmpty
            )
        }

        @JvmStatic
        private fun View.generateViewType(): Int {
            return this.hashCode()
        }

        private const val VIEW_TYPE_OFFSET = 0x666
    }

    private var isDataEmpty: Boolean = false

    private var models: MutableList<HeaderUiState> = mutableListOf<HeaderUiState>()

    /**
     * item创建器，通过view type 找到对应的构造器
     */
    private val itemCreators: MutableMap<Int, ViewHolderFactory> = mutableMapOf()

    init {
        setDataAdapter(adapter = dataAdapter)
    }

    /**
     * 根据布局资源id生成 viewtype类型
     */
    private fun generateViewTypeForLayoutRes(@LayoutRes layoutId: Int): Int {
        return if (!itemCreators.containsKey(layoutId)) {
            layoutId
        } else {
            generateViewTypeForLayoutRes(layoutId + VIEW_TYPE_OFFSET)
        }
    }

    private class HeaderUiState private constructor(
        val itemViewType: Int,
        val viewCreator: ViewHolderFactory,
        @LayoutRes val layoutId: Int = -1,
        val view: View? = null
    ) {
        constructor(itemViewType: Int, @LayoutRes layoutId: Int) : this(
            itemViewType, ViewHolderFactory.create(layoutId) , layoutId = layoutId
        )

        constructor(view: View) : this(
            view.generateViewType(),
            ViewHolderFactory.create(view),
            view = view
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return itemCreators[viewType]!!.createViewHolder(parent.layoutInflater,parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    /**
     * 当前item是否对用户可见
     */
    fun isItemShouldDisplay(): Boolean {
        //当前已经添加子布局，并且 在为空的时候可用或者当前不为空
        return !isEmpty && (displayWhenEmpty || !isDataEmpty)
    }

    private inline val modelSize: Int get() = models.size

    /**
     * 当前是否已经添加了child view
     */
    private inline val isEmpty: Boolean get() = modelSize <= 0

    final override fun getItemCount(): Int {
        return if (isItemShouldDisplay()) {
            modelSize
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return models[position].itemViewType
    }

    override fun onDataCountChanged(oldCount: Int, newCount: Int) {
        //之前是否已显示
        val oldVisible = isItemShouldDisplay()
        isDataEmpty = newCount <= 0
        val newVisible = isItemShouldDisplay()
        if (oldVisible && !newVisible) {
            notifyItemRemoved(0)
        } else if (newVisible && !oldVisible) {
            notifyItemInserted(0)
        }
    }

    /**
     * 添加view
     * @param index 添加的位置；-1表示添加在末尾
     */
    @JvmOverloads
    fun addView(
        view: View,
        index: Int = -1
    ) {
        if (itemCreators.containsKey(view.generateViewType()))
            return
        addViewInternal(view, index)
    }

    /**
     * 添加view
     * @param index 添加的位置；-1表示添加在末尾
     * @throws IllegalStateException 已经添加，重复添加时会抛出
     */
    @Throws(IllegalStateException::class)
    @JvmOverloads
    fun addViewOrThrow(
        view: View,
        index: Int = -1
    ) {
        if (itemCreators.containsKey(view.generateViewType()))
            throw IllegalStateException("this view has added in header.")
        addViewInternal(view, index)
    }

    private fun addViewInternal(
        view: View,
        index: Int = -1
    ) {
        val childCount = models.size
        val indexToAdd =
            if (index < 0 || index > childCount) {
                childCount
            } else {
                index
            }
        val item = HeaderUiState(view)
        addItem(indexToAdd, item)
    }

    /**
     * @return
     */
    fun addView(@LayoutRes layoutId: Int): Int {
        return addView(layoutId, -1)
    }

    fun addView(@LayoutRes layoutId: Int, index: Int): Int {
        val viewType = generateViewTypeForLayoutRes(layoutId)
        val childCount = models.size
        val indexToAdd =
            if (index < 0 || index > childCount) {
                childCount
            } else {
                index
            }
        val item = HeaderUiState(viewType, layoutId)
        addItem(indexToAdd, item)
        return indexToAdd
    }

    /**
     * 修改制定位置的view
     */
    @JvmOverloads
    fun setView(
        view: View,
        @IntRange(from = 0) index: Int = 0
    ) {
        if (itemCreators.containsKey(view.hashCode()))
            throw IllegalStateException("this view has added in header.")
        if (index < 0 || index >= modelSize) {
            throw IndexOutOfBoundsException("index is smaller than 0 or it is greater than list size (= $modelSize)")
        }
        val old = models[index]
        val new = HeaderUiState(view)
        models[index] = new
        itemCreators.remove(old.itemViewType)
        itemCreators[new.itemViewType] = new.viewCreator
        if (isItemShouldDisplay()) {
            notifyItemChanged(index)
        }
    }

    /**
     * 移除指定的View
     */
    fun removeView(view: View) {
        if (isEmpty)
            return
        val index = models.indexOfFirst {
            view == it.view
        }
        if (index < 0)
            return
        val item = models[index]
        removeItem(item)
    }

    /**
     * 通过layout id去移除指定的条目
     * @param revers 是否反向查找，false则从顺序查找第一满足条件的布局，true则倒叙查找第一个满足条件的布局
     */
    fun removeView(@LayoutRes layoutId: Int, revers: Boolean = true) {
        if (isEmpty)
            return
        val index = if (revers) models.indexOfLast {
            layoutId == it.layoutId
        } else models.indexOfFirst {
            layoutId == it.layoutId
        }
        if (index < 0)
            return

        val item = models[index]
        removeItem(item)
    }

    /**
     * 移除所有view
     */
    fun removeAllView() {
        if (isEmpty)
            return
        val oldVisible = isItemShouldDisplay()
        val count = models.size
        models.clear()
        itemCreators.clear()
        if (oldVisible) {
            notifyItemRangeRemoved(0, count)
        }
    }

    private fun addItem(index: Int, item: HeaderUiState) {
        itemCreators[item.itemViewType] = item.viewCreator
        models.add(index, item)
        if (isItemShouldDisplay()) {
            notifyItemInserted(index)
        }
    }

    private fun removeItem(item: HeaderUiState) {
        val index = models.indexOf(item)
        if (index < 0)
            return
        val oldVisible = isItemShouldDisplay()
        models.removeAt(index)
        itemCreators.remove(item.itemViewType)
        if (oldVisible) {
            notifyItemRemoved(index)
        }
    }
}