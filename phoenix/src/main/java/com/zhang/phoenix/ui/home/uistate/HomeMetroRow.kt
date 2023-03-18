package com.zhang.phoenix.ui.home.uistate

import com.zhang.phoenix.net.model.BannerApiModel
import com.zhang.phoenix.net.model.BannerImage
import com.zhang.phoenix.net.model.LiveTai
import com.zhang.phoenix.net.model.MediaModel

data class HomeMetroRow(
    val zixuntoutiao: List<MediaModel>,
    val rebobang: MediaModel,
    val zhongwentai: LiveTai,
    val zixuntai: LiveTai,
    val tuijian8: MediaModel
) {
    val banners: List<BannerApiModel>? by lazy {
        tuijian8.images.map {
            BannerImage(it)
        }
    }
}