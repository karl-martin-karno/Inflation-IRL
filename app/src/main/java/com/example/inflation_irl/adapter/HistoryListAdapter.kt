package com.example.inflation_irl.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inflation_irl.R


class HistoryListAdapter(private val context: Context, private val dataset: MutableList<HistoryListItem>) :
    RecyclerView.Adapter<HistoryListAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.history_list_item_name)
        val storeIcon: ImageView = view.findViewById(R.id.history_list_item_store_icon)
        val itemIcon: ImageView = view.findViewById(R.id.history_list_item_icon)
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = item.name
        holder.storeIcon.setImageResource(item.store)
        holder.itemIcon.setImageResource(item.icon)

    }

    override fun getItemCount() = dataset.size
}

data class HistoryListItem(val name: String, val icon: Int, val store: Int)

