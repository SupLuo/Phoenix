//这里：必须保持包名，否则无法修改item view type
package androidx.recyclerview.widget

fun RecyclerView.ViewHolder.setItemViewType(itemViewType: Int) {
    this.mItemViewType = itemViewType
}

