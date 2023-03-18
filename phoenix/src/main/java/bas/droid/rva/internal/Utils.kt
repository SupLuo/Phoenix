package bas.droid.rva.internal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

internal inline val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)

internal inline val View.layoutInflater get() = context.layoutInflater


internal inline fun <T> T?.orTry(initialize: () -> T?): T? {
    return this ?: initialize.invoke()
}

internal inline fun <T> T?.orDefault(initialize: () -> T): T {
    return this ?: initialize.invoke()
}

/**
 * 根据Item在全局数据中的位置，获取对应的Adapter
 */
inline fun RecyclerView.Adapter<*>.getBindingAdapter(position: Int): RecyclerView.Adapter<out RecyclerView.ViewHolder>? {
    if (this is ConcatAdapter)
        return (this as ConcatAdapter).getBindingAdapter(position)
    return this
}

/**
 * 根据Item在全局数据中的位置，获取对应的Adapter以及在Adapter中的位置
 */
inline fun RecyclerView.Adapter<*>.getBindingAdapterAndPosition(position: Int): Pair<RecyclerView.Adapter<*>?, Int> {
    if (this is ConcatAdapter) {
        return (this as ConcatAdapter).getBindingAdapterAndPosition(position)
    } else {
        return Pair(this, position)
    }
}

/**
 * 根据Item在全局数据中的位置获取对应的ItemViewType
 */
inline fun RecyclerView.Adapter<*>.getBindingItemViewType(position: Int): Int {
    if (this is ConcatAdapter) {
        return (this as ConcatAdapter).getBindingItemViewType(position)
    } else {
        return getItemViewType(position)
    }
}


/**
 * 根据绝对位置获取相对位置的Adapter
 */
fun ConcatAdapter.getBindingAdapter(position: Int): RecyclerView.Adapter<out RecyclerView.ViewHolder>? {
    var pos = position
    val adapters = adapters
    for (adapter in adapters) {
        when {
            pos >= adapter.itemCount -> {
                pos -= adapter.itemCount
            }
            pos < 0 -> return null
            else -> return adapter
        }
    }
    return null
}

/**
 * 根据绝对位置获取相对位置的Adapter和相对位置
 */
fun ConcatAdapter.getBindingAdapterAndPosition(position: Int): Pair<RecyclerView.Adapter<*>?, Int> {
    var adapterPosition = position
    var targetAdapter: RecyclerView.Adapter<*>? = null
    val adapters = adapters
    for (adapter in adapters) {
        when {
            adapterPosition >= adapter.itemCount -> {
                adapterPosition -= adapter.itemCount
            }
            adapterPosition < 0 -> {
                targetAdapter = null
                break
            }
            else -> {
                targetAdapter = adapter
                break
            }
        }
    }
    return Pair(targetAdapter, adapterPosition)
}

/**
 * 根据绝对位置获取相对位置的ItemViewType
 */
fun ConcatAdapter.getBindingItemViewType(layoutPosition: Int): Int {
    var pos = layoutPosition
    val adapters = adapters
    for (adapter in adapters) {
        when {
            pos >= adapter.itemCount -> {
                pos -= adapter.itemCount
            }
            pos < 0 -> return getItemViewType(layoutPosition)
            else -> return adapter.getItemViewType(pos)
        }
    }
    return getItemViewType(layoutPosition)
}

/**
 * 根据绝对位置获取相对位置
 */
fun ConcatAdapter.getBindingAdapterPosition(layoutPosition: Int): Int {
    var pos = layoutPosition
    val adapters = adapters
    for (adapter in adapters) {
        when {
            pos >= adapter.itemCount -> {
                pos -= adapter.itemCount
            }
            pos < 0 -> return layoutPosition
            else -> return pos
        }
    }
    return layoutPosition
}
