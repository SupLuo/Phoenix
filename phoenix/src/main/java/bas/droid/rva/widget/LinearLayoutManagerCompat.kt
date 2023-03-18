package bas.droid.rva.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutManagerCompat : LinearLayoutManager{

    private var mExtraLayoutSpace = -1

    constructor(context: Context?) : super(context)

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


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