package com.example.inflation_irl.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inflation_irl.Product
import com.example.inflation_irl.R
import com.example.inflation_irl.Store
import com.example.inflation_irl.adapter.ProductInfoItem
import com.example.inflation_irl.adapter.ProductInfoListAdapter
import com.example.inflation_irl.dao.FireStoreDao
import com.example.inflation_irl.databinding.FragmentProductInfoBinding
import com.example.inflation_irl.prisma.PrismaHandler
import com.koushikdutta.ion.Ion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProductInfoFragment : Fragment() {

    private var _binding: FragmentProductInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var prismaHandler: PrismaHandler
    private val fireBaseDao: FireStoreDao = FireStoreDao()
    private lateinit var recyclerView: RecyclerView
    private val dateFormat = SimpleDateFormat("dd. MMM yyyy, HH:mm", Locale.US)

    private var navigationType = ""

    var dataset = emptyArray<ProductInfoItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        val store = arguments?.getString("store") ?: ""
        val barcode = arguments?.getString("barcode") ?: ""
        navigationType = arguments?.getString("navigation") ?: ""

        // store icon
        binding.productInfoStoreIcon.setImageResource(
            when (store) {
                Store.PRISMA.name -> R.drawable.prisma
                Store.SELVER.name -> R.drawable.selver
                Store.MAXIMA.name -> R.drawable.maxima
                Store.KAUBAMAJA.name -> R.drawable.kaubamaja
                else -> throw throw IllegalArgumentException("Unsupported store name provided")
            }
        )

        // barcode parser
        prismaHandler = PrismaHandler(requireContext())

        // Button is different for history view and bar code scanning view
        if (navigationType == "historyView") {
            val button = binding.scanAnotherItemButton
            val parentView: ViewGroup = button.parent as ViewGroup
            parentView.removeView(button)
            val iconUrl = arguments?.getString("iconUrl") ?: ""
            updateItemIcon(iconUrl)
        } else {
            binding.scanAnotherItemButton.setOnClickListener {
                view.findNavController().popBackStack()
            }
        }

        // Set up RecyclerView
        recyclerView = binding.productItemHistoryReacyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = ProductInfoListAdapter(dataset)

        handleBarCodeFound(barcode, store)
        return view
    }

    private fun updateItemIcon(iconUrl: String) {
        Ion.with(binding.productInfoItemIcon)
            .error(R.drawable.default_product_icon)
            .load(iconUrl)
    }

    private fun handleBarCodeFound(barCode: String, selectedStore: String) {
        binding.productInfoPrice.text = getString(R.string.loading_text)
        binding.productInfoTitle.text = ""
        when (selectedStore) {
            Store.PRISMA.name -> prismaHandler.getProduct(barCode) { product ->
                handleProductFound(product)
            }
            else -> Toast.makeText(
                requireContext(),
                "Only Prisma is currently supported",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleProductFound(product: Product) {
        Log.d("ProductInfoFragment", "handleProductFound: $product")
        CoroutineScope(IO).launch {
            product.barCode?.let { barcode ->
                fireBaseDao.getProductsByBarCodeAndStore(barcode, product.store) { products ->
                    if (products.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Previous product history not found",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        dataset = products
                            .map {
                                ProductInfoItem(
                                    it.price.toString(),
                                    dateFormat.format(it.date.toDate())
                                )
                            }.toTypedArray()
                        recyclerView.adapter = ProductInfoListAdapter(dataset)
                    }
                }
            }
            CoroutineScope(Main).launch {
                binding.productInfoPrice.text =
                    getString(R.string.product_price_textView, product.price.toString())
                binding.productInfoTitle.text = product.name
                updateItemIcon(product.imageFilePath)
            }
            if (product.name != "Error when searching for product" && navigationType != "historyView") {
                fireBaseDao.addProduct(product)
            }
        }
    }
}