package bas.droid

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import bas.droid.core.droidExceptionHandler
import bas.lib.core.exceptionHandler
import bas.lib.core.exceptionMessageTransformer
import com.zhang.phoenix.BuildConfig

/**
 * 是否开启调试模式
 */
var debuggable: Boolean = false
lateinit var ctxBas:Application
internal var isInit: Boolean = false

fun initBas(application: Application,debug:Boolean = BuildConfig.DEBUG){
    if (isInit) {
        return
    }
    isInit = true
    ctxBas = application
    debuggable = debug
    exceptionHandler = droidExceptionHandler
    exceptionMessageTransformer = DroidExceptionMessageTransformer()
}

fun TextView.setTextOrGone(message: CharSequence?) {
    visibility = if (message.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
    text = message
}

fun TextView.setTextOrInvisible(message: CharSequence?) {
    visibility = if (message.isNullOrEmpty()) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
    text = message
}


/**
 * 调试执行代码
 */
inline fun <T> T.runOnDebug(action: () -> Unit): T {
    if (debuggable) {
        action()
    }
    return this
}


inline fun <T, R> T.mapAs(transformer: T.() -> R): R {
    return transformer(this)
}

internal inline val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)
