package bas.droid.rva.adapter

/**
 * item view type 提供器
 */
fun interface ViewTypeProvider<M> {

    fun getItemViewType(model: M, position: Int): Int

    companion object{

        /**
         * 明确的 item view type值
         * @param itemViewType 设置的item view type 值，该值可以是自定义的值，也可以是布局id
         */
        fun <M> create(itemViewType:Int): ViewTypeProvider<M> {
            return ViewTypeProvider<M> { _, _ -> itemViewType }
        }
    }
}
