package bas.droid.rva.adapter.internal

import android.annotation.SuppressLint
import androidx.annotation.IntRange

/**
 * adapter帮助类：用于adapter内部数据集管理、item创建、绑定等
 */
class CommonAdapterHelper : AdapterHelper() {

    var data = mutableListOf<Any>()

    inline fun getItemCount(): Int {
        return data.size
    }

    inline fun <M> getModel(position: Int): M {
        return getItem(position) as M
    }

    inline fun getItem(position: Int): Any {
        return data[position]
    }

    /**
     * 替换所有数据
     */
    fun replaceAll(models: List<Any>){
        data.clear()
        data.addAll(models)
    }

    /**
     * 添加新的数据
     * @param models 被添加的数据
     * @param index 插入到[models]指定位置, 如果index超过[models]长度则会添加到最后
     * @return 开始变化的位置
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addModels(
        models: List<Any>, @IntRange(from = -1) index: Int = -1
    ): Int {
        if (models.isEmpty()) return -1
        val itemCount = getItemCount()
        val fromIndex = if (index in 0..itemCount) index else itemCount
        data.addAll(fromIndex, models)
        return fromIndex
    }

    /**
     * 添加新的数据
     * @param model 被添加的数据
     * @param index 插入到[data]指定位置, 如果index超过[data]长度则会添加到最后
     * @return 开始变化的位置
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addModel(model: Any, @IntRange(from = -1) index: Int = -1): Int {
        val itemCount = getItemCount()
        val fromIndex = if (index in 0..itemCount) index else itemCount
        data.add(fromIndex, model)
        return fromIndex
    }

    /**
     * 移除最后一项数据
     * @return 改变的位置
     */
    fun removeLastModel(): Int {
        if (data.isEmpty())
            return -1
        val changedIndex = data.size - 1
        data.removeLast()
        return changedIndex
    }

    /**
     * @return 清除的数据个数
     */
    fun clearModels(): Int {
        val size = data.size
        data.clear()
        return size
    }

    fun getItemViewType(position: Int): Int? {
        return getItemViewType(getItem(position), position)
    }
}