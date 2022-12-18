package com.example.inflation_irl.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.inflation_irl.R

class GridLayoutDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // inflate the grid layout view using the .xml file
        return inflater.inflate(R.layout.select_store_prompt, container, false)
    }
}