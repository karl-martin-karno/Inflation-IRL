package com.example.inflation_irl.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.inflation_irl.R
import com.example.inflation_irl.databinding.FragmentScanBarcodeBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ScanBarcodeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScanBarcodeFragment : Fragment() {
    private var _binding: FragmentScanBarcodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBarcodeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.scanBarcodeButton.setOnClickListener{
            Toast.makeText(requireContext(),"Scan Barcode clicked",Toast.LENGTH_SHORT).show();
        }
        return view
    }
}