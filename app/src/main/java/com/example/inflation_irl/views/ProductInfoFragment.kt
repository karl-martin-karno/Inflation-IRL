package com.example.inflation_irl.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inflation_irl.Product
import com.example.inflation_irl.R
import com.example.inflation_irl.Store
import com.example.inflation_irl.adapter.HistoryListAdapter
import com.example.inflation_irl.adapter.HistoryListItem
import com.example.inflation_irl.adapter.ProductHistory
import com.example.inflation_irl.adapter.ProductInfoListAdapter
import com.example.inflation_irl.dao.FireStoreDao
import com.example.inflation_irl.databinding.FragmentProductInfoBinding
import com.example.inflation_irl.prisma.PrismaHandler
import com.example.inflation_irl.selver.SelverHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


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
    private val fireBaseDao: FireStoreDao = FireStoreDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        val _store = arguments?.getString("store") ?: ""
        val _barcode = arguments?.getString("barcode") ?: ""
        val navigation = arguments?.getString("navigation") ?: ""

        // TODO delete dummy data
        binding.productInfoItemIcon.setImageResource(R.drawable.red_bull)
        binding.productInfoStoreIcon.setImageResource(R.drawable.prisma)
        binding.productInfoTitle.text = "Doritos Nacho Cheese Flavored Tortilla Chips"
        binding.productInfoPrice.text = "Today: 16.5€"

//        binding.productTitleEditText.focusable = View.NOT_FOCUSABLE
//        binding.productPriceEditText.focusable = View.NOT_FOCUSABLE

        prismaHandler = PrismaHandler(requireContext())
        selverHandler = SelverHandler(requireContext())


        val selectedStore = Store.PRISMA
        val barCode = "4743050000045"
        handleBarCodeFound(barCode, selectedStore)


        if( navigation == "historyView"){
            val button = binding.scanAnotherItemButton
            val parentView: ViewGroup = button.parent as ViewGroup
            parentView.removeView(button)
        }else{
            binding.scanAnotherItemButton.setOnClickListener{
                view.findNavController().popBackStack()
            }
        }

        val dataset = getDummyData()
        val recyclerView = binding.productItemHistoryReacyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = ProductInfoListAdapter(dataset)
        return view
    }

    private fun getDummyData(): MutableList<ProductHistory> {
        val dataset = mutableListOf<ProductHistory>()
        dataset.add(ProductHistory("15.6€", "14. jan 2022"))
        dataset.add(ProductHistory("16.6€", "14. nov 2022"))
        dataset.add(ProductHistory("18.6€", "14. dec 2022"))
        dataset.add(ProductHistory("19.6€", "14. jul 2022"))
        return dataset
    }

    private fun handleBarCodeFound(barCode: String, selectedStore: Store) {
//        binding.productPriceEditText.setText(getString(R.string.loading_text))
//        binding.productTitleEditText.setText(getString(R.string.loading_text))
        when (selectedStore) {
            Store.PRISMA -> prismaHandler.getProduct(barCode) { product ->
                handleProductFound(product)
            }
            else -> Toast.makeText(requireContext(),"Only Prisma is currently supported",Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleProductFound(product: Product) {
//        binding.productPriceEditText.setText(product.price.toString())
//        binding.productTitleEditText.setText(product.name)
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
                        products.forEach {
                            Log.d("ProductInfoFragment", "handleProductFound: $it")
                            // TODO: Show all these product data points with time and price in a list
                        }
                    }
                }
            }
            fireBaseDao.addProduct(product)
        }

    }
}