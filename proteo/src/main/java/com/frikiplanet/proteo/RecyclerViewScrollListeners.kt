package com.frikiplanet.proteo

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.viewpager2.widget.ViewPager2
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

val ViewPager2.recyclerView: RecyclerView
    get() {
        return this[0] as RecyclerView
    }

class SingleScrollDirectionEnforcer : RecyclerView.OnScrollListener(), OnItemTouchListener {

    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    private var scrollPointerId = -1
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var dx = 0
    private var dy = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrollPointerId = e.getPointerId(0)
                initialTouchX = (e.x + 0.5f).toInt()
                initialTouchY = (e.y + 0.5f).toInt()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = e.actionIndex
                scrollPointerId = e.getPointerId(actionIndex)
                initialTouchX = (e.getX(actionIndex) + 0.5f).toInt()
                initialTouchY = (e.getY(actionIndex) + 0.5f).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val index = e.findPointerIndex(scrollPointerId)
                if (index >= 0 && scrollState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    val x = (e.getX(index) + 0.5f).toInt()
                    val y = (e.getY(index) + 0.5f).toInt()
                    dx = x - initialTouchX
                    dy = y - initialTouchY
                }
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val oldState = scrollState
        scrollState = newState
        if (oldState == RecyclerView.SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            recyclerView.layoutManager?.let { layoutManager ->
                val canScrollHorizontally = layoutManager.canScrollHorizontally()
                val canScrollVertically = layoutManager.canScrollVertically()
                if (canScrollHorizontally != canScrollVertically) {
                    if ((canScrollHorizontally && abs(dy) > abs(dx))
                            || (canScrollVertically && abs(dx) > abs(dy))) {
                        recyclerView.stopScroll()
                    }
                }
            }
        }
    }
}

fun RecyclerView.enforceSingleScrollDirection() {
    val enforcer = SingleScrollDirectionEnforcer()
    addOnItemTouchListener(enforcer)
    addOnScrollListener(enforcer)
}

class OffsetScrollListener: RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

        val horizontalOffsetFactor = (recyclerView.computeHorizontalScrollOffset() % recyclerView.measuredWidth) / recyclerView.measuredWidth.toFloat()
        val verticalOffsetFactor = (recyclerView.computeVerticalScrollOffset() % recyclerView.measuredHeight) / recyclerView.measuredHeight.toFloat()

        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition)?.let {
            (it as? ItemsAdapter.ItemViewHolder<*>)?.horizontalOffset = -horizontalOffsetFactor
            (it as? ItemsAdapter.ItemViewHolder<*>)?.verticalOffset = -verticalOffsetFactor
        }

        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        if (firstVisibleItemPosition != lastVisibleItemPosition) {
            recyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition)?.let {
                (it as? ItemsAdapter.ItemViewHolder<*>)?.horizontalOffset = 1 - horizontalOffsetFactor
                (it as? ItemsAdapter.ItemViewHolder<*>)?.verticalOffset = 1 - verticalOffsetFactor
            }
        }
    }
}

fun RecyclerView.setupOffsetScrollListener() {
    addOnScrollListener(OffsetScrollListener())
}

class EndlessScrollListener(
        private val threshold: Int,
        private val onEndReached: (Int) -> Unit
): RecyclerView.OnScrollListener() {

    private val endReached = AtomicBoolean(true)
    private var previousItemCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        recyclerView.layoutManager?.let { layoutManager ->

            if (previousItemCount < layoutManager.itemCount) {
                previousItemCount = layoutManager.itemCount
                endReached.set(false)
            }

            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val hasMoreItems = layoutManager.childCount < layoutManager.itemCount

            if (hasMoreItems && lastVisibleItemPosition + threshold > layoutManager.itemCount && !endReached.get()) {
                endReached.set(true)
                onEndReached.invoke(lastVisibleItemPosition)
            }

        } ?: super.onScrolled(recyclerView, dx, dy)
    }

    private fun RecyclerView.LayoutManager.findLastVisibleItemPosition(): Int =
        when(this) {
            is LinearLayoutManager -> findLastCompletelyVisibleItemPosition()
            is StaggeredGridLayoutManager -> findLastCompletelyVisibleItemPositions(null).maxOf { it }
            else -> RecyclerView.NO_POSITION
        }
}

fun RecyclerView.addEndlessScrollListener(
        threshold: Int = 3,
        onEndReached: (Int) -> Unit = {}
) {
    addOnScrollListener(EndlessScrollListener(threshold, onEndReached))
}

@ExperimentalCoroutinesApi
fun RecyclerView.addEndlessScrollListener(threshold: Int = 3): Flow<Int> =
        callbackFlow {
            val listener = EndlessScrollListener(threshold) { lastVisiblePosition ->
                offer(lastVisiblePosition)
            }

            addOnScrollListener(listener)

            awaitClose { removeOnScrollListener(listener) }
        }

