package bas.droid.rva.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by owen on 2017/7/4.
 */
class GridLayoutManagerCompat : GridLayoutManager {
    /**
     * 解决 底部还有控件，但是没加载出来，无法滚动的问题，因此在底部提供更多的高度用于提前加载底部的控件
     */
    private var mExtraLayoutSpace = -1

    constructor(context: Context?, spanCount: Int) : super(context, spanCount)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context?,
        spanCount: Int,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, spanCount, orientation, reverseLayout)

    override fun requestChildRectangleOnScreen(
        parent: RecyclerView,
        child: View,
        rect: Rect,
        immediate: Boolean,
        focusedChildVisible: Boolean
    ): Boolean {
//        if(parent instanceof TvRecyclerView) {
//            return parent.requestChildRectangleOnScreen(child, rect, immediate);
//        }
        return super.requestChildRectangleOnScreen(
            parent,
            child,
            rect,
            immediate,
            focusedChildVisible
        )
    }

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        if (mExtraLayoutSpace > 0) {
            return mExtraLayoutSpace
        }
        return super.getExtraLayoutSpace(state)
    }

    /**
     * 设置额外加载的空间
     */
    fun setExtraLayoutSpace(extraLayoutSpace: Int) {
        mExtraLayoutSpace = extraLayoutSpace
    }
}