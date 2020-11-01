package com.frikiplanet.proteo.transformers

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

class AlphaPageTransformer2: ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.pivotX = page.width.toFloat() / 2
        page.pivotY = page.height * 2f

        page.alpha = when {
            position < -1 -> 0.1f
            position <= 1 -> max(0.2f, 1 - abs(position))
            else -> 0.1f
        }
    }
}