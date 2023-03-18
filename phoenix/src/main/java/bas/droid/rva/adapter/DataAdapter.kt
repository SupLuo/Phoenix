package bas.droid.rva.adapter

import android.annotation.SuppressLint
import androidx.annotation.IntRange
import androidx.annotation.MainThread
import androidx.recyclerview.widget.DiffUtil
import bas.droid.rva.adapter.internal.BaseAdapter
import bas.droid.rva.adapter.internal.CommonAdapterHelper
import bas.droid.rva.widget.DEFAULT_ITEM_CALLBACK
import bas.droid.rva.widget.diffCallback

open class DataAdapter(
    layoutId: Int = -1,
    override val helper: CommonAdapterHelper = CommonAdapterHelper()
) : BaseAdapter(helper, layoutId) {

    /**
     * item的差分回调
     */
    var itemDiffCallback: DiffUtil.ItemCallback<*> = DEFAULT_ITEM_CALLBACK

    var models: List<Any>
        get() = helper.data
        set(value) {
            helper.data = value.toMutableList()
            notifyDataSetChanged()
//            _data = when (value) {
//                is ArrayList -> flat(value)
//                is List -> flat(value.toMutableList())
//                else -> null
//            }
//            notifyDataSetChanged()
//            checkedPosition.clear()
//            if (isFirst) {
//                lastPosition = -1
//                isFirst = false
//            } else {
//                lastPosition = itemCount - 1
//            }
        }

    /**
     * 更改数据集，当新旧数据均不为空时采用[notifyDataSetChanged]方法进行列表刷新，该方法会导致整个列表刷新（尤其是ConcatAdapter的场景不建议用此方法）
     * 建议采用[setDiffNewData]进行差分更新。但此方法也有个弊端，在新旧数据差异较大的情况下可能体验不好（比如同个RV被几个菜单同时使用，菜单的数据差异很大，此时采用该方法刷新会导致界面有一个刷新跳动的过程）
     * @param newModels 新的数据
     * @see setDiffNewData
     */
    @MainThread
    open fun setNewData(newModels: List<Any>?) {
        val previous = helper.data.toList()
        if (previous.isEmpty()) {
            if (newModels.isNullOrEmpty())
                return
            helper.data = newModels.toMutableList()
            notifyItemRangeInserted(0, newModels.size)
            return
        }
        if (newModels.isNullOrEmpty()) {
            val oldSize = previous.size
            helper.clearModels()
            notifyItemRangeRemoved(0, oldSize)
            return
        }
        helper.replaceAll(newModels)
        notifyDataSetChanged()
    }

    /**
     * 采用差分计算更新数据
     * 比较适合同个界面的数据变化刷新处理
     * @param newModels 新的数据
     * @param detectMoves [DiffUtil.calculateDiff]
     * @see setNewData
     */
    @MainThread
    @JvmOverloads
    open fun setDiffNewData(
        newModels: List<Any>?,
        detectMoves: Boolean = true,
        commitCallback: Runnable? = null
    ) {
        val previous = helper.data.toList()
        if (previous.isEmpty()) {
            if (newModels.isNullOrEmpty()) {
                commitCallback?.run()
                return
            }
            helper.data = newModels.toMutableList()
            notifyItemRangeInserted(0, newModels.size)
            commitCallback?.run()
            return
        }

        if (newModels.isNullOrEmpty()) {
            val oldSize = previous.size
            helper.clearModels()
            notifyItemRangeRemoved(0, oldSize)
            commitCallback?.run()
            return
        }
        helper.replaceAll(newModels)
        val diffResult =
            DiffUtil.calculateDiff(
                diffCallback(
                    previous, newModels,
                    itemDiffCallback as DiffUtil.ItemCallback<Any>
                ), detectMoves
            )
        diffResult.dispatchUpdatesTo(this)
        commitCallback?.run()
    }

//    private fun mainBlock(block: Runnable) {
//        val mainLooper = Looper.getMainLooper()
//        if (Looper.myLooper() != mainLooper) {
//            helper.recyclerView?.post(block) ?: Handler(mainLooper).post(block)
//        } else {
//            block.run()
//        }
//    }
//
//    /**
//     * 使用 DiffResult 设置新实例.
//     * Use DiffResult setting up a new instance to data.
//     *
//     * @param diffResult DiffResult
//     * @param list New Data
//     */
//    fun setDiffNewData(@NonNull diffResult: DiffUtil.DiffResult, list: MutableList<T>) {
//        if (hasEmptyView()) {
//            // If the current view is an empty view, set the new data directly without diff
//            setNewInstance(list)
//            return
//        }
//        diffResult.dispatchUpdatesTo(BrvahListUpdateCallback(this))
//        this.data = list
//    }

    /**
     * 添加新的数据
     * @param models 被添加的数据
     * @param index 插入到[models]指定位置, 如果index小于0或者超过现有数据列表长度则会添加列表末尾
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addModels(
        models: List<Any>,
        @IntRange(from = -1) index: Int = -1,
        animation: Boolean = true
    ) {
        val changedIndex = helper.addModels(models, index)
        if (animation && changedIndex >= 0) {
            notifyItemRangeInserted(changedIndex, models.size)
        }
    }

    /**
     * 添加新的数据
     * @param model 被添加的数据
     * @param index 插入到[models]指定位置, 如果index超过[models]长度则会添加到最后
     * @return 开始变化的位置
     */
    fun addModel(model: Any, @IntRange(from = -1) index: Int = -1, animation: Boolean = true) {
        val changedIndex = helper.addModel(model, index)
        if (animation && changedIndex >= 0) {
            notifyItemInserted(changedIndex)
        }
    }

    fun removeLastModel(animation: Boolean = true) {
        val changedIndex = helper.removeLastModel()
        if (animation && changedIndex >= 0) {
            notifyItemRemoved(changedIndex)
        }
    }

    fun clearModels(animation: Boolean = true) {
        val count = helper.clearModels()
        if (animation && count > 0) {
            notifyItemRangeRemoved(0, count)
        }
    }

    override fun getItem(position: Int): Any {
        return helper.getItem(position)
    }

    override fun getItemCount(): Int {
        return helper.getItemCount()
    }

}