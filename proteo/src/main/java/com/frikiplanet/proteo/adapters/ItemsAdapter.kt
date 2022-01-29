package com.frikiplanet.proteo.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

typealias OnItemClickListener = (view: View, item: Any) -> Unit

class ItemsAdapter<Item>(private var viewHolderProvider: ViewHolderProvider<Item>) :
    ListAdapter<Item, ItemsAdapter.ItemViewHolder<Item>>(viewHolderProvider.diffUtilCallback) {

    private var clickListener: OnItemClickListener? = null
    private var longClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<Item> = viewHolderProvider.itemViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: ItemViewHolder<Item>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item, position)
            holder.setOnItemClickListener(clickListener, item, position)
            holder.setOnItemLongClickListener(longClickListener, item, position)
            holder.afterBind(item, position)
        }
    }

    override fun getItemViewType(position: Int): Int = viewHolderProvider.getItemViewType(position, currentList[position])

    override fun getItemCount(): Int = currentList.size

    override fun getItem(position: Int): Item? = currentList.getOrNull(position)

    override fun onViewAttachedToWindow(holder: ItemViewHolder<Item>) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: ItemViewHolder<Item>) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    fun updateViewHolderProvider(holderProvider: ViewHolderProvider<Item>) {
        viewHolderProvider = holderProvider
        notifyDataSetChanged()
    }

    fun addOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        clickListener = onItemClickListener
    }

    fun addOnItemLongClickListener(onItemLongClickListener: OnItemClickListener?) {
        longClickListener = onItemLongClickListener
    }

    abstract class ItemViewHolder<Item>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var backgroundView: View? = null
        var foregroundView: View? = null

        var horizontalOffset: Float = 0f
            set(v) {
                field = v.coerceIn(-1f, 1f)
                onOffsetChangedListener(field, RecyclerView.HORIZONTAL)

            }

        var verticalOffset: Float = 0f
            set(v) {
                field = v.coerceIn(-1f, 1f)
                onOffsetChangedListener(field, RecyclerView.VERTICAL)
            }

        open var onOffsetChangedListener: (offset: Float, direction: Int) -> Unit = { _, _ -> }

        abstract fun bind(value: Item, position: Int)
        open fun afterBind(value: Item, position: Int) {}
        open fun onViewAttachedToWindow() {}
        open fun onViewDetachedFromWindow() {}
        open fun setOnItemClickListener(onItemClickListener: OnItemClickListener?, item: Item, position: Int) {}
        open fun setOnItemLongClickListener(onItemLongClickListener: OnItemClickListener?, item: Item, position: Int) {}
    }

    abstract class ItemBindingViewHolder<Item, Binding : ViewBinding>(protected val binding: Binding): ItemViewHolder<Item>(binding.root)
}

abstract class ViewHolderProvider<Item>() {

    abstract val itemViewHolder: (parent: ViewGroup, viewType: Int) -> ItemsAdapter.ItemViewHolder<Item>

    abstract val diffUtilCallback: DiffUtil.ItemCallback<Item>

    open fun getItemViewType(position: Int, item: Item): Int = position
}