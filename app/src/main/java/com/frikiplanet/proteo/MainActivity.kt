package com.frikiplanet.proteo.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.frikiplanet.proteo.adapters.ItemsAdapter


class MainActivity : AppCompatActivity() {

    private val adapter: ItemsAdapter<String> by lazy {
        ItemsAdapter(object : ItemsAdapter.ViewHolderProvider<String>() {

            override val itemViewHolder: (parent: ViewGroup, viewType: Int) -> ItemsAdapter.ItemViewHolder<String>
                get() = { parent, viewType ->
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.row_sample_text, parent, false)
                    object : ItemsAdapter.ItemViewHolder<String>(view) {
                        override fun bind(value: String, position: Int) {
                            itemView.findViewById<TextView>(R.id.label)?.text = position.toString()
                        }
                    }
                }

            override val diffUtilCallback: DiffUtil.ItemCallback<String>
                get() = object : DiffUtil.ItemCallback<String>() {
                    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                        return oldItem == newItem
                    }

                    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                        return oldItem == newItem
                    }
                }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.items).let { recyclerView ->
            recyclerView.adapter = adapter
            adapter.submitList(listOf("1", "2", "3", "4", "5"))
        }

        findViewById<AppCompatButton>(R.id.add)?.let { addButton ->
            addButton.setOnClickListener {
                adapter.submitList(adapter.currentList.toMutableList().apply { add("10") })
            }
        }

        findViewById<AppCompatButton>(R.id.remove)?.let { removeButton ->
            removeButton.setOnClickListener {
                adapter.submitList(adapter.currentList.toMutableList().apply { removeLastOrNull(); })
            }
        }
    }
}