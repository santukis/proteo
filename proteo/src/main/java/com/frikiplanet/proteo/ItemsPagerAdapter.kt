package com.frikiplanet.proteo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class ItemsPagerAdapter<Item>(private val itemViewHolder: ItemViewPagerHolder<Item>): PagerAdapter() {

    private val items: MutableList<Item> = mutableListOf()
    private var listener: OnItemClickListener? = null

    override fun getCount(): Int = items.size

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(itemViewHolder.layoutRes, container, false)

        val item = items[position]
        itemViewHolder.bind(view, item)
        itemViewHolder.setOnItemClickListener(view, listener, item)

        container.addView(view)

        return view
    }

    fun showItems(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Item {
        return items[position]
    }

    fun addOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        listener = onItemClickListener
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}

abstract class ItemViewPagerHolder<Item>(val layoutRes: Int) {
    abstract fun bind(view: View, value: Item)
    abstract fun setOnItemClickListener(view: View, onItemClickListener: OnItemClickListener?, item: Item)
}