package com.zhang.phoenix.widget.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.zhang.phoenix.net.model.BannerApiModel
import com.zhpan.bannerview.BaseViewHolder

abstract class BannerViewHolder(
    val view: View
) : BaseViewHolder<BannerApiModel>(view) {

    /**
     * banner页数
     */
    protected var pageCount: Int = 1
        private set

    /**
     * 数据索引
     */
    var dataIndex: Int = -1
        private set

    /**
     * 当前数据
     */
    protected var data: BannerApiModel? = null

    /**
     * 当前ViewHolder在适配器中的位置
     */
    val adapterPositionFixed: Int get() = dataIndex

    /**
     * 是否已添加到窗口
     */
    protected var isAttachedToWindow: Boolean = false
        private set

    constructor(
        inflater: LayoutInflater,
        layoutId: Int,
        parent: ViewGroup?
    ) : this(inflater.inflate(layoutId, parent, false))


    /**
     * 绑定ViewHolder数据
     */
    protected abstract fun bindDataInternal(data: BannerApiModel, position: Int, pageSize: Int)

    final override fun bindData(data: BannerApiModel, position: Int, pageSize: Int) {
        pageCount = pageSize
        dataIndex = position
        this.data = data
        bindDataInternal(data, position, pageSize)
    }

    open fun onAttachedToWindow() {
        isAttachedToWindow = true
    }

    open fun onDetachedFromWindow() {
        isAttachedToWindow = false
    }

    protected inline fun <T : View> getView(@IdRes id: Int): T? {
        return findViewById(id)
    }
}

