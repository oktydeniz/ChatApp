package com.oktydeniz.chatapp.opening.opening.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object DataBindingUtils{
    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(imageView: ImageView?, url: String?) {
        Glide.with(imageView!!.context).load(url).into(imageView)
    }
}