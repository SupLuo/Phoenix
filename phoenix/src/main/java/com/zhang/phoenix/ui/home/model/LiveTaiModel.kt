package com.zhang.phoenix.ui.home.model

import bas.lib.core.cache
import kotlin.properties.Delegates

data class LiveTaiModel(val id: String?) {
    /**
     * 播放地址
     */
    @delegate:Transient
    var playUrl: String? by Delegates.cache(23 * 60 * 60 * 1000, null)
}
