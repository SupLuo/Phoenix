package xyz.doikki.videocontroller.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.DKVideoView
import xyz.doikki.videoplayer.TVCompatible
import xyz.doikki.videoplayer.util.isVisible
import xyz.doikki.videoplayer.util.orDefault

/**
 * 自动播放完成界面
 *
 *
 * update by luochao at 2022/9/28
 */
@TVCompatible(message = "默认布局根据是否是tv模式加载不同布局")
class CompleteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @LayoutRes layoutId: Int = UNDEFINED_LAYOUT
) : BaseControlComponent(context, attrs) {

    private val mStopFullscreen: View?

    /**
     * 设置播放结束按钮的文本（默认是“重新播放”）
     *
     * @param message
     */
    fun setCompleteText(message: CharSequence?) {
        findViewById<TextView?>(R.id.tv_replay)?.text = message
    }

    override fun onPlayStateChanged(playState: Int) {
        if (playState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
            visibility = VISIBLE
            mStopFullscreen?.isVisible = mController?.isFullScreen.orDefault()
            bringToFront()
        } else {
            visibility = GONE
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onScreenModeChanged(screenMode: Int) {
        //退出全屏按钮没指定
        mStopFullscreen ?: return

        if (screenMode == DKVideoView.SCREEN_MODE_FULL) {
            mStopFullscreen.visibility = VISIBLE
        } else if (screenMode == DKVideoView.SCREEN_MODE_NORMAL) {
            mStopFullscreen.visibility = GONE
        }

        val activity = activity ?: return
        mController?.let { controller ->
            if (controller.hasCutout()) {
                val orientation = activity.requestedOrientation
                val cutoutHeight = controller.cutoutHeight
                val sflp = mStopFullscreen.layoutParams as LayoutParams
                when (orientation) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                        sflp.setMargins(0, 0, 0, 0)
                    }
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                        sflp.setMargins(cutoutHeight, 0, 0, 0)
                    }
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                        sflp.setMargins(0, 0, 0, 0)
                    }
                }
            }
        }
    }

    init {
        //默认不显示
        visibility = GONE
        if (layoutId > 0) {
            layoutInflater.inflate(layoutId, this)
        } else {
            layoutInflater.inflate(
                if (isTelevisionUiMode()) R.layout.dkplayer_layout_complete_view_tv else R.layout.dkplayer_layout_complete_view,
                this
            )
        }

        //在xml中去除了一个布局层级，因此xml中的背景色改为代码设置在当前布局中
        setBackgroundColor(Color.parseColor("#33000000"))
        findViewById<View?>(R.id.replay_layout)?.setOnClickListener {
            //重新播放
            mController?.replay(true)
        }
//        if (isTelevisionUiMode()) {
//            replyAct.isClickable = true
//            setViewInFocusMode(replyAct)
//        } else {
//            //防止touch模式下，事件穿透
//            isClickable = true
//        }
        //防止touch模式下，事件穿透
        isClickable = true

        mStopFullscreen = findViewById(R.id.stop_fullscreen)
        mStopFullscreen?.setOnClickListener {
            mController?.let { controller ->
                if (controller.isFullScreen) {
                    val activity = activity
                    if (activity != null && !activity.isFinishing) {
                        controller.stopFullScreen()
                    }
                }
            }
        }
    }

}