package xyz.doikki.videoplayer.render

import android.content.Context
import android.os.Build
import xyz.doikki.videoplayer.DKManager
import xyz.doikki.videoplayer.render.RenderFactory.Companion.textureViewRenderFactory

/**
 * 此接口用于扩展自己的渲染View。使用方法如下：
 * 1.继承IRenderView实现自己的渲染View。
 * 2.重写createRenderView返回步骤1的渲染View。
 * 3.通过[DKManager.renderFactory] 设置步骤2的实例
 * 可参考[TextureRenderView]和[textureViewRenderFactory]的实现。
 */
fun interface RenderFactory {

    fun create(context: Context): Render

    companion object {

        @JvmStatic
        val DEFAULT: RenderFactory =
            if (Build.VERSION.SDK_INT < 21) surfaceViewRenderFactory() else textureViewRenderFactory()

        @JvmStatic
        fun textureViewRenderFactory(): RenderFactory {
            return TextureRenderViewFactory()
        }

        @JvmStatic
        fun surfaceViewRenderFactory(): RenderFactory {
            return SurfaceRenderViewFactory()
        }
    }
}