package com.example.inflation_irl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.inflation_irl.R
import com.example.inflation_irl.Store
import com.koushikdutta.ion.Ion


class HistoryListAdapter(private val dataset: MutableList<HistoryListItem>) :
    RecyclerView.Adapter<HistoryListAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.history_list_item_name)
        val storeIcon: ImageView = view.findViewById(R.id.history_list_item_store_icon)
        val itemIcon: ImageView = view.findViewById(R.id.history_list_item_icon)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = item.title
        holder.storeIcon.setImageResource(
            when (item.store) {
                Store.PRISMA.name -> R.drawable.prisma
                Store.KAUBAMAJA.name -> R.drawable.kaubamaja
                Store.MAXIMA.name -> R.drawable.maxima
                Store.SELVER.name -> R.drawable.selver
                else -> throw IllegalArgumentException("Unsupported store name provided")
            }
        )
        Ion.with(holder.itemIcon)
            .error(R.drawable.default_history_list_icon)
            .load(item.iconUrl)

        holder.view.setOnClickListener {
            val bundle = bundleOf("store" to item.store, "barcode" to item.barcode, "navigation" to "historyView")
            holder.view.findNavController().navigate(R.id.productInfoFragment, bundle)
        }
    }

    override fun getItemCount() = dataset.size
}

data class HistoryListItem(
    val barcode: String,
    val title: String,
    val iconUrl: String,
    val store: String
)

