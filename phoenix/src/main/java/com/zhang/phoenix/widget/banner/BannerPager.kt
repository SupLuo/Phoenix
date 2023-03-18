package com.zhang.phoenix.widget.banner

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import bas.droid.dimen
import bas.droid.dip
import bas.droid.layoutInflater
import com.zhang.phoenix.R
import com.zhang.phoenix.net.model.BannerApiModel
import com.zhang.phoenix.widget.banner.internal.BannerViewPager
import com.zhang.phoenix.widget.banner.internal.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle

/**
 * Created by Lucio on 2021/3/12.
 */
class BannerPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BannerViewPager<BannerApiModel>(context, attrs, defStyleAttr) {

    private val mAdapter: Adapter

    init {
        val viewPager2 = findViewById(com.zhpan.bannerview.R.id.vp_main) as? ViewPager2
        viewPager2?.let {
            it.isFocusable = false
            it.isFocusableInTouchMode = false
        }
        //循环播放
        setCanLoop(true)
        setAutoPlay(false)
//        //自动播放
//        setAutoPlay(true)
//        //5s切换
//        setInterval(getIntervalTime())
        //动画切换时间
        setScrollDuration(600)
        //禁止用户手动滑动
        setUserInputEnabled(false)
        setIndicatorStyle(IndicatorStyle.DASH)
        setIndicatorSlideMode(IndicatorSlideMode.WORM)
        setIndicatorSliderGap(dimen(R.dimen.pnx_view_margin_small))
        setIndicatorSliderColor(Color.parseColor("#4DFFFFFF"),
            ContextCompat.getColor(context, R.color.pnx_text_color_primary)
        )
        val iWidth = dip(6)
        setIndicatorSliderWidth(iWidth.toInt(), (iWidth*3).toInt())
        setIndicatorGravity(IndicatorGravity.CENTER)
        mAdapter = Adapter(context)
        adapter = mAdapter
    }

    fun replaceAll(data: List<BannerApiModel>?) {
        refreshData(data.orEmpty())
    }

    fun getCurrentData(): BannerApiModel? {
        return data.getOrNull(currentItem)
    }

    /**
     * Created by Lucio on 2021/3/12.
     */
    private inner class Adapter(ctx: Context) : BaseBannerAdapter<BannerApiModel>() {

        private val mLayoutInflater = ctx.layoutInflater

        private val mViewHolderSet = hashSetOf<BannerViewHolder>()

        val dataSize: Int get() = mList.size

        fun getCurrentViewHolder(): BannerViewHolder? {
            return mViewHolderSet.firstOrNull {
                it.adapterPositionFixed == this@BannerPager.currentItem
            }
        }
//
//        fun onCurrentItemChanged(current: Int) {
//            mViewHolderSet.forEach {
//                it.onCurrentSelectedPositionChanged(current)
//            }
//        }

        override fun getViewType(position: Int): Int {
            return this.mList[position].resType
        }

        override fun getLayoutId(viewType: Int): Int {
            return R.layout.pnx_home_banner_image_item
        }

        override fun createViewHolder(
            parent: ViewGroup,
            itemView: View,
            viewType: Int
        ): BaseViewHolder<BannerApiModel> {
            return BannerImageViewHolder(itemView)
        }

        override fun bindData(
            holder: BaseViewHolder<BannerApiModel>,
            data: BannerApiModel,
            position: Int,
            pageSize: Int
        ) {
            holder.itemView.tag = position
//            holder.itemView.setTag(R.id.extra_tag, holder)
            (holder as BannerViewHolder?)?.bindData(
                data,
                position,
                pageSize
            )
            mViewHolderSet.add(holder)
        }

        override fun onViewAttachedToWindow(holder: BaseViewHolder<BannerApiModel>) {
            super.onViewAttachedToWindow(holder)
            (holder as BannerViewHolder?)?.onAttachedToWindow()
        }

        override fun onViewDetachedFromWindow(holder: BaseViewHolder<BannerApiModel>) {
            super.onViewDetachedFromWindow(holder)
            (holder as BannerViewHolder?)?.onDetachedFromWindow()
            mViewHolderSet.remove(holder)
        }

    }

}