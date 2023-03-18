package com.zhang.phoenix.ui.home

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import bas.droid.core.onDebounceClick
import bas.droid.rva.PowerfulViewHolder
import bas.droid.setTextOrGone
import bas.lib.areItemsEqual
import bas.lib.core.exception.onCatch
import bas.lib.core.exception.tryIgnore
import bas.lib.orDefault
import com.zhang.phoenix.app.loadPhx
import com.zhang.phoenix.databinding.PnxHomeRowMetroBinding
import com.zhang.phoenix.net.model.MediaModel
import com.zhang.phoenix.ui.home.model.LiveTaiModel
import com.zhang.phoenix.ui.home.uistate.HomeMetroRow


class MetroViewHolder : PowerfulViewHolder {

    constructor(binding: ViewBinding) : super(binding)
    constructor(itemView: View) : super(itemView)
    constructor(parent: ViewGroup, layoutId: Int) : super(
        parent,
        layoutId
    )

    constructor(
        inflater: LayoutInflater,
        layoutId: Int,
        parent: ViewGroup?
    ) : super(inflater, layoutId, parent)

    private val what_next = 1
    private var currentIndex = 0
    private val interval: Long = 6000
    private var isAttach: Boolean = false
    private var isResume: Boolean = false
    private var targetEnable: Boolean = false

    private val lifecycleOwner: LifecycleOwner? get() = itemView.context as? LifecycleOwner

    private val mBindings: PnxHomeRowMetroBinding = getBinding<PnxHomeRowMetroBinding>()

    private lateinit var playerManager: MetroVideoViewManager
    private val zwtTai = LiveTaiModel("xfjcHD")
    private val zxTai = LiveTaiModel("xfylHD")
    private var currentTai: LiveTaiModel? = null

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.d("LoopText", "执行message")
            if (msg.what == what_next) {
                if (!targetEnable) {
                    this.removeMessages(what_next)
                    return
                }
                tryIgnore {
                    val items = model?.zixuntoutiao
                    var nextIndex: Int = 0
                    if (items.isNullOrEmpty()) {
                        currentIndex = 0
                        nextIndex = -1
                    } else {
                        if (currentIndex < 0 || currentIndex >= items.size - 1) {
                            currentIndex = 0
                            nextIndex = 0
                        } else {
                            nextIndex = currentIndex + 1
                        }
                        if (nextIndex >= 0 && nextIndex <= items.size - 1) {
                            val next = model?.zixuntoutiao?.getOrNull(nextIndex)
                            mBindings.topText.setTextOrGone(next?.name)
                            Log.d("LoopText", "currentIndex=$nextIndex size=${items.size}")
                            currentIndex = nextIndex
                        }
                    }
                }.onCatch {
                    it.printStackTrace()
                }
                sendMessageDelayed(obtainMessage(what_next), interval)
            }
        }
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            isResume = true

            bas.lib.core.exception.trySilent {
                playerManager.onResume()
            }

            tryStartLoop()
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            isResume = false
            bas.lib.core.exception.trySilent {
                playerManager.onPause()
            }
            stopLoop()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            bas.lib.core.exception.trySilent {
                stopLoop()
            }
            bas.lib.core.exception.trySilent {
                playerManager.onDestroy()
            }
            super.onDestroy(owner)
        }
    }

    private val model: HomeMetroRow? get() = getModel()

    override fun bindModel(data: Any) {
        val oldData = getModel<HomeMetroRow?>()
        super.bindModel(data)
        data as HomeMetroRow

        if (!data.banners.areItemsEqual(oldData?.banners)) {
            mBindings.metroBanner.replaceAll(data.banners)
        }
        val items = data.zixuntoutiao
        if (currentIndex < 0 || currentIndex >= items.size - 1) {
            currentIndex = 0
        }

        bindTopCard(data.zixuntoutiao.getOrNull(currentIndex))
        mBindings.metroImage22.loadPhx(data.rebobang.images.firstOrNull())
        mBindings.hotText.setTextOrGone(data.rebobang.name)
        tryStartLoop()
        if (currentTai == null) {
            currentTai = zwtTai
            startPlayLive(zwtTai)
        }
    }

    private fun startPlayLive(data: LiveTaiModel) {
        playerManager.startOrResumePlay(data)
    }

    private fun bindTopCard(data: MediaModel?) {
        mBindings.metroImage11.loadPhx(data?.images?.firstOrNull())
        mBindings.topText.setTextOrGone(data?.name)
    }

    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        isAttach = true
        lifecycleOwner?.lifecycle?.let {
            it.removeObserver(lifecycleObserver)
            it.addObserver(lifecycleObserver)
        }
        tryStartLoop()
    }

    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
        isAttach = false
        lifecycleOwner?.lifecycle?.removeObserver(lifecycleObserver)
        stopLoop()
    }

    private fun tryStartLoop() {
        handler.removeMessages(what_next)
        targetEnable = if (model?.zixuntoutiao?.size.orDefault() > 1) {
            handler.sendMessageDelayed(handler.obtainMessage(what_next), interval)
            true
        } else {
            false
        }
    }

    private fun stopLoop() {
        targetEnable = false
        handler.removeCallbacksAndMessages(null)
    }

    init {
        lifecycleOwner?.let {
            isResume = it.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        }

        playerManager = MetroVideoViewManager(mBindings.playerView, object :
            MetroVideoViewManager.ReleaseUserFocus {
            override fun onReleaseUserFocus() {
//                isPlayerReleaseFocus = true
//                delayToNextPage(ACTION_DELAY_INTERVAL)
                if (currentTai == null)
                    currentTai = zwtTai
                startPlayLive(currentTai!!)
            }

            override fun getItemCount(): Int {
                return 1
            }
        })
        mBindings.leftTaiContainer.onDebounceClick {
            if (currentTai == zwtTai) {

            } else {
                currentTai = zwtTai
                startPlayLive(zwtTai)
            }
        }

        mBindings.rightTaiContainer.onDebounceClick {
            if (currentTai == zxTai) {

            } else {
                currentTai = zxTai
                startPlayLive(zxTai)
            }
        }
    }
}