package com.zhang.phoenix.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import bas.droid.rva.PowerfulViewHolder
import bas.droid.setTextOrGone
import bas.droid.setTextOrInvisible
import com.zhang.phoenix.app.loadPhx
import com.zhang.phoenix.databinding.PnxHomeRowLatestBinding
import com.zhang.phoenix.net.model.HomeLatestRow

class LatestViewHolder : PowerfulViewHolder {

    constructor(binding: ViewBinding) : super(binding)
    constructor(itemView: View) : super(itemView)
    constructor(parent: ViewGroup, layoutId: Int) : super(parent, layoutId)

    constructor(inflater: LayoutInflater, layoutId: Int, parent: ViewGroup?) : super(
        inflater,
        layoutId,
        parent
    )

    private class VH(
        val view: View,
        val imageView: ImageView,
        val textView: TextView,
        val timeView: TextView
    )

    private val viewHolders: List<VH>

    init {
        val binding = getBinding<PnxHomeRowLatestBinding>()
        viewHolders = listOf(
            VH(
                binding.weight4Ceil1,
                binding.weight4Image1,
                binding.weight4Text1,
                binding.weight4Tag1
            ),
            VH(
                binding.weight4Ceil2,
                binding.weight4Image2,
                binding.weight4Text2,
                binding.weight4Tag2
            ),
            VH(
                binding.weight4Ceil3,
                binding.weight4Image3,
                binding.weight4Text3,
                binding.weight4Tag3
            ),
            VH(
                binding.weight4Ceil4,
                binding.weight4Image4,
                binding.weight4Text4,
                binding.weight4Tag4
            )
        )
    }

    override fun bindModel(data: Any) {
        super.bindModel(data)

        val model = data as HomeLatestRow

        viewHolders.forEachIndexed { index, vh ->
            val item = model.categories.getOrNull(index)
            if (item == null) {
                vh.view.visibility = View.INVISIBLE
            } else {
                vh.view.isVisible = true
                vh.imageView.loadPhx(item.image)
                vh.textView.setTextOrInvisible(item.description)
                vh.timeView.setTextOrGone(item.updateTime)
            }
        }
    }
}