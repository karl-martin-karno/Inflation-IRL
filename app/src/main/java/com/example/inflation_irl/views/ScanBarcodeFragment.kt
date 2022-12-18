package com.example.inflation_irl.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.inflation_irl.R


/**
 * A simple [Fragment] subclass.
 * Use the [ScanBarcodeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScanBarcodeFragment : Fragment() {

    private lateinit var binding: ScanBarcodeFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan_barcode, container, false)

        view.findViewById<Button>(R.id.scan_barcode_button).setOnClickListener{
            Toast.makeText(requireContext(),"Scan Barcode clicked",Toast.LENGTH_SHORT).show();
        }
        return view
    }

}