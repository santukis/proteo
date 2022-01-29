package com.frikiplanet.proteo.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable

class ItemsArrayAdapter<Item>(private val viewProvider: ViewProvider<Item>) : BaseAdapter(), Filterable {

    private var items: MutableList<Item> = mutableListOf()
    private var filteredItems: MutableList<Item> = mutableListOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return viewProvider.getViewForItem(position, convertView, parent, filteredItems[position])
    }

    override fun getItem(position: Int): Item = filteredItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = filteredItems.size

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val results = FilterResults()

            charSequence?.apply {
                val filteredItems = viewProvider.filterItems(items, this)
                results.values = filteredItems
                results.count = filteredItems.size
            }

            return results
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            if (filterResults != null && filterResults.count > 0) {
                showFilteredItems(filterResults.values as MutableList<Item>)

            } else {
                showItems(items)
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return viewProvider.convertResultToString(resultValue)
        }
    }

    fun getItems(): List<Item> = items

    fun showFilteredItems(items: List<Item>) {
        this.filteredItems.clear()
        this.filteredItems.addAll(items)
        notifyDataSetChanged()
    }

    fun showItems(items: List<Item>) {
        this.items = items.toMutableList()
        showFilteredItems(items)
    }

    fun getPositionFor(selectedItem: Item): Int? {
        items.forEachIndexed { index, item ->
            if (item == selectedItem) {
                return index
            }
        }

        return null
    }
}

abstract class ViewProvider<Item> {

    abstract fun getViewForItem(position: Int, convertView: View?, parent: ViewGroup?, item: Item): View

    open fun filterItems(items: List<Item>, charSequence: CharSequence): List<Item> = emptyList()

    open fun convertResultToString(value: Any?): CharSequence { return "" }
}