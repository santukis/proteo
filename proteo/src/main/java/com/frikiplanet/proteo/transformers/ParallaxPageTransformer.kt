package com.frikiplanet.proteo.transformers

import android.os.Build
import android.annotation.TargetApi
import android.view.View
import androidx.viewpager.widget.ViewPager

class ParallaxPageTransformer(private val id: Int) : ViewPager.PageTransformer {
    private var border = 0
    private var speed = 0.2f

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun transformPage(view: View, position: Float) {
        val parallaxView = view.findViewById<View>(id)
        parallaxView?.apply {
            if (position > -1 && position < 1) {
                val width = parallaxView.width
                translationX = -(position * width * speed)
                val sc = (view.width.toFloat() - border) / view.width

                if (position == 0f) {
                    view.scaleX = 1f
                    view.scaleY = 1f

                } else {
                    view.scaleX = sc
                    view.scaleY = sc
                }
            }
        }
    }

    fun setBorder(px: Int) {
        border = px
    }

    fun setSpeed(speed: Float) {
        this.speed = speed
    }
}