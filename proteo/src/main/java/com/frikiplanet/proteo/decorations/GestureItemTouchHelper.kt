package com.frikiplanet.proteo.decorations

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.GridLayoutManager
import com.frikiplanet.proteo.ItemsAdapter

class GestureItemTouchHelper(
        private val canDrag: () -> Boolean = { false },
        private val canSwipe: () -> Boolean = { false }
): ItemTouchHelper.Callback() {

    private var listener: ItemTouchGestureListener? = null

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            (viewHolder as? ItemsAdapter.ItemViewHolder<*>)?.foregroundView?.apply {
                getDefaultUIUtil().onSelected(this)
            }
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            listener?.onItemMoveFinished(viewHolder)
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onChildDrawOver(canvas: Canvas, recyclerView: RecyclerView,
                                 viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                                 actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            (viewHolder as? ItemsAdapter.ItemViewHolder<*>)?.foregroundView?.apply {
                getDefaultUIUtil().onDrawOver(canvas, recyclerView, this, dX, dY,
                        actionState, isCurrentlyActive)
            }
        } else {
            super.onChildDrawOver(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        (viewHolder as? ItemsAdapter.ItemViewHolder<*>)?.foregroundView?.apply {
            getDefaultUIUtil().clearView(this)
        }

        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            (viewHolder as? ItemsAdapter.ItemViewHolder<*>)?.foregroundView?.apply {
                getDefaultUIUtil().onDraw(canvas, recyclerView, this, dX, dY,
                        actionState, isCurrentlyActive)
            }
        } else {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (recyclerView.layoutManager is GridLayoutManager) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            makeMovementFlags(dragFlags, swipeFlags)
        } else {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            makeMovementFlags(dragFlags, swipeFlags)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (canSwipe()) listener?.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return if (canDrag()) listener?.onItemMove(source.adapterPosition, target.adapterPosition) ?: true else true
    }

    override fun isLongPressDragEnabled(): Boolean = canDrag()

    override fun isItemViewSwipeEnabled(): Boolean = canSwipe()

    fun setOnRecyclerItemTouchHelperListener(recyclerItemTouchGestureListener: ItemTouchGestureListener) {
        listener = recyclerItemTouchGestureListener
    }

    interface ItemTouchGestureListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {}
        fun onItemMove(fromPosition: Int, toPosition: Int): Boolean { return true }
        fun onItemMoveFinished(viewHolder: RecyclerView.ViewHolder?) {}
    }
}