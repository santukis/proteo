package com.frikiplanet.proteo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.annotation.LayoutRes
import java.lang.Exception

class ItemsExpandableAdapter<Item: ExpandableItem>(private val expandableViewProvider: ExpandableViewProvider<Item>) : BaseExpandableListAdapter(),
        ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    private var items: MutableList<Item> = mutableListOf()

    private var onGroupClickListener: OnItemClickListener? = null

    private var onChildClickListener: OnItemClickListener? = null

    override fun getGroupCount(): Int = items.size

    override fun getChildrenCount(groupPosition: Int): Int = items[groupPosition].getChildren().size

    override fun getGroup(groupPosition: Int): Any = items[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
            items[groupPosition].getChildren()[childPosition]

    override fun getGroupId(groupPosition: Int): Long = items[groupPosition].getId()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long =
            items[groupPosition].getChildren()[childPosition].getId()

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val group = getGroup(groupPosition) as Item
        val view = getView(parent.context, convertView, expandableViewProvider.groupLayout)
        expandableViewProvider.bindGroup(view, group, groupPosition, isExpanded)
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val child = getChild(groupPosition, childPosition) as Item
        val view = getView(parent.context, convertView, expandableViewProvider.childLayout)
        expandableViewProvider.bindChild(view, child, groupPosition, childPosition, isLastChild)
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun onChildClick(parent: ExpandableListView, view: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        onChildClickListener?.invoke(view, getChild(groupPosition, childPosition))
        return false
    }

    override fun onGroupClick(parent: ExpandableListView, view: View, groupPosition: Int, id: Long): Boolean {
        onGroupClickListener?.invoke(view, getGroup(groupPosition))
        return false
    }

    private fun getView(context: Context, convertView: View?, @LayoutRes layoutRes: Int): View {
        return convertView ?: LayoutInflater.from(context).inflate(layoutRes, null, false)
    }

    fun showItems(items: MutableList<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun addItems(items: MutableList<out Item>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun getItems(): MutableList<out Item> = this.items

    fun getItem(id: Long): Item? {
        getItem(items, id)?.let {
            return it
        }
        return null
    }

    private fun getItem(items: List<Item>, id: Long): Item? {
        items.forEach { item ->
            if (item.getId() == id) {
                return item

            } else {
                getItem(item.getChildren() as List<Item>, id)?.let {
                    return it
                }
            }
        }

        return null
    }

    fun addItem(position: Int = 0, item: Item) {
        this.items.add(position, item)
        notifyDataSetChanged()
    }

    fun getGroupAt(position: Int): Item? =
            try {
                items[position]

            } catch (exception: Exception) {
                null
            }

    fun getChildAt(groupPosition: Int, childPosition: Int): Item? =
            try {
                (items[groupPosition].getChildren() as? List<Item>)?.getOrNull(childPosition)

            } catch (exception: Exception) {
                null
            }

    fun removeItem(position: Int) {
        this.items.removeAt(position)
        notifyDataSetChanged()
    }

    fun addOnGroupClickListener(onItemClickListener: OnItemClickListener?) {
        onGroupClickListener = onItemClickListener
    }

    fun addOnChildClickListener(onItemClickListener: OnItemClickListener?) {
        onChildClickListener = onItemClickListener
    }
}

open class ExpandableViewProvider<Item: ExpandableItem>(val groupLayout: Int,
                                  val childLayout: Int) {

    open fun bindGroup(view: View, item: Item, groupPosition: Int, isExpanded: Boolean) {}
    open fun bindChild(view: View, item: Item, groupPosition: Int, childPosition: Int, isLastChild: Boolean) {}
}

interface ExpandableItem {

    fun getId(): Long

    fun getChildren(): List<ExpandableItem>
}
