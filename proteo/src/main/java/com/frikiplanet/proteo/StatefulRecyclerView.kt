package com.frikiplanet.proteo

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class StatefulRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null): RecyclerView(context, attributeSet) {

    private var state: SavedState? = null

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? SavedState)?.let {
            this.state = state
        }

        super.onRestoreInstanceState(state)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        restorePosition()
    }

    private fun restorePosition() {
        state?.let { savedState ->
            layoutManager?.onRestoreInstanceState(savedState)
            state = null
        }
    }
}