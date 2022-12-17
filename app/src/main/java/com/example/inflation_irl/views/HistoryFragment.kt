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
                icon = R.drawable.doritos,
                store = R.drawable.prisma
            )
        )
        dataset.add(
            HistoryListItem(
                title = "Monster Energy Zero Sugar Energy Drink",
                icon = R.drawable.monster_energy_drink,
                store = R.drawable.prisma
            )
        )
        dataset.add(
            HistoryListItem(
                title = "DiGiorno Traditional Crust Pepperoni Frozen Pizza",
                icon = R.drawable.pizza,
                store = R.drawable.rimi
            )
        )
        dataset.add(
            HistoryListItem(
                title = "Red Bull Energy Drink 4x 0.25L",
                icon = R.drawable.red_bull,
                store = R.drawable.rimi
            )
        )
        return dataset
    }
}