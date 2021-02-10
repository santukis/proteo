package com.frikiplanet.proteo

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.properties.Delegates

class ItemsAdapter<Item>(private var viewHolderProvider: ViewHolderProvider<Item>) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder<Item>>() {

    private var areItemsTheSame: (Item, Item) -> Boolean = { old, new -> old == new }

    var items: List<Item> by Delegates.observable(mutableListOf()) { _, old, new ->

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    areItemsTheSame(old[oldItemPosition], new[newItemPosition])

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    old[oldItemPosition] == new[newItemPosition]

        })
        diff.dispatchUpdatesTo(this)
    }
        private set

    private var clickListener: OnItemClickListener? = null
    private var longClickListener: OnItemLongClickListener? = null

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<Item> = viewHolderProvider.itemViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: ItemViewHolder<Item>, position: Int) {
        val item = items[position]
        holder.bind(item, position)
        holder.setOnItemClickListener(clickListener, item, position)
        holder.setOnItemLongClickListener(longClickListener, item, position)
    }

    override fun getItemViewType(position: Int): Int = viewHolderProvider.getItemViewType(position, items[position])

    override fun getItemCount(): Int = items.size

    fun showItems(items: List<Item>, areItemsTheSame: (Item, Item) -> Boolean = { old, new -> old == new }) {
        this.areItemsTheSame = areItemsTheSame
        this.items = items
    }

    fun getItemAt(position: Int): Item? =
            try {
                items[position]
            } catch (exception: Exception) {
                null
            }

    fun addItemAt(position: Int, item: Item) {
        items = items.toMutableList().apply { add(position, item) }
    }

    fun addItemAtEnd(item: Item) {
        items = items.toMutableList().apply { add(item) }
    }

    fun removeItemAt(position: Int): Item? {
        var item: Item?
        items = items.toMutableList().apply { item = removeAt(position) }
        return item
    }

    fun clearItems() {
        items = emptyList()
    }

    fun updateViewHolderProvider(holderProvider: ViewHolderProvider<Item>) {
        viewHolderProvider = holderProvider
        notifyDataSetChanged()
    }

    fun addOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        clickListener = onItemClickListener
    }

    fun addOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?) {
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
        open fun setOnItemClickListener(onItemClickListener: OnItemClickListener?, item: Item, position: Int) {}
        open fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?, item: Item, position: Int) {}
    }

    abstract class ItemBindingViewHolder<Item, Binding : ViewBinding>(protected val binding: Binding): ItemViewHolder<Item>(binding.root)

}

open class ViewHolderProvider<Item>(val itemViewHolder: (parent: ViewGroup, viewType: Int) -> ItemsAdapter.ItemViewHolder<Item>) {

    open fun getItemViewType(position: Int, item: Item): Int = position
}