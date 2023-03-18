package com.zhang.phoenix.widget.banner

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import bas.droid.core.onDebounceClick
import bas.droid.dpInt
import bas.leanback.effect.EffectConstraintLayout
import bas.leanback.effect.EffectParams
import bas.lib.core.exception.trySilent
import com.zhang.phoenix.R
import com.zhang.phoenix.net.model.BannerApiModel
import com.zhang.phoenix.net.model.ResType
import com.zhpan.indicator.IndicatorView

/**
 * 第三版设计：
 * 该容器内部包含一个ViewPager做图片滚动 ，包含一个VideoView做视频播放，即底部的ViewPager只做图片的轮播，当轮播的item是视频时再用播放器去播放该view
 */
class BannerLayout : EffectConstraintLayout {

    companion object {
        private const val SWITCH_INTERVAL = 6000L
        private const val ACTION_DELAY_INTERVAL = 3000L

        private const val WHAT_DELAY_NEXT = 1
        private const val WHAT_DELAY_PLAY = 2

        private fun log(message: String) {
            Log.d("BBBB", "${this.hashCode()} $message")
        }
    }

    private lateinit var pager: BannerPager
    private lateinit var pagerIndicator: IndicatorView

    //    private lateinit var player: DKVideoView
//    private lateinit var playerManager: BannerVideoViewManager
    private var isPlayerReleaseFocus: Boolean = false
    private var data: List<BannerApiModel> = emptyList()

    private val mHandler: Handler = Handler(Looper.getMainLooper(), ::handleMessage)
    private var lifecycleOwner: LifecycleOwner? = null

    private var hasSetDefaultFocus: Boolean = false

    /**
     * 是否自己处理默认焦点
     */
    var defaultFocusSetEnable: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context, attrs)
    }

    constructor(context: Context, effectParams: EffectParams) : super(context, effectParams) {
        setup(context, null)
    }

    private val onPageChangeCallback: ViewPager2.OnPageChangeCallback = object :
        ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            log("onPageSelected:$position")
            pageCallbacks.forEach {
                it.onPageSelected(position)
            }
            isPlayerReleaseFocus = false
            dispatchItemSelectedOnCallback(position)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            pageCallbacks.forEach {
                it.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            pageCallbacks.forEach {
                it.onPageScrollStateChanged(state)
            }
        }
    }

    private val pageCallbacks: MutableList<ViewPager2.OnPageChangeCallback> = mutableListOf()

    val currentData: BannerApiModel? get() = data.getOrNull(pager.currentItem)

    private val lifecycleObserver = object : DefaultLifecycleObserver {

        private var hasRemoveMessage: Boolean = false

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            bas.lib.core.exception.trySilent {
                pager.onResume()
            }
//            bas.lib.core.exception.trySilent {
//                playerManager.onResume()
//            }

            bas.lib.core.exception.trySilent {
                //当显示图片时，移除了handler信息，界面可见时补全一下handler信息，避免不会自动切换
                currentData?.let { currentData ->
                    if (hasRemoveMessage) {
                        when (currentData.resType) {
                            ResType.VIDEO -> {
                                delayToNextPage(ACTION_DELAY_INTERVAL)
                            }
                            else -> {
                                dispatchItemSelectedOnCallback(pager.currentItem)
                            }
                        }
                    }
                }
            }
            hasRemoveMessage = false
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            bas.lib.core.exception.trySilent {
                pager.onPause()
            }
//            bas.lib.core.exception.trySilent {
//                playerManager.onPause()
//            }
            trySilent {
                //当前显示的item是图片时移除handler信息：避免界面不可见时pager延迟切换到下一页
                currentData?.let { currentData ->
                    if (currentData.resType != ResType.VIDEO || isPlayerReleaseFocus) {
                        mHandler.removeCallbacksAndMessages(null)
                        hasRemoveMessage = true
                    }
                }
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            bas.lib.core.exception.trySilent {
                pager.onDestroy()
            }
//            bas.lib.core.exception.trySilent {
//                playerManager.onDestroy()
//            }
            bas.lib.core.exception.trySilent {
                mHandler.removeCallbacksAndMessages(null)
            }
        }
    }

    private fun generateLayoutParams(width: Int, height: Int): LayoutParams {
        return LayoutParams(width, height).also {
            it.leftToLeft = LayoutParams.PARENT_ID
            it.topToTop = LayoutParams.PARENT_ID
            it.rightToRight = LayoutParams.PARENT_ID
            it.bottomToBottom = LayoutParams.PARENT_ID
            if (width == 0 && height == 0) {
                it.dimensionRatio = "16:9"
            }
        }
    }

    private fun setup(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BannerLayout)
        val width = ta.getDimensionPixelSize(R.styleable.BannerLayout_banner_width, 0)
        val height = ta.getDimensionPixelSize(R.styleable.BannerLayout_banner_height, 0)
        ta.recycle()

//        player = DKVideoView(context).also {
//            it.visibility = View.INVISIBLE
//            it.setPlayerFactory(FlavorManager.getPlayerFactory())
//            it.layoutParams = generateLayoutParams(width, height)
//        }
//        playerManager = BannerVideoViewManager(player, object :
//            BannerVideoViewManager.ReleaseUserFocus {
//            override fun onReleaseUserFocus() {
//                isPlayerReleaseFocus = true
//                delayToNextPage(ACTION_DELAY_INTERVAL)
//            }
//
//            override fun getItemCount(): Int {
//                return this@BannerLayout.data.size.orDefault()
//            }
//        })
//        addView(player)

        //添加在底层
        pager = BannerPager(context).also {
            it.layoutParams = generateLayoutParams(width, height)
            it.registerOnPageChangeCallback(onPageChangeCallback)
        }

        addView(pager, 0)

        //添加在顶部
        pagerIndicator = IndicatorView(context).also {
            it.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).also { lp ->
                lp.leftToLeft = LayoutParams.PARENT_ID
                lp.bottomToBottom = LayoutParams.PARENT_ID
                lp.rightToRight = LayoutParams.PARENT_ID
                lp.leftMargin = 12.dpInt
                lp.rightMargin = lp.leftMargin
                lp.bottomMargin = lp.leftMargin / 6
            }
        }
        addView(pagerIndicator, childCount)

        //pager 设置自定义指示器
        pager.setIndicatorView(pagerIndicator)
        pager.create()
        this.onDebounceClick {
            //nothing
        }
        lifecycleOwner = context as LifecycleOwner?
        lifecycleOwner?.lifecycle?.addObserver(lifecycleObserver)
    }

    fun bindLifecycleOwner(owner: LifecycleOwner?) {
        lifecycleOwner?.lifecycle?.removeObserver(lifecycleObserver)
        lifecycleOwner = owner
        lifecycleOwner?.lifecycle?.addObserver(lifecycleObserver)
    }

    fun addOnPageChangeCallback(callback: ViewPager2.OnPageChangeCallback) {
        pageCallbacks.add(callback)
    }

    fun removeOnPageChangeCallback(callback: ViewPager2.OnPageChangeCallback) {
        pageCallbacks.remove(callback)
    }

    /**
     * 替换数据
     */
    fun replaceAll(data: List<BannerApiModel>?) {
        log("replaceAll")
        resetContentView()
//        //释放播放器
//        bas.lib.core.exception.trySilent {
//            player.release()
//        }
        this.data = data.orEmpty()
        pager.replaceAll(this.data)
        //似乎第一次不会主动触发item的选中
        if (this.data.isNotEmpty())
            dispatchItemSelectedOnCallback(0)
    }

    private fun handleDefaultFocus() {
        if (defaultFocusSetEnable && !hasSetDefaultFocus) {
            postDelayed({
                this.requestFocus()
            }, 300)
        }
    }

    private fun handleMessage(message: Message): Boolean {
        val data = this.data
        if (data.isEmpty()) {
            resetContentView()
            return true
        }
        when (message.what) {
            WHAT_DELAY_NEXT -> {
                log("handleMessage:WHAT_DELAY_NEXT")
                if (data.size == 1) {
                    //todo 新加的，不知道有没有问题
                    mHandler.removeCallbacksAndMessages(null)
                    //如果adapter只有一项，则重新调用该方法让item回调当前选中。从而让视频重新播放
                    dispatchItemSelectedOnCallback(pager.currentItem)
                } else {
                    pager.currentItem = pager.currentItem + 1
                }
            }
            WHAT_DELAY_PLAY -> {
                log("handleMessage:WHAT_DELAY_PLAY")
                val item = data[pager.currentItem]
                if (item.resType == ResType.VIDEO) {
//                    player.visibility = View.VISIBLE
//                    playerManager.startOrResumePlay(item)
                } else {
                    resetContentView()
                    delayToNextPage(SWITCH_INTERVAL - ACTION_DELAY_INTERVAL)
                }
            }
        }
        return true
    }

    /**
     * viewpager 选中 对应位置的回调
     */
    private fun dispatchItemSelectedOnCallback(position: Int) {
        log("dispatchItemSelectedOnCallback:$position")
        val data = this.data
        if (data.isEmpty()) {
            resetContentView()
            return
        }
        val item = data[position]
//        player.visibility = View.INVISIBLE
        pager.visibility = View.VISIBLE
        when (item.resType) {
            ResType.VIDEO -> {
                //视频选中处理：先显示图片，3s后播放器和图片显示状态互换，开始播放，播放结束之后延迟切换到下一页
                delayPlay()
            }
            else -> {
                //显示图片，隐藏视频
//                player.release()
                delayToNextPage()
            }
        }
    }

    /**
     * 延迟切换到下一页
     */
    private fun delayToNextPage(delay: Long = SWITCH_INTERVAL) {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendMessageDelayed(mHandler.obtainMessage(WHAT_DELAY_NEXT), delay)
    }

    /**
     * 延迟开始播放
     */
    private fun delayPlay() {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendMessageDelayed(mHandler.obtainMessage(WHAT_DELAY_PLAY), ACTION_DELAY_INTERVAL)
    }

    private fun resetContentView(removeMessage: Boolean = true) {
        if (removeMessage)
            mHandler.removeCallbacksAndMessages(null)
//        player.release()
//        player.visibility = View.INVISIBLE
        pager.visibility = View.VISIBLE
    }

    override fun onAttachedToWindow() {
        log("onAttachedToWindow")
        super.onAttachedToWindow()
        pager.startLoop()
    }

    override fun onDetachedFromWindow() {
        log("onDetachedFromWindow")
        super.onDetachedFromWindow()
        pager.stopLoop()
        mHandler.removeCallbacksAndMessages(null)
    }
}