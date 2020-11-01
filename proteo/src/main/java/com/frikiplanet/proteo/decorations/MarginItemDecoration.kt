package com.frikiplanet.proteo.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val left: Int  = 0,
                           private val top: Int = 0,
                           private val right: Int  = 0,
                           private val bottom: Int = 0) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            left =  this@MarginItemDecoration.left
            top = this@MarginItemDecoration.top
            right = this@MarginItemDecoration.right
            bottom = this@MarginItemDecoration.bottom
        }
    }
}