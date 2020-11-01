package com.frikiplanet.proteo.transformers

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class RotationPageTransformer2(private val degrees: Double): ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.pivotX = page.width.toFloat() / 2
        page.pivotY = page.height * 2f

        when {
            position < -1.0 -> page.rotation = 0f // [-Infinity,-0.8) way off-screen to the left
            position > -1.0 && position <= 0.0 -> page.rotation = (position * degrees.toFloat())
            position > 0.0 && position < 0.7 -> page.translationX = -position
            else -> page.rotation = 0f // (0.7,+Infinity] way off-screen to the right
        }
    }
}