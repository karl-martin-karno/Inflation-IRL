package com.example.inflation_irl.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.inflation_irl.Product
import com.example.inflation_irl.R
import com.example.inflation_irl.Store
import com.example.inflation_irl.adapter.HistoryListAdapter
import com.example.inflation_irl.adapter.HistoryListItem
import com.example.inflation_irl.dao.FireStoreDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {

    private val fireStoreDao = FireStoreDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        CoroutineScope(IO).launch {
            fireStoreDao.getProducts { products ->
                val dataset = mapDataSetToHistoryList(products)
                val recyclerView = view.findViewById<RecyclerView>(R.id.history_recyclerview)
                recyclerView.adapter = HistoryListAdapter(dataset)
                recyclerView.setHasFixedSize(true)
            }
        }
        return view
    }

    private fun mapDataSetToHistoryList(products: List<Product>): MutableList<HistoryListItem> {
        val dataset = mutableListOf<HistoryListItem>()
        val usedBarCodes = mutableListOf<String>()
        products.forEach { product ->
            if (product.barCode !in dataset.map { it.barcode }) {
                dataset.add(
                    HistoryListItem(
                        product.barCode ?: "",
                        product.name.toString(),
                        product.imageFilePath,
                        product.store.toString()
                    )
                )
            }
            usedBarCodes.add(product.barCode ?: "")
        }
        return dataset
    }
}