package com.zhang.phoenix.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import bas.droid.core.onDebounceClick
import bas.droid.core.tryUi
import bas.leanback.tab.TabConfigurationStrategy
import bas.lib.core.coroutines.ioInvoke
import bas.lib.core.exception.onCatch
import com.google.android.material.tabs.TabLayout
import com.zhang.phoenix.PnxAdapt
import com.zhang.phoenix.R
import com.zhang.phoenix.base.BaseActivity
import com.zhang.phoenix.databinding.PnxHomeActivityBinding
import com.zhang.phoenix.ui.home.model.TabInfo
import kotlinx.coroutines.flow.collectLatest
import xyz.doikki.videoplayer.util.toast

class HomeActivity : BaseActivity(), PnxAdapt {

    private lateinit var viewBinding: PnxHomeActivityBinding

    private val viewModel: HomeViewModel by viewModels()
    private var isAppBarExpand: Boolean = true

    private val globalFocusListener =
        ViewTreeObserver.OnGlobalFocusChangeListener { oldFocus, newFocus ->
            Log.d("AppBarLayout2","${oldFocus?.isActivated} ${oldFocus?.isSelected} ${newFocus.isActivated} ${newFocus.isSelected}")
            Log.d("AppBarLayout","isAppBarExpand=${isAppBarExpand} hasFocus=${viewBinding.appBarLayout.hasFocus()} oldFocus=${oldFocus} newFocus=${newFocus}")
            if (isAppBarExpand && !viewBinding.appBarLayout.hasFocus()) {//当appbarlayout展开的情况下，如果不包含焦点了则收拢整个AppBarLayout
                setAppBarExpanded(false)
                Log.d("AppBarLayout","set false, isAppBarExpand=${isAppBarExpand} hasFocus=${viewBinding.appBarLayout.hasFocus()} oldFocus=${oldFocus} newFocus=${newFocus}")
            }

            if(!isAppBarExpand && viewBinding.appBarLayout.hasFocus()){
                setAppBarExpanded(true)
                Log.d("AppBarLayout","set true, isAppBarExpand=${isAppBarExpand} hasFocus=${viewBinding.appBarLayout.hasFocus()} oldFocus=${oldFocus} newFocus=${newFocus}")
            }
        }

//    private val coordinatorLayoutFocusCallback =
//        GlueCoordinatorLayout.FocusSearchResultCallback { focused, result, direction ->
//            Log.d("FocusSearchResultCallback","isAppBarExpand=$isAppBarExpand direction=${direction == View.FOCUS_UP} result=${result} focused=$focused")
//            if(!isAppBarExpand && direction == View.FOCUS_UP && focused == result){
//                setAppBarExpanded(true)
//            }
//        }

    private fun setAppBarExpanded(isExpand:Boolean,animate:Boolean = true){
        isAppBarExpand = isExpand
        viewBinding.appBarLayout.setExpanded(isExpand, animate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = PnxHomeActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        window.decorView.viewTreeObserver.addOnGlobalFocusChangeListener(globalFocusListener)
//        viewBinding.glueCoordinatorLayout.setFocusSearchResultCallback(coordinatorLayoutFocusCallback)

        viewBinding.statusBar.searchBtn.onDebounceClick(::onViewClick)
        viewBinding.statusBar.historyBtn.onDebounceClick(::onViewClick)
        viewBinding.statusBar.subscribeBtn.onDebounceClick(::onViewClick)
        viewBinding.statusBar.wxBtn.onDebounceClick(::onViewClick)
        viewBinding.statusBar.noticeView.onDebounceClick(::onViewClick)
        lifecycleScope.launchWhenResumed {
            viewModel.homeUiState.collectLatest {
                println("data type is ${it.javaClass}")
                it.hashCode()
            }
        }

        lifecycleScope.launchWhenCreated {
            tryUi {
                val tabs = ioInvoke {
                    viewModel.tabs()
                }
                setupTab(tabs)
            }.onCatch {
                it.printStackTrace()
            }
        }
        viewModel.fetchHome()
    }

    private fun onViewClick(view: View) {
        view.tryUi {
            toast("点击了控件")
            viewModel.fetchHome()
            when (view.id) {
                R.id.wx_btn -> {
                    val data = viewModel.homeUiState.value
                    println(data)
                }
            }
        }
    }

    private fun setupTab(
        data: List<TabInfo>,
        firstPosition: Int = 1
    ) {
        viewBinding.viewPager.adapter = object :
            FragmentStatePagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {

            override fun getCount(): Int {
                return data.size
            }

            override fun getItem(position: Int): Fragment {
                return if (position == 1) {
                    RecommendFragment()
                } else {
                    Fragment(R.layout.pnx_developing_fragment)
                }
            }
        }

        //禁用viewpager平滑滚动
        viewBinding.tab.viewPagerSmoothScroll = false
        viewBinding.tab.setupWithViewPager(
            viewBinding.viewPager,
            object : TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    val tabCustomView = ImageView(this@HomeActivity).also {
                        it.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        it.scaleType = ImageView.ScaleType.CENTER
                        it.isDuplicateParentStateEnabled = true
                        it.setImageDrawable(data[position].drawable)
                    }
                    tab.customView = tabCustomView
                    if (firstPosition == position) {
                        viewBinding.tab.post {
//                            viewBindings.viewpager.setCurrentItem(firstPosition,false)
                            tab.select()
                            viewBinding.tab.postDelayed(100L) {
                                tab.view.requestFocus()
                            }
                        }
                    }
                }
            }
        )
//
//        viewBindings.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                brandLayout?.setTitle(tab?.text)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//        })
//        viewBindings.tab.postDelayed({ viewBindings.tab.getTabAt(firstPosition)?.select() }, 100)
    }


}