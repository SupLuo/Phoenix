package bas.droid.rva

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import bas.droid.rva.internal.orTry

open class PowerfulViewHolder : RecyclerView.ViewHolder {

    @PublishedApi
    internal var viewBinding: ViewBinding? = null

    constructor(binding: ViewBinding) : super(binding.root) {
        this.viewBinding = binding
    }

    constructor(itemView: View) : super(itemView)

    @JvmOverloads
    constructor(parent: ViewGroup, @LayoutRes layoutId: Int, attachToRoot: Boolean = false) : this(
        LayoutInflater.from(parent.context),
        layoutId,
        parent,
        attachToRoot
    )

    @JvmOverloads
    constructor(
        inflater: LayoutInflater,
        @LayoutRes layoutId: Int,
        parent: ViewGroup?,
        attachToRoot: Boolean = false
    ) : super(inflater.inflate(layoutId, parent, attachToRoot))

    private val views: SparseArray<View> = SparseArray()

    private var _data: Any? = null

    @CallSuper
    open fun bindModel(data: Any) {
        this._data = data
    }

    open fun onViewAttachedToWindow() {
    }

    open fun onViewDetachedFromWindow() {
    }

    fun <M> getModel(): M = _data as M

    /**
     * 返回匹配泛型的数据绑定对象[ViewBinding]
     */
    inline fun <reified B : ViewBinding> getBinding(): B {
        return if (viewBinding == null) {
            val method = B::class.java.getMethod("bind", View::class.java)
            val viewBinding = method.invoke(null, itemView) as B
            this.viewBinding = viewBinding
            viewBinding
        } else {
            viewBinding as B
        }
    }

    /**
     * 返回匹配泛型的数据绑定对象[ViewBinding], 如果不匹配则返回null
     */
    inline fun <reified B : ViewBinding> getBindingOrNull(): B? {
        return try {
            getBinding<B>()
        } catch (e: Throwable) {
            viewBinding as? B
        }
    }

    fun <T : View> getView(@IdRes viewId: Int): T {
        val view = getViewOrNull<T>(viewId)
        checkNotNull(view) { "No view found with id $viewId" }
        return view
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getViewOrNull(@IdRes viewId: Int): T? {
        return views.get(viewId).orTry {
            itemView.findViewById<T>(viewId)?.also {
                views.put(viewId, it)
            }
        } as T?
    }

    fun <T : View> Int.findView(): T? {
        return itemView.findViewById(this)
    }

    fun setText(@IdRes viewId: Int, value: CharSequence?): PowerfulViewHolder {
        getView<TextView>(viewId).text = value
        return this
    }

    fun setTextOrGone(@IdRes id: Int, message: CharSequence?) {
        getViewOrNull<TextView>(id)?.apply {
            visibility = if (message.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
            text = message
        }
    }
}
