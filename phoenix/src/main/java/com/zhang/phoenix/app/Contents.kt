package com.zhang.phoenix.app

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.zhang.phoenix.R
import com.zhang.phoenix.net.IMAGE_URL_PREFIX
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMATTER = "yyyy/MM/dd"

@JvmOverloads
inline fun ImageView.loadPhx(url: String?, @DrawableRes placeHolder: Int = R.drawable.image_place_holder) {
    Glide.with(this)
        .load(url.toPnxUrl)
        .placeholder(placeHolder)
        .into(this)
}

inline val String?.toPnxUrl: String?
    get() {
        if (this.isNullOrEmpty() || this.startsWith("http")) {
            return this
        }
        return IMAGE_URL_PREFIX + this
    }

@PublishedApi
internal val categoryDateFormat:SimpleDateFormat = SimpleDateFormat(DATE_FORMATTER)

inline fun Date?.categoryDateText(invalidDefVal: String = ""): String {
    if (this == null)
        return invalidDefVal
    return categoryDateFormat.format(this)
}
