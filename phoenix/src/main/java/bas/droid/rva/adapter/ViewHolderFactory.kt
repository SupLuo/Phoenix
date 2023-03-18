package bas.droid.rva.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import bas.droid.rva.PowerfulViewHolder

/**
 * view holder 构建工厂
 */
fun interface ViewHolderFactory {

    fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): PowerfulViewHolder

    /**
     * 默认工厂:将ViewType作为布局id进行加载创建ViewHolder
     */
    companion object DEFAULT : ViewHolderFactory {

        override fun createViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): PowerfulViewHolder {
            return createPowerfulViewHolder(inflater, parent, viewType)
        }

        /**
         * 根据明确的布局id创建构造器
         */
        @JvmStatic
        fun create(@LayoutRes layoutId: Int): ViewHolderFactory {
            return ViewHolderFactory { inflater, parent, _ ->
                createPowerfulViewHolder(
                    inflater,
                    parent,
                    layoutId
                )
            }
        }

        /**
         * 将指定的View转换成的item view 构造器
         *
         * 用于返回固定的View：通常用于固定view 的header 或者footer之类
         */
        @JvmStatic
        fun create(view: View): ViewHolderFactory {
            return ViewHolderFactory { _, _, _ ->
                (view.parent as? ViewGroup)?.removeView(view)
                PowerfulViewHolder(view)
            }
        }

        @JvmStatic
        fun create(itemViewFactory: ViewHolderItemViewFactory): ViewHolderFactory {
            return ViewHolderFactory { inflater, parent, viewType ->
                createPowerfulViewHolder(itemViewFactory.createItemView(inflater, parent, viewType))
            }
        }

        @JvmStatic
        fun createPowerfulViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            @LayoutRes layoutId: Int
        ): PowerfulViewHolder {
            val itemView = inflater.inflate(layoutId, parent, false)
            return createPowerfulViewHolder(itemView)
        }

        @JvmStatic
        fun createPowerfulViewHolder(itemView: View): PowerfulViewHolder {
            return PowerfulViewHolder(itemView)

        }
    }
}