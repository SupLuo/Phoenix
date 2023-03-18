package com.zhang.phoenix

import android.annotation.SuppressLint
import android.app.Application
import bas.droid.initBas
import bas.lib.core.converter.Converters
import bas.lib.core.converter.moshi.MoshiConverter
import com.zhang.phoenix.net.apiMoshi
import me.jessyan.autosize.AutoSizeConfig
import xyz.doikki.videoplayer.DKManager
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory

object Phoenix {

    @SuppressLint("StaticFieldLeak")
    internal lateinit var context: Application

    internal var isDebuggable: Boolean = false

    @JvmStatic
    fun init(context: Application, isDebuggable: Boolean = false) {
        this.context = context
        this.isDebuggable = isDebuggable
        initBas(context,isDebuggable)
        //使用moshi 序列化工具
        Converters.setJsonConverter(MoshiConverter(apiMoshi))
        //屏幕适配方案
        AutoSizeConfig.getInstance().setAutoAdaptStrategy(AutoSizeAdaptStrategy())
        AutoSizeConfig.getInstance().designWidthInDp = 980
        AutoSizeConfig.getInstance().designHeightInDp = 540
        //使用Ijk播放器
        DKManager.playerFactory = IjkPlayerFactory.create()
    }

}