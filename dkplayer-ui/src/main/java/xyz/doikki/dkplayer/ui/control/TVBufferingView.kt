package xyz.doikki.dkplayer.ui.control

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.DKVideoView
import xyz.doikki.videoplayer.controller.MediaController
import xyz.doikki.videoplayer.controller.VideoViewControl
import xyz.doikki.videoplayer.controller.component.ControlComponent
import xyz.doikki.videoplayer.util.setTextOrGone

/**
 * Created by Lucio on 2021/4/15.
 * 用于tv端播放缓冲和缓冲结束的逻辑
 */
class TVBufferingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ControlComponent {

    protected var mController: MediaController? = null
    protected val player: VideoViewControl? get() = mController?.playerControl
    private val titleView: TextView
    private val hintView: TextView
    private val loadingView: ProgressBar

    private var textWhenBuffering: CharSequence? = "缓冲中，请稍后..."

    init {
        View.inflate(context, R.layout.dkplayer_ctrl_buffering_view_layout, this)
        titleView = findViewById(R.id.ctrl_buffering_title_tv)
        hintView = findViewById(R.id.ctrl_buffering_hint_tv)
        loadingView = findViewById(R.id.ctrl_buffering_loading)
        //设置默认背景
        if (background == null) {
            setBackgroundColor(Color.parseColor("#4D000000"))
        }
    }

    fun setBufferingText(text: CharSequence?) {
        textWhenBuffering = text
    }

    @JvmOverloads
    fun show(title: CharSequence? = titleView.text, hint: CharSequence? = null) {
        titleView.setTextOrGone(title)
        hintView.setTextOrGone(hint)
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    override fun attachController(controller: MediaController) {
        mController = controller
    }

    override fun getView(): View {
        return this
    }

    override fun onPlayStateChanged(playState: Int) {
        super.onPlayStateChanged(playState)
        if (playState == DKVideoView.STATE_BUFFERING) {
            show(textWhenBuffering)
        } else if (playState == DKVideoView.STATE_PLAYING
            || playState == DKVideoView.STATE_ERROR
            || playState == DKVideoView.STATE_PLAYBACK_COMPLETED
            || playState == DKVideoView.STATE_BUFFERED
            || playState == DKVideoView.STATE_PREPARED_BUT_ABORT
            || playState == DKVideoView.STATE_PAUSED
        ) {
            hide()
        }
    }
}