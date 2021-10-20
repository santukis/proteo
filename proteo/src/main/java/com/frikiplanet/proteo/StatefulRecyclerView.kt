package com.frikiplanet.proteo

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class StatefulRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null): RecyclerView(context, attributeSet) {

    private val LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE"

    private var state: Parcelable? = null

    override fun onSaveInstanceState(): Parcelable? {
        super.onSaveInstanceState()

        return Bundle().apply {
            putParcelable(LAYOUT_MANAGER_STATE, layoutManager?.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? Bundle)?.let { bundle ->
            this.state = bundle.getParcelable(LAYOUT_MANAGER_STATE)

        } ?: super.onRestoreInstanceState(state)
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