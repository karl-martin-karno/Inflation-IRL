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

class GridLayoutDialogFragment(val selectStore: (store:Store) -> Unit) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // inflate the grid layout view using the .xml file
        isCancelable = false

        val view = inflater.inflate(R.layout.select_store_prompt, container, false)
        view.findViewById<ImageView>(R.id.select_store_prisma).setOnClickListener{
            Toast.makeText(requireContext(),"Toggle prompt:", Toast.LENGTH_SHORT).show()
            selectStore(Store.PRISMA)
            dismiss()
        }
//        view.findViewById<ImageView>(R.id.select_store_rimi).setOnClickListener{}
//        view.findViewById<ImageView>(R.id.select_store_maxima).setOnClickListener{}
//        view.findViewById<ImageView>(R.id.select_store_selver).setOnClickListener{}
        return view
    }
}