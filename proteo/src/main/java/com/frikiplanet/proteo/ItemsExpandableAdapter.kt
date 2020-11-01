package com.frikiplanet.proteo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.annotation.LayoutRes
import java.lang.Exception

class ItemsExpandableAdapter(private val expandableViewProvider: ExpandableViewProvider) : BaseExpandableListAdapter(),
        ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    private var items: MutableList<ExpandableItem> = mutableListOf()

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
        val group = getGroup(groupPosition) as ExpandableItem
        val view = getView(parent.context, convertView, expandableViewProvider.groupLayout)
        expandableViewProvider.bindGroup(view, group, groupPosition, isExpanded)
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val child = getChild(groupPosition, childPosition) as ExpandableItem
        val view = getView(parent.context, convertView, expandableViewProvider.childLayout)
        expandableViewProvider.bindChild(view, child, groupPosition, childPosition, isLastChild)
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun onChildClick(parent: ExpandableListView, view: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        onChildClickListener?.onItemClick(view, getChild(groupPosition, childPosition))
        return false
    }

    override fun onGroupClick(parent: ExpandableListView, view: View, groupPosition: Int, id: Long): Boolean {
        onGroupClickListener?.onItemClick(view, getGroup(groupPosition))
        return false
    }

    private fun getView(context: Context, convertView: View?, @LayoutRes layoutRes: Int): View {
        return convertView ?: LayoutInflater.from(context).inflate(layoutRes, null, false)
    }

    fun showItems(items: MutableList<ExpandableItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun addItems(items: MutableList<out ExpandableItem>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun getItems(): MutableList<out ExpandableItem> = this.items

    fun getItem(id: Long): ExpandableItem? {
        getItem(items, id)?.let {
            return it
        }
        return null
    }

    private fun getItem(items: List<ExpandableItem>, id: Long): ExpandableItem? {
        items.forEach { item ->
            if (item.getId() == id) {
                return item

            } else {
                getItem(item.getChildren(), id)?.let {
                    return it
                }
            }
        }

        return null
    }

    fun addItem(position: Int = 0, item: ExpandableItem) {
        this.items.add(position, item)
        notifyDataSetChanged()
    }

    fun getGroupAt(position: Int): ExpandableItem? =
            try {
                items[position]

            } catch (exception: Exception) {
                null
            }

    fun getChildAt(groupPosition: Int, childPosition: Int): ExpandableItem? =
            try {
                items[groupPosition].getChildren()[childPosition]

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

open class ExpandableViewProvider(val groupLayout: Int,
                                  val childLayout: Int) {

    open fun bindGroup(view: View, item: ExpandableItem, groupPosition: Int, isExpanded: Boolean) {}
    open fun bindChild(view: View, item: ExpandableItem, groupPosition: Int, childPosition: Int, isLastChild: Boolean) {}
}

interface ExpandableItem {

    fun getId(): Long

    fun getChildren(): List<ExpandableItem>
}
