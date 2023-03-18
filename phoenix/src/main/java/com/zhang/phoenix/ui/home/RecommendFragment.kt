package com.zhang.phoenix.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import bas.droid.dimen
import bas.droid.dpInt
import bas.droid.rva.adapter.ItemPresenter
import bas.droid.rva.linear
import bas.droid.rva.requirePowerAdapter
import bas.droid.rva.setup
import com.zhang.phoenix.R
import com.zhang.phoenix.app.loadPhx
import com.zhang.phoenix.base.BaseFragment
import com.zhang.phoenix.net.model.*
import com.zhang.phoenix.ui.home.uistate.HomeMetroRow
import com.zhang.phoenix.widget.ItemDecorationLinearColor
import com.zhang.phoenix.widget.LeanbackRecyclerView
import com.zhang.phoenix.widget.banner.BannerLayout
import kotlinx.coroutines.flow.collectLatest

class RecommendFragment : BaseFragment() {

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    private var interaction: HomeActivityInteraction? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        private var lastCheckTime: Long = 0

        private fun doTitleBarVisibilityCheck() {
            lastCheckTime = System.currentTimeMillis()
            interaction?.let {
                val isVisible = it.isTitleBarVisible
                val scrollOffset = recyclerView.computeVerticalScrollOffset()
                if (isVisible && scrollOffset > 60) {
                    it.setTitleBarVisibility(false)
                } else if (!isVisible && scrollOffset == 0) {
                    it.setTitleBarVisibility(true)
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

            Log.d(
                "Recycler", "onScrollStateChanged:($newState)" +
                        " computeVerticalScrollRange = ${recyclerView.computeVerticalScrollRange()} " +
                        " computeVerticalScrollExtent=${recyclerView.computeVerticalScrollExtent()}" +
                        " computeVerticalScrollOffset=${recyclerView.computeVerticalScrollOffset()}"
            )
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                recyclerView.computeVerticalScrollOffset();
//            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                doTitleBarVisibilityCheck()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            Log.d("Recycler", "onScrolled:(dx=$dx,dy=$dy)")
            if (System.currentTimeMillis() - lastCheckTime > 1000) {
                doTitleBarVisibilityCheck()
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = context as? HomeActivityInteraction
    }

    override fun onDetach() {
        super.onDetach()
        interaction = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!::recyclerView.isInitialized){
            createContentView(inflater, container, savedInstanceState)
            initView(recyclerView,savedInstanceState)
        }
        return recyclerView
    }

    private fun createContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recyclerView = LeanbackRecyclerView(inflater.context).also {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            it.setPadding(0,0,0,32.dpInt)
            it.clipToPadding = false
            it.overScrollMode = View.OVER_SCROLL_NEVER
            val itemMargin = dimen(R.dimen.pnx_card_margin_exclude_2fb)
            if (itemMargin > 0)
                it.addItemDecoration(
                    ItemDecorationLinearColor(itemMargin, Color.TRANSPARENT)
                )
//            it.addOnScrollListener(scrollListener)
//            it.layoutManager = V7LinearLayoutManager(requireContext())
            it.linear(extraLayoutSpace = 240)
        }


        recyclerView.setup {
            addType<HomeMetroRow>(R.layout.pnx_home_row_metro) { inflater, parent, viewType ->
                MetroViewHolder(
                    inflater,
                    viewType,
                    parent
                )
            }
            addType<HomeLatestRow>(R.layout.pnx_home_row_latest) { inflater, parent, viewType ->
                LatestViewHolder(
                    inflater,
                    viewType,
                    parent
                )
            }
            addType<HomePuzzle>(R.layout.pnx_home_row_puzzle5_card)
            addType<HomeZhuantiRow>(R.layout.pnx_home_row_weight2_card)
            addInterfaceType<HomeWeightRow>(ItemPresenter.create(typeProvider = { model, _ ->
                model.layoutId
            }))

            addInterfaceType<HomeRow>(ItemPresenter.create(typeProvider = { model, _ ->
                when (model.type) {
                    HomeRow.TITLE -> {
                        R.layout.pnx_home_row_title
                    }
                    HomeRow.NEWEST_TITLE -> {
                        R.layout.pnx_home_row_title_latest
                    }
                    HomeRow.CATEGORY -> {
                        R.layout.pnx_home_row_categories
                    }
                    HomeRow.END -> {
                        R.layout.pnx_home_row_footer
                    }
                    else -> throw IllegalArgumentException("")
                }
            }
            ))

            onItemBind {
                val data = getModel<Any>()
                when (data) {
                    is HomeTitleRow -> {
                        setText(R.id.title_view, data.title)
                    }
                    is HomeMetroRow -> {
                        getViewOrNull<BannerLayout>(R.id.metro_banner)?.replaceAll(data.banners)
                    }
                    is HomeCategoryRow -> {
                        getViewOrNull<ImageView>(R.id.image1)?.loadPhx(data.categories[0].image)
                        getViewOrNull<ImageView>(R.id.image2)?.loadPhx(data.categories[1].image)
                        getViewOrNull<ImageView>(R.id.image3)?.loadPhx(data.categories[2].image)
                        getViewOrNull<ImageView>(R.id.image4)?.loadPhx(data.categories[3].image)
                        getViewOrNull<ImageView>(R.id.image5)?.loadPhx(data.categories[4].image)
                        getViewOrNull<ImageView>(R.id.image6)?.loadPhx(data.categories[5].image)
                    }
                    is HomeMediaWeightRow -> {
                        when (data.layoutId) {
                            R.layout.pnx_home_row_weight3_card -> {
                                getViewOrNull<ImageView>(R.id.weight3_image1)?.loadPhx(data.medias[0].images[0])
                                getViewOrNull<ImageView>(R.id.weight3_image2)?.loadPhx(data.medias[1].images[0])
                                getViewOrNull<ImageView>(R.id.weight3_image3)?.loadPhx(data.medias[2].images[0])
                            }
                            R.layout.pnx_home_row_weight4_card -> {
                                getViewOrNull<ImageView>(R.id.weight4_image1)?.loadPhx(data.medias[0].images[0])
                                getViewOrNull<ImageView>(R.id.weight4_image2)?.loadPhx(data.medias[1].images[0])
                                getViewOrNull<ImageView>(R.id.weight4_image3)?.loadPhx(data.medias[2].images[0])
                                getViewOrNull<ImageView>(R.id.weight4_image4)?.loadPhx(data.medias[3].images[0])
                            }
                        }
                    }
                    is HomeCategoryWeightRow -> {
                        when (data.layoutId) {
                            R.layout.pnx_home_row_weight3_card -> {
                                getViewOrNull<ImageView>(R.id.weight3_image1)?.loadPhx(data.medias[0].image)
                                getViewOrNull<ImageView>(R.id.weight3_image2)?.loadPhx(data.medias[1].image)
                                getViewOrNull<ImageView>(R.id.weight3_image3)?.loadPhx(data.medias[2].image)
                            }
                            R.layout.pnx_home_row_weight4_card -> {
                                getViewOrNull<ImageView>(R.id.weight4_image1)?.loadPhx(data.medias[0].image)
                                getViewOrNull<ImageView>(R.id.weight4_image2)?.loadPhx(data.medias[1].image)
                                getViewOrNull<ImageView>(R.id.weight4_image3)?.loadPhx(data.medias[2].image)
                                getViewOrNull<ImageView>(R.id.weight4_image4)?.loadPhx(data.medias[3].image)
                            }
                        }
                    }
                    is HomeZhuantiRow -> {
                        getViewOrNull<ImageView>(R.id.weight2_image1)?.loadPhx(data.left.image)
                        getViewOrNull<ImageView>(R.id.weight2_image2)?.loadPhx(data.right.image)
                    }
                    is HomePuzzle -> {
                        getViewOrNull<ImageView>(R.id.weight2_image1)?.loadPhx(data.medias[0].images[0])
                        getViewOrNull<ImageView>(R.id.weight2_image2)?.loadPhx(data.medias[1].images[0])
                        getViewOrNull<ImageView>(R.id.weight3_image1)?.loadPhx(data.medias[2].images[0])
                        getViewOrNull<ImageView>(R.id.weight3_image2)?.loadPhx(data.medias[3].images[0])
                        getViewOrNull<ImageView>(R.id.weight3_image3)?.loadPhx(data.medias[4].images[0])
                    }
                    else -> {}
                }
            }
        }

        return recyclerView
    }

     fun initView(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            viewModel.homeUiState.collectLatest {
                recyclerView.requirePowerAdapter().setDiffNewData(it)
            }
        }
    }
}