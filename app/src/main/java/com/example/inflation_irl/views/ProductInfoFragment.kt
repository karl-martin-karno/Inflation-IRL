package com.example.inflation_irl.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.inflation_irl.Product
import com.example.inflation_irl.R
import com.example.inflation_irl.Store
import com.example.inflation_irl.databinding.FragmentProductInfoBinding
import com.example.inflation_irl.databinding.FragmentScanBarcodeBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ProductInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductInfoFragment : Fragment() {

    private var _binding: FragmentProductInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        val icon = arguments?.getInt("icon") ?: R.drawable.red_bull
        val store = arguments?.getInt("store") ?: R.drawable.rimi
        val title = arguments?.getString("title") ?: ""

        binding.productInfoItemIcon.setImageResource(icon)
        binding.productInfoStoreIcon.setImageResource(store)
        binding.productInfoTitle.text = title
        return view
    }

}