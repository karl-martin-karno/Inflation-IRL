package com.example.inflation_irl.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inflation_irl.R
import com.example.inflation_irl.adapter.HistoryListAdapter
import com.example.inflation_irl.adapter.HistoryListItem


/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        val dataset = getDummyData()
        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recyclerview)
        recyclerView.adapter = HistoryListAdapter(dataset)
        return view
    }

    // TODO replace with query
    private fun getDummyData(): MutableList<HistoryListItem> {
        val dataset = mutableListOf<HistoryListItem>()
        dataset.add(
            HistoryListItem(
                title = "Doritos Nacho Cheese Flavored Tortilla Chips",
                iconUrl = "https://s3-eu-west-1.amazonaws.com/balticsimages/images/180x220/3f4dac76479e9d075db6b662f46c251b.png",
                store = "Prisma"
            )
        )
        dataset.add(
            HistoryListItem(
                title = "Monster Energy Zero Sugar Energy Drink",
                iconUrl = "https://s3-eu-west-1.amazonaws.com/balticsimages/images/180x220/89e4630f3cf26fb4b0c620e27eb01134.png",
                store = "Prisma"
            )
        )
        dataset.add(
            HistoryListItem(
                title = "DiGiorno Traditional Crust Pepperoni Frozen Pizza",
                iconUrl = "https://s3-eu-west-1.amazonaws.com/balticsimages/images/180x220/330c6cdbf139ec891d2d6af1b637b88d.png",
                store = "Rimi"
            )
        )
        dataset.add(
            HistoryListItem(
                title = "Red Bull Energy Drink 4x 0.25L",
                iconUrl = "https://s3-eu-west-1.amazonaws.com/balticsimages/images/180x220/77d9d03eec79bfb50882db7cff50f090.png",
                store = "Rimi"
            )
        )
        return dataset
    }
}