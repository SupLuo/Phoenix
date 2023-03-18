package com.zhang.phoenix.ui.home

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import bas.lib.core.exception.friendlyMessage
import com.zhang.phoenix.net.api.Api
import com.zhang.phoenix.ui.home.model.LiveTaiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.CompleteView
import xyz.doikki.videocontroller.component.ErrorView
import xyz.doikki.videocontroller.component.PrepareView
import xyz.doikki.videoplayer.DKVideoView

class MetroVideoViewManager(
    private val playerView: DKVideoView,
    private val callback: ReleaseUserFocus
) {

    /**
     * 释放用户焦点
     */
    interface ReleaseUserFocus {
        fun onReleaseUserFocus()

        fun getItemCount(): Int
    }

    //当前数据
    private var mData: LiveTaiModel? = null

    //请求播放数据的任务
    private var fetchPlayInfoJob: Job? = null

    //当前播放状态
    private var playerState: Int = DKVideoView.STATE_IDLE

    //播放器发生错误的次数
    private var playerErrorCount = 0

    private val controller: StandardVideoController = StandardVideoController(playerView.context)
    private val prepareView: PrepareView = PrepareView(playerView.context)
    private val errorView: ErrorView = ErrorView(playerView.context)
    private val completeView: CompleteView = CompleteView(playerView.context)

    private val lifecycleOwner: LifecycleOwner = playerView.context as LifecycleOwner

    /**
     * 生命周期是否已暂停
     */
    private var isLifecyclePause: Boolean = false

    init {
        controller.addControlComponent(prepareView)
        controller.addControlComponent(errorView)
        controller.addControlComponent(completeView)
        playerView.setVideoController(controller)
        playerView.addOnStateChangeListener(object : DKVideoView.OnStateChangeListener {
            override fun onPlayerStateChanged(playState: Int) {
                super.onPlayerStateChanged(playState)
                playerState = playState
                when (playerState) {
                    DKVideoView.STATE_PLAYING -> {
                        playerErrorCount = 0
                    }
                    DKVideoView.STATE_PREPARED -> {
                        playerErrorCount = 0
                        if(canUsePlayerOnLifecycle){
                            startPlay(0)
                            playerView.start()
                        }
                    }
                    DKVideoView.STATE_PLAYBACK_COMPLETED -> {
                        log("PlayerListener --> onPlayEnd")
                        restoreState()
                        if (callback.getItemCount() == 1) {
                            //如果只有一条数据，则视频end 状态不显示封面和endview，直接重新播放
                            completeView.setCompleteText("播放完成，即将重新播放...")
                        }
                        releaseUserFocus()
                    }
                    DKVideoView.STATE_ERROR -> {
                        playerState = DKVideoView.STATE_IDLE
                        playerErrorCount++
                        if (playerErrorCount <= 2) {
                            //前两次直接重试
                            log("PlayerListener --> onPlayError 第${playerErrorCount}次重试")
                            retryPlayOnError()
                        } else if (playerErrorCount == 3) {
                            log("PlayerListener --> onPlayError 第${playerErrorCount}次重试")
                            //第三次重试的时候将url设置为空，重新获取url
                            mData?.playUrl = null
                            //发生错误，重试一次
                            retryPlayOnError()
                        } else {
                            log("PlayerListener --> onPlayError 连续失败${playerErrorCount}次，不再重试")
                            //连续重试三次失败，则切换下一页
                            releaseUserFocus()
                        }
                    }
                }

            }
        })
    }

    fun onPause() {
        log("onPause in manager")
        isLifecyclePause = true
        if (playerView.isPlaying() && playerState != DKVideoView.STATE_PAUSED) {
            playerView.pause()
        }
    }

    fun onResume() {
        log("onResume in manager")
        isLifecyclePause = false

        val data = mData ?: return
        startOrResumePlay(data)
    }

    fun onDestroy() {
        log("onDestroy in manager")
        playerView.release()
    }

    fun startOrResumePlay(data: LiveTaiModel) {
        log("startOrResumePlay:${data}")
        prepareView.coverImage?.let {
//            todo 封面问题
//            bindImage16_9(it, data.Pic)
        }
        if (mData != null && mData != data) {
            //切换了数据，重置之前的，重新开始播放
            restoreState()
        }
        mData = data
        val resId = data.id
        if (resId.isNullOrEmpty()) {
            log("startOrResumePlay:播放资源id为空，releaseUserFocus")
            releaseUserFocus()
            errorView.setErrorMessage("播放资源ID为空，无法处理")
            errorView.onPlayStateChanged(DKVideoView.STATE_ERROR)
            return
        }
        if (data.playUrl.isNullOrEmpty()) {
            log("startOrResumePlay:播放地址不存在或播放地址已过期（30分钟），重新获取播放地址播放。")
            val previousJob = fetchPlayInfoJob
            if (previousJob != null && previousJob.isActive) {
                log("startOrResumePlay:previousJob还活着，在获取播放信息，不做其他额外处理")
                return
            }
            fetchPlayInfoJob = lifecycleOwner.lifecycleScope.launchWhenCreated {
                bas.lib.core.exception.tryCatching {
                    if (mData == data) {
                        //强制让view显示loading效果
                        prepareView.onPlayStateChanged(DKVideoView.STATE_PREPARING)
                        errorView.onPlayStateChanged(DKVideoView.STATE_PREPARING)
                        //重置下错误信息
                        errorView.setErrorMessage("出了点小问题，稍后重试")
                    }
                    val playerData = Api.getLivePlayUrl(data.id)
                    if (mData == data) {
                        if (playerData.isNullOrEmpty()) {
                            log("startOrResumePlay：playerData == null，释放")
                            releaseUserFocus()
                            errorView.setErrorMessage("播放地址不存在，无法播放。")
                            errorView.onPlayStateChanged(DKVideoView.STATE_ERROR)
                            return@tryCatching
                        }
                        log("startOrResumePlay：获取播放数据成功 url=${playerData}")
                        data.playUrl = playerData
                        fetchPlayInfoJob = null
                        onPlayInfoPrepared(playerData)
                    }
                }.onFailure {
                    log("startOrResumePlay：获取播放地址错误:$it")
                    it.printStackTrace()
                    if (it !is CancellationException) {
                        prepareView.onPlayStateChanged(DKVideoView.STATE_ERROR)
                        errorView.setErrorMessage(it.friendlyMessage)
                        errorView.onPlayStateChanged(DKVideoView.STATE_ERROR)
                        releaseUserFocus()
                    }//else todo 说明是被取消了，任务被取消？暂时不管
                    fetchPlayInfoJob = null
                }
            }
        } else {
            onPlayInfoPrepared(data.playUrl!!)
        }
    }

    private fun onPlayInfoPrepared(data: String) {
        log("播放地址可用")
        if (playerState == DKVideoView.STATE_IDLE) {
            setDataSource(data)
        } else if (playerState == DKVideoView.STATE_PREPARED) {
            startPlay()
        } else if (playerState == DKVideoView.STATE_PAUSED) {
            resumePlay()
        } else if (playerState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
            controller.replay(resetPosition = true)
        } else if (playerState == DKVideoView.STATE_ERROR) {
            controller.replay(resetPosition = false)
        }
    }

    /**
     * 设置数据源
     */
    private fun setDataSource(url: String?) {
        if (url.isNullOrEmpty()) {
            errorView.setErrorMessage("播放地址不存在，无法播放。")
            errorView.onPlayStateChanged(DKVideoView.STATE_ERROR)
            log("setDataSource 播放数据为空，放弃焦点，转移到下一页")
            releaseUserFocus()
            return
        }
        playerView.setDataSource(url)
//        if (playInfo.Headers.isNullOrEmpty()) {
//            playerView.setDataSource(url)
//        } else {
//            playerView.setDataSource(url, playInfo.Headers)
//        }
    }

    /**
     * 放弃当前当前焦点：延迟一段时间后切换到下一页
     */
    private fun releaseUserFocus() {
        restoreState()
        callback.onReleaseUserFocus()
    }

    /**
     * 是否可以操作播放器：避免生命周期不合适
     */
    private val canUsePlayerOnLifecycle: Boolean
        get() = lifecycleOwner.lifecycle.currentState.isAtLeast(
            Lifecycle.State.RESUMED
        ) && !isLifecyclePause

    /**
     * 重新尝试播放
     */
    private fun retryPlayOnError() {
        if (canUsePlayerOnLifecycle) {
            log("执行重试")
            mData?.let {
                startOrResumePlay(it)
            }
        } else {
            log("当前界面不可见或者viewholder未获取用户焦点，放弃重试")
        }
    }

    /**
     * 开始播放
     */
    private fun startPlay(seekPosition: Long = 0) {
        log("准备开始播放")
        if (canUsePlayerOnLifecycle) {
            log("当前界面已进入Resume生命周期，执行start")
//            //当前生命周期进入resume之后才开始播放
//            removeDelayPlayAction()
            if (seekPosition > 0)
                playerView.seekTo(seekPosition)
            playerView.start()
            playerState = DKVideoView.STATE_PLAYING
        } else {
            log("当前界面未进入Resume生命周期，不执行start")
        }
    }

    /**
     * 恢复播放
     * todo：是否存在resume失败的情况？是否需要考虑
     */
    private fun resumePlay() {
        log("播放器之前已就绪，准备恢复播放")
        if (canUsePlayerOnLifecycle) {
            log("当前界面已进入Resume生命周期，执行resume")
//            //当前生命周期进入resume之后才开始播放
//            removeDelayPlayAction()
            playerView.resume()
        } else {
            log("当前界面未进入Resume生命周期，不执行resume")
        }
    }

    /**
     * 还原状态
     */
    private fun restoreState() {
        playerState = DKVideoView.STATE_IDLE
        playerErrorCount = 0
        fetchPlayInfoJob?.cancel()
        mData = null
        playerView.release()
    }

//    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//        log("onStateChanged $event")
//        if (event == Lifecycle.Event.ON_PAUSE && playerView.isPlaying()) {
//            log("onStateChanged pause")
//            playerView.pause()
//        }
//
//        if (event == Lifecycle.Event.ON_RESUME && hasUserFocus) {
//            log("onStateChanged resume")
//            postDelayPlayAction()
//        }
//    }

    private fun log(msg: String) {
        Log.d("BBBB", msg)
    }
}