package com.example.inflation_irl.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.inflation_irl.R
import com.example.inflation_irl.Store

class GridLayoutDialogFragment(val selectStore: (store: Store) -> Unit) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false

        val view = inflater.inflate(R.layout.select_store_prompt, container, false)
        view.findViewById<ImageView>(R.id.select_store_prisma).setOnClickListener {
            selectStore(Store.PRISMA)
        }
        view.findViewById<ImageView>(R.id.select_store_rimi)
            .setOnClickListener { selectStore(Store.KAUBAMAJA)}
        view.findViewById<ImageView>(R.id.select_store_maxima)
            .setOnClickListener { selectStore(Store.MAXIMA) }
        view.findViewById<ImageView>(R.id.select_store_selver)
            .setOnClickListener { selectStore(Store.SELVER) }
        return view
    }


}