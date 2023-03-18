package bas.droid.rva.adapter.internal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.setItemViewType
import bas.droid.rva.PowerfulViewHolder
import bas.droid.rva.adapter.ItemPresenter
import bas.droid.rva.adapter.ViewHolderFactory
import bas.droid.rva.adapter.ViewHolderItemViewFactory
import bas.droid.rva.adapter.ViewTypeProvider
import java.lang.reflect.Modifier

/**
 * adapter帮助类：用于adapter内部数据集管理、item创建、绑定等
 */
open class AdapterHelper {

    companion object {

        /** 是否启用DataBinding */
        private val dataBindingEnable: Boolean by lazy {
            try {
                Class.forName("androidx.databinding.DataBindingUtil")
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    private var inflater: LayoutInflater? = null

    var recyclerView: RecyclerView? = null

    /**
     * 是否优先采用databinding的布局加载方式：默认如果项目开启了dataBinding，那么则会启用
     * 一般不用修改该配置：如果您的item布局没有使用databinding的写法，那么可以主动设置此配置为false，可以减少一次binding的查询动作：不过这个查询并不影响效率
     */
    @Deprecated(message = "禁用该方法：binding的查询并不影响效率")
    var useDataBinding: Boolean = dataBindingEnable

    /**
     * itemPresenter集合（数据类型为具体Class的情况）
     */
    @PublishedApi
    internal val itemPresenters: MutableMap<Class<*>, ItemPresenter<Any>> = mutableMapOf()

    /**
     * itemPresenter集合（数据类型为Interface的情况）
     */
    @PublishedApi
    internal val interfaceItemPresenter: MutableMap<Class<*>, ItemPresenter<Any>> = mutableMapOf()

    /**
     * item创建器，通过view type 找到对应的构造器
     */
    private val itemCreators: MutableMap<Int, ViewHolderFactory> = mutableMapOf()

    /**
     * 添加类型（[1对1模型][M]与[layoutId]一一对应）：该方法会将[layoutId]作为[RecyclerView.Adapter.getItemViewType]的返回值
     * 即数据类型[M]返回的ItemViewType为[layoutId]
     * @param M item对应的数据类型
     * @param layoutId item对应的布局id,会将该layoutId作为item view type
     */
    inline fun <reified M> addType(@LayoutRes layoutId: Int) {
        addType<M>(layoutId, ViewHolderFactory)
    }

    /**
     * 添加类型（[1对1模型][M]与[itemViewType]一一对应）：该方法会将[itemViewType]作为[RecyclerView.Adapter.getItemViewType]的返回值，并且通过自定义的[factory]创建ViewHolder
     * @param M item对应的数据类型
     * @param itemViewType 指定的 item view type值
     * @param factory view holder 工厂
     */
    inline fun <reified M> addType(itemViewType: Int, factory: ViewHolderFactory) {
        addType<M>(ViewTypeProvider.create(itemViewType), factory)
    }

    /**
     * 添加类型（[1对1模型][M]与[itemViewType]一一对应）：该方法会将[itemViewType]作为[RecyclerView.Adapter.getItemViewType]的返回值，并且通过自定义的[factory]创建ViewHolder
     * @param M item对应的数据类型
     * @param itemViewType 指定的 item view type值
     * @param factory item view 工厂
     */
    inline fun <reified M> addType2(itemViewType: Int, factory: ViewHolderItemViewFactory) {
        return addType<M>(itemViewType, ViewHolderFactory.create(factory))
    }

    /**
     * 添加类型（多对多模型）：该方法会将[typeProvider]返回的item view type 作为 view holder 的布局 id进行加载
     * 注意：[typeProvider]返回的值应该是布局资源id
     * @param M item对应的数据类型
     * @param typeProvider item view type 提供器，其返回值应该布局id
     */
    inline fun <reified M> addType(typeProvider: ViewTypeProvider<M>) {
        return addType<M>(typeProvider, ViewHolderFactory)
    }

    /**
     * 添加类型（完全自定义）
     */
    inline fun <reified M> addType(typeProvider: ViewTypeProvider<M>, factory: ViewHolderFactory) {
        if (Modifier.isInterface(M::class.java.modifiers)) {
            addInterfaceType(ItemPresenter.create(typeProvider, factory))
        } else {
            addType(ItemPresenter.create(typeProvider, factory))
        }
    }

    inline fun <reified M> addType(itemPresenter: ItemPresenter<M>) {
        val cls = M::class.java
        if(Modifier.isInterface(M::class.java.modifiers)){
            interfaceItemPresenter[M::class.java] = itemPresenter as ItemPresenter<Any>
        }else{
            itemPresenters[cls] = itemPresenter as ItemPresenter<Any>
        }
    }

    inline fun <reified M> addInterfaceType(itemPresenter: ItemPresenter<M>) {
        interfaceItemPresenter[M::class.java] = itemPresenter as ItemPresenter<Any>
    }

    fun getItemViewType(model: Any, position: Int): Int? {
        val modelClass: Class<*> = model.javaClass
        val itemPresenter =
            itemPresenters[modelClass] ?: interfaceItemPresenter.firstNotNullOfOrNull {
                if (it.key.isAssignableFrom(modelClass)) it.value else null
            }
        return itemPresenter?.let {
            val itemViewType = itemPresenter.typeProvider.getItemViewType(model, position)
            itemCreators[itemViewType] = itemPresenter.factory
            itemViewType
        }
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PowerfulViewHolder {
        val inflater = (this.inflater ?: LayoutInflater.from(parent.context))
        val vh =
            itemCreators[viewType]?.createViewHolder(inflater, parent, viewType)
                ?: ViewHolderFactory.createViewHolder(inflater, parent, viewType)
        vh.setItemViewType(viewType)
        return vh
    }

    fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        inflater = LayoutInflater.from(recyclerView.context)
        this.recyclerView = recyclerView
    }

    fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        inflater = null
        this.recyclerView = null
    }

}