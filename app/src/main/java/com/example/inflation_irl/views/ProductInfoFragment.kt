package com.example.inflation_irl.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.inflation_irl.R


/**
 * A simple [Fragment] subclass.
 * Use the [ProductInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_info, container, false)
        val icon = arguments?.getInt("icon") ?: R.drawable.red_bull
        val store = arguments?.getInt("store") ?: R.drawable.rimi
        val title = arguments?.getString("title") ?: ""

        view.findViewById<ImageView>(R.id.product_info_item_icon).setImageResource(icon)
        view.findViewById<ImageView>(R.id.product_info_store_icon).setImageResource(store)
        view.findViewById<TextView>(R.id.product_info_title).text = title
        return view
    }

}