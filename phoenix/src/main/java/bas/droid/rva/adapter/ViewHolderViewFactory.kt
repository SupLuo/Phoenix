package bas.droid.rva.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun interface ViewHolderItemViewFactory {

    fun createItemView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): View


    companion object DEFAULT : ViewHolderItemViewFactory {

        override fun createItemView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): View {
            return inflater.inflate(viewType, parent, false)
        }

        /**
         * 根据明确的布局id创建构造器
         */
        @JvmStatic
        fun create(@LayoutRes layoutId: Int): ViewHolderItemViewFactory {
            return ViewHolderItemViewFactory { inflater, parent, _ ->
                createItemView(inflater, parent, layoutId)
            }
        }
    }
}

