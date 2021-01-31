package com.frikiplanet.proteo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class ItemsAdapter<Item>(private var viewHolderProvider: ViewHolderProvider<Item>,
                         areItemsTheSame: (Item, Item) -> Boolean = { old, new -> old == new }) :
        RecyclerView.Adapter<ItemsAdapter.ItemViewHolder<Item>>() {

    private var items: List<Item> by update(emptyList(), areItemsTheSame = areItemsTheSame)
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

fun <Item> ItemsAdapter<Item>.update(
        initialValue: List<Item> = emptyList(),
        areItemsTheSame: (Item, Item) -> Boolean = { old, new -> old == new }
) = Delegates.observable(initialValue) { _, old, new ->

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