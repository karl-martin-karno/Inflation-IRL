package com.example.inflation_irl.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.inflation_irl.Product
import com.example.inflation_irl.R
import com.example.inflation_irl.Store
import com.example.inflation_irl.databinding.FragmentProductInfoBinding
import com.example.inflation_irl.prisma.PrismaHandler
import com.example.inflation_irl.selver.SelverHandler


/**
 * A simple [Fragment] subclass.
 * Use the [ProductInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductInfoFragment : Fragment() {

    private var _binding: FragmentProductInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var prismaHandler: PrismaHandler
    private lateinit var selverHandler: SelverHandler

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

        prismaHandler = PrismaHandler(requireContext())
        selverHandler = SelverHandler(requireContext())


        val selectedStore = Store.PRISMA
        val barCode = "4750042304845"
        handleBarCodeFound(barCode, selectedStore)
        return view
    }

    private fun handleBarCodeFound(barCode: String, selectedStore: Store) {
        when (selectedStore) {
            Store.PRISMA -> prismaHandler.getProduct(barCode) { product ->
                handleProductFound(product)
            }
            Store.SELVER -> selverHandler.getProduct(barCode) { product ->
                handleProductFound(product)
            }
        }
    }

    private fun handleProductFound(product: Product) {
        binding.productPriceEditText.setText(product.price.toString())
        binding.productTitleEditText.setText(product.name)
        Log.d("MainActivity", "handleProductFound: $product")
    }
}