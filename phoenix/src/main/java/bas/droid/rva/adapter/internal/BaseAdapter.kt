package bas.droid.rva.adapter.internal

import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import bas.droid.rva.PowerfulViewHolder
import bas.droid.rva.adapter.ItemPresenter
import bas.droid.rva.adapter.ViewHolderFactory
import bas.droid.rva.adapter.ViewHolderItemViewFactory
import bas.droid.rva.adapter.ViewTypeProvider
import droid.rva.internal.throttleClick

/**
 * view点击代码块。view参数为被点击的控件
 */
typealias ViewClick = PowerfulViewHolder.(view: View) -> Unit

/**
 * view长按点击代码块。view参数为被点击的控件
 * 返回值boolean，对应[View.OnLongClickListener]的返回值含义
 */
typealias ViewLongClick = PowerfulViewHolder.(view: View) -> Boolean

/**
 * view焦点变化代码块。
 */
typealias ViewFocusChange = PowerfulViewHolder.(view: View, hasFocus: Boolean) -> Unit

/**
 * view key变化代码块
 */
typealias ViewOnKeyListener = PowerfulViewHolder.(view: View, keyCode: Int, event: KeyEvent) -> Boolean

internal data class EventHolder<T : Function<*>>(
    val throttle: Boolean,
    val period: Long,
    val block: T
)

/**
 * 所有数据适配器的基类
 */
abstract class BaseAdapter(
    @PublishedApi
    internal open val helper: AdapterHelper,
    @LayoutRes private val layoutId: Int = -1,
) : RecyclerView.Adapter<PowerfulViewHolder>() {

    /**
     * Interface for listening to ViewHolder operations.
     */
    interface AdapterListener {
        fun onCreate(viewHolder: RecyclerView.ViewHolder) {}
        fun onBind(viewHolder: RecyclerView.ViewHolder) {}
        fun onBind(viewHolder: RecyclerView.ViewHolder, payloads: List<*>?) {
            onBind(viewHolder)
        }

        fun onUnbind(viewHolder: RecyclerView.ViewHolder) {}
        fun onAttachedToWindow(viewHolder: RecyclerView.ViewHolder) {}
        fun onDetachedFromWindow(viewHolder: RecyclerView.ViewHolder) {}
    }

    private var adapterListeners: MutableList<AdapterListener> = mutableListOf()

    /**
     * 是否优先采用databinding的布局加载方式：默认如果项目开启了dataBinding，那么则会启用
     * 一般不用修改该配置：如果您的item布局没有使用databinding的写法，那么可以主动设置此配置为false，可以减少一次binding的查询动作：不过这个查询并不影响效率
     */
    var useDataBinding: Boolean
        get() = helper.useDataBinding
        set(value) {
            helper.useDataBinding = value
        }

    private var onItemBind: (PowerfulViewHolder.() -> Unit)? = null

    /**
     * item view 点击事件
     */
    private var itemClick: EventHolder<ViewClick>? = null

    /**
     * item view 长按事件
     */
    private var itemLongClick: ViewLongClick? = null

    /**
     * item view focus 事件
     */
    private var itemFocusChange: ViewFocusChange? = null

    /**
     * item view key监听
     */
    private var itemKeyListener: ViewOnKeyListener? = null

    /**
     * 添加类型（[1对1模型][M]与[layoutId]一一对应）：该方法会将[layoutId]作为[RecyclerView.Adapter.getItemViewType]的返回值
     * 即数据类型[M]返回的ItemViewType为[layoutId]
     * @param M item对应的数据类型
     * @param layoutId item对应的布局id,会将该layoutId作为item view type
     */
    inline fun <reified M> addType(@LayoutRes layoutId: Int) {
        helper.addType<M>(layoutId)
    }

    /**
     * 添加类型（[1对1模型][M]与[itemViewType]一一对应）：该方法会将[itemViewType]作为[RecyclerView.Adapter.getItemViewType]的返回值，并且通过自定义的[factory]创建ViewHolder
     * @param M item对应的数据类型
     * @param itemViewType 指定的 item view type值
     * @param factory view holder 工厂
     */
    inline fun <reified M> addType(itemViewType: Int, factory: ViewHolderFactory) {
        helper.addType<M>(itemViewType, factory)
    }

    /**
     * 添加类型（[1对1模型][M]与[itemViewType]一一对应）：该方法会将[itemViewType]作为[RecyclerView.Adapter.getItemViewType]的返回值，并且通过自定义的[factory]创建ViewHolder
     * @param M item对应的数据类型
     * @param itemViewType 指定的 item view type值
     * @param factory item view 工厂
     */
    inline fun <reified M> addType2(itemViewType: Int, factory: ViewHolderItemViewFactory) {
        helper.addType2<M>(itemViewType, factory)
    }

    /**
     * 添加类型（多对多模型）：该方法会将[typeProvider]返回的item view type 作为 view holder 的布局 id进行加载
     * 注意：[typeProvider]返回的值应该是布局资源id
     * @param M item对应的数据类型
     * @param typeProvider item view type 提供器，其返回值应该布局id
     */
    inline fun <reified M> addType(typeProvider: ViewTypeProvider<M>) {
        helper.addType<M>(typeProvider)
    }

    /**
     * 添加类型（完全自定义）
     */
    inline fun <reified M> addType(typeProvider: ViewTypeProvider<M>, factory: ViewHolderFactory) {
        helper.addType<M>(typeProvider, factory)
    }

    inline fun <reified M> addType(itemPresenter: ItemPresenter<M>) {
        helper.addType<M>(itemPresenter)
    }

    inline fun <reified M> addInterfaceType(itemPresenter: ItemPresenter<M>) {
        helper.addInterfaceType<M>(itemPresenter)
    }

    fun onItemBind(block: (PowerfulViewHolder.() -> Unit)?) {
        this.onItemBind = block
    }

    fun addAdapterListener(listener: AdapterListener) {
        this.adapterListeners.add(listener)
    }

    fun removeAdapterListener(listener: AdapterListener) {
        this.adapterListeners.remove(listener)
    }

    fun hasAdapterListener(listener: AdapterListener): Boolean {
        return this.adapterListeners.contains(listener)
    }

    /*******Event********/

    /**
     * item view 点击事件
     * @param throttle 是否防抖动
     * @param periodMsc 防抖动的时间（毫秒），即在多少时间内只响应一次事件
     */
    fun onItemClick(
        throttle: Boolean = false,
        periodMsc: Long = 500L,
        block: ViewClick
    ) {
        this.itemClick = EventHolder(throttle, periodMsc, block)
    }

    /**
     * item view 长按事件
     */
    fun onItemLongClick(block: ViewLongClick) {
        this.itemLongClick = block
    }

    /**
     * 设置item view 焦点变化监听
     */
    fun onItemFocusChange(block: ViewFocusChange) {
        this.itemFocusChange = block
    }

    /**
     * 设置item view 按键监听
     */
    fun onItemKeyListener(block: ViewOnKeyListener) {
        this.itemKeyListener = block
    }

    inline fun <M> getModel(position: Int): M {
        return getItem(position) as M
    }

    abstract fun getItem(position: Int): Any

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return helper.getItemViewType(item, position)
            ?: if (layoutId > 0) layoutId else throw IllegalStateException(
                "please add item model type : addType<${
                    item.javaClass.name
                }>(R.layout.item) or set layoutId"
            )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PowerfulViewHolder {
        val vh = helper.onCreateViewHolder(parent, viewType)
        adapterListeners.forEach {
            it.onCreate(vh)
        }
        bindHolderEvents(vh)
        return vh
    }

    private fun bindHolderEvents(holder: PowerfulViewHolder) {
        itemClick?.let { it ->
            val (throttle, period, block) = it
            if (throttle) {
                holder.itemView.throttleClick(period) {
                    block.invoke(holder, it)
                }
            } else {
                holder.itemView.setOnClickListener {
                    block.invoke(holder, it)
                }
            }
        }

        itemLongClick?.let { listener ->
            holder.itemView.setOnLongClickListener {
                listener.invoke(holder, it)
            }
        }

        itemFocusChange?.let { listener ->
            holder.itemView.setOnFocusChangeListener { v, hasFocus ->
                listener.invoke(holder, v, hasFocus)
            }
        }

        itemKeyListener?.let { listener ->
            holder.itemView.setOnKeyListener { v, keyCode, event ->
                listener.invoke(holder, v, keyCode, event)
            }
        }
    }

    override fun onBindViewHolder(holder: PowerfulViewHolder, position: Int) {
        holder.bindModel(getModel(position))
        onItemBind?.invoke(holder)
        adapterListeners.forEach {
            it.onBind(holder)
        }
    }

    override fun onBindViewHolder(
        holder: PowerfulViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bindModel(getModel(position))
        onItemBind?.invoke(holder)
        adapterListeners.forEach {
            it.onBind(holder)
        }
        Log.d("RVA", "initialRecyclerView onBindViewHolder")
    }

    override fun onViewRecycled(holder: PowerfulViewHolder) {
        adapterListeners.forEach {
            it.onUnbind(holder)
        }
    }

    override fun onViewAttachedToWindow(holder: PowerfulViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
        adapterListeners.forEach {
            it.onAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: PowerfulViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
        adapterListeners.forEach {
            it.onDetachedFromWindow(holder)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        helper.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        helper.onDetachedFromRecyclerView(recyclerView)
    }

}