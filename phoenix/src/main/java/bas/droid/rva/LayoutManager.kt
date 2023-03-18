@file:JvmName("RvaKt")
@file:JvmMultifileClass

package bas.droid.rva

import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bas.droid.rva.widget.GridLayoutManagerCompat
import bas.droid.rva.widget.LinearLayoutManagerCompat

@Px
const val DEFAULT_EXTRA_LAYOUT_SPACE = 500

/**
 * @param extraLayoutSpace 额外加载的布局空间，参见[GridLayoutManager.getExtraLayoutSpace]说明，通常用于tv开发
 */
inline fun <R : RecyclerView> R.linear(
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    stackFromEnd: Boolean = false,
    extraLayoutSpace: Int = -1
): R {
    layoutManager = LinearLayoutManagerCompat(context, orientation, reverseLayout).apply {
        this.stackFromEnd = stackFromEnd
        this.setExtraLayoutSpace(extraLayoutSpace)
    }
    return this
}

inline fun <R : RecyclerView> R.leanbackLinear(
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    stackFromEnd: Boolean = false
): R = linear(orientation, reverseLayout, stackFromEnd, 500)


/**
 * @param extraLayoutSpace 额外加载的布局空间，参见[GridLayoutManager.getExtraLayoutSpace]说明，通常用于tv开发
 */
inline fun <R : RecyclerView> R.grid(
    spanCount: Int = 1,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    extraLayoutSpace: Int = -1
): R {
    layoutManager = GridLayoutManagerCompat(context, spanCount, orientation, reverseLayout).also {
        it.setExtraLayoutSpace(extraLayoutSpace)
    }
    return this
}

inline fun <R : RecyclerView> R.leanbackGrid(
    spanCount: Int = 1,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    extraLayoutSpace: Int = DEFAULT_EXTRA_LAYOUT_SPACE
): R = grid(spanCount, orientation, reverseLayout, extraLayoutSpace)