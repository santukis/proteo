package com.frikiplanet.proteo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ItemsAdapter<Item>(private var viewHolderProvider: ViewHolderProvider<Item>) :
        RecyclerView.Adapter<ItemsAdapter.ItemViewHolder<Item>>() {

    private var items: MutableList<Item> = mutableListOf()
    private var clickListener: OnItemClickListener? = null
    private var longClickListener: OnItemLongClickListener? = null

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<Item> =
            viewHolderProvider.getViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: ItemViewHolder<Item>, position: Int) {
        val item = items[position]
        holder.bind(item, position)
        holder.setOnItemClickListener(clickListener, item, position)
        holder.setOnItemLongClickListener(longClickListener, item, position)
    }

    override fun getItemViewType(position: Int): Int = viewHolderProvider.getItemViewType(position, items[position])

    override fun getItemCount(): Int = items.size

    fun showItems(items: List<Item>) {
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    fun addItems(items: List<Item>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addDistinctItems(items: List<Item>) {
        this.items = this.items.union(items).toMutableList()
        notifyDataSetChanged()
    }

    fun getItems(): List<Item> = items

    fun getItemAt(position: Int): Item? =
            try {
                items[position]
            } catch (exception: Exception) {
                null
            }

    fun addItemAt(position: Int, item: Item) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    fun addItemAtEnd(item: Item) {
        items.add(item)
        notifyItemInserted(if (items.isEmpty()) 0 else items.lastIndex)
    }

    fun removeItemAt(position: Int): Item? {
        val item = items.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    fun updateItemAt(position: Int, item: Item) {
        try {
            items[position] = item
            notifyItemChanged(position)

        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun updateItems(items: List<Item>) {
        this.items.forEachIndexed { index, outdatedItem ->
            items.forEach { updatedItem ->
                if (outdatedItem == updatedItem) {
                    this.items[index] = updatedItem
                    notifyItemChanged(index)
                }
            }
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
        return true
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
}

open class ViewHolderProvider<Item>(private val itemViewHolder: Class<out RecyclerView.ViewHolder> = EmptyViewHolder::class.java,
                                    private val layoutRes: Int) {

    open fun getItemViewType(position: Int, item: Item): Int = -1

    open fun getViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ItemViewHolder<Item> {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return itemViewHolder.getConstructor(View::class.java).newInstance(view) as ItemsAdapter.ItemViewHolder<Item>
    }
}

class EmptyViewHolder(itemView: View): ItemsAdapter.ItemViewHolder<Any>(itemView) {

    override fun bind(value: Any, position: Int) {

    }

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener?, item: Any, position: Int) {

    }

    override fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?, item: Any, position: Int) {

    }

}