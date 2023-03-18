@file:JvmName("DiffHelperKt")

package bas.droid.rva.widget

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

fun diffCallback(
    oldData: List<Any>,
    newData: List<Any>,
    itemDiff: DiffUtil.ItemCallback<Any>
): DiffUtil.Callback = DiffCallbackProxy(oldData, newData, itemDiff)

val DEFAULT_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }
}


internal class DiffCallbackProxy(
    private val oldData: List<Any>,
    private val newData: List<Any>,
    private val itemDiff: DiffUtil.ItemCallback<Any>
) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return itemDiff.areItemsTheSame(oldData[oldItemPosition], newData[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return itemDiff.areContentsTheSame(oldData[oldItemPosition], newData[newItemPosition])
    }
}