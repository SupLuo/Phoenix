package com.zhang.phoenix.ui.home.model

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.zhang.phoenix.Phoenix

open class TabInfo(
    val id: Int, @DrawableRes val image: Int,
    @DrawableRes val imageFocus: Int
) {

    private var _drawable: Drawable? = null

    val drawable: Drawable
        get() {
            if (_drawable == null) {
                _drawable = StateListDrawable().also {
                    val focus = ContextCompat.getDrawable(Phoenix.context, imageFocus)
                    it.addState(intArrayOf(android.R.attr.state_pressed), focus)
                    it.addState(intArrayOf(android.R.attr.state_focused), focus)
                    it.addState(intArrayOf(android.R.attr.state_selected), focus)
                    it.addState(intArrayOf(), ContextCompat.getDrawable(Phoenix.context, image))
                }
            }
            return _drawable!!
        }
}