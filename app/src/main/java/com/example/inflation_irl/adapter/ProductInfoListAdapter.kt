package com.example.inflation_irl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inflation_irl.R


class ProductInfoListAdapter(private val dataList: MutableList<ProductHistory>) :
    RecyclerView.Adapter<ProductInfoListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.date_textview)
        val priceTextView: TextView = view.findViewById(R.id.price_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_info_list_item, parent, false)
        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.dateTextView.text = data.date
        holder.priceTextView.text = data.price
    }

    override fun getItemCount() = dataList.size
}

data class ProductHistory(val price: String, val date: String)