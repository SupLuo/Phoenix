package com.zhang.phoenix.widget.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.zhang.phoenix.R
import com.zhang.phoenix.app.loadPhx
import com.zhang.phoenix.net.model.BannerApiModel

class BannerImageViewHolder(view: View) : BannerViewHolder(view) {

    constructor(inflater: LayoutInflater, parent: ViewGroup?) : this(
        inflater.inflate(
            R.layout.pnx_home_banner_image_item,
            parent,
            false
        )
    )

    override fun bindDataInternal(data: BannerApiModel, position: Int, pageSize: Int) {
        getView<ImageView>(R.id.image_view)?.loadPhx(data.pic)
    }
}