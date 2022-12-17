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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        val dataset = getDummyData()
        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recyclerview)
        recyclerView.adapter = HistoryListAdapter(dataset)

        // TODO navigate to product info on item click
//        view.findViewById<Button>(R.id.bout_btn).setOnClickListener {
//            findNavController().navigate(R.id.action_title_to_about)
//        }
        return view
    }


    private fun getDummyData(): MutableList<HistoryListItem> {
        val dataset = mutableListOf<HistoryListItem>()
        dataset.add(
            HistoryListItem(
                name = "Doritos Nacho Cheese Flavored Tortilla Chips",
                icon = R.drawable.doritos,
                store = R.drawable.prisma
            )
        )
        dataset.add(
            HistoryListItem(
                name = "Monster Energy Zero Sugar Energy Drink",
                icon = R.drawable.monster_energy_drink,
                store = R.drawable.prisma
            )
        )
        dataset.add(
            HistoryListItem(
                name = "DiGiorno Traditional Crust Pepperoni Frozen Pizza",
                icon = R.drawable.pizza,
                store = R.drawable.rimi
            )
        )
        dataset.add(
            HistoryListItem(
                name = "Red Bull Energy Drink 4x 0.25L",
                icon = R.drawable.red_bull,
                store = R.drawable.rimi
            )
        )
        return dataset
    }
}