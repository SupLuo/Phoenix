package bas.droid.rva.adapter

class ItemPresenter<M>(val typeProvider: ViewTypeProvider<M>, val factory: ViewHolderFactory) {

    companion object {

        /**
         * 根据布局id 创建 1对1 ItemPresenter
         */
        @JvmStatic
        fun <M> create(layoutId: Int): ItemPresenter<M> {
            return create(layoutId, ViewHolderFactory)
        }

        @JvmStatic
        fun <M> create(itemViewType: Int, factory: ViewHolderFactory): ItemPresenter<M> {
            return ItemPresenter(
                typeProvider = ViewTypeProvider.create(itemViewType),
                factory = factory
            )
        }

        @JvmOverloads
        @JvmStatic
        fun <M> create(
            typeProvider: ViewTypeProvider<M>,
            factory: ViewHolderFactory = ViewHolderFactory
        ): ItemPresenter<M> {
            return ItemPresenter(
                typeProvider = typeProvider,
                factory = factory
            )
        }
    }
}