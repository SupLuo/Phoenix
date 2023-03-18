package xyz.doikki.videoplayer.render

import android.content.Context

/**
 * todo 本身可以采用lambda实现，但是不利于调试时通过classname获取render的名字
 */
internal class SurfaceRenderViewFactory : RenderFactory {

    override fun create(context: Context): Render {
        return SurfaceRenderView(context)
    }

    override fun equals(other: Any?): Boolean {
        println("equals ${other is SurfaceRenderViewFactory}")
        if (other is SurfaceRenderViewFactory)
            return true
        return super.equals(other)
    }

    override fun hashCode(): Int {
        println("hashCode $this ${this.javaClass.hashCode()}")
        return this.javaClass.hashCode()
    }

}