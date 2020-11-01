package com.frikiplanet.proteo

import android.view.View
import java.io.Serializable

@FunctionalInterface
interface OnItemClickListener: Serializable {

    fun onItemClick(view: View, item: Any)
}

@FunctionalInterface
interface OnItemLongClickListener: Serializable {

    fun onItemLongClick(item: Any)
}
