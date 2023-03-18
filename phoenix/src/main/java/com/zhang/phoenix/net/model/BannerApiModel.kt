package com.zhang.phoenix.net.model

import androidx.annotation.Keep

interface BannerApiModel {

    val resType:Int

    val pic:String?
}

@Keep
data class BannerImage( override val pic: String?):BannerApiModel{
    override val resType: Int = ResType.PICTURES
}