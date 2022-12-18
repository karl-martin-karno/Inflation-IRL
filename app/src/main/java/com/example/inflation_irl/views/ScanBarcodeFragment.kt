package com.example.inflation_irl.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inflation_irl.Store
import com.example.inflation_irl.databinding.FragmentScanBarcodeBinding
import com.example.inflation_irl.location.LocationUtils
import com.example.inflation_irl.permission.PermissionUtils
import com.example.inflation_irl.prisma.PrismaHandler
import com.example.inflation_irl.scanner.BarCodeScanner
import com.example.inflation_irl.selver.SelverHandler
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


/**
 * A simple [Fragment] subclass.
 * Use the [ScanBarcodeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScanBarcodeFragment : Fragment() {
    private var _binding: FragmentScanBarcodeBinding? = null
    private val binding get() = _binding!!
    private val items = listOf(Store.PRISMA, Store.SELVER)
    private var imageFilePath: String? = null
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val barCodeScanner: BarCodeScanner = BarCodeScanner()
    private val imageUtils: com.example.inflation_irl.image.ImageUtils =
        com.example.inflation_irl.image.ImageUtils()
    private val permissionUtils: PermissionUtils = PermissionUtils()
    private val locationUtils: LocationUtils = LocationUtils()
    private lateinit var prismaHandler: PrismaHandler
    private lateinit var selverHandler: SelverHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBarcodeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.scanBarcodeButton.setOnClickListener{
            Toast.makeText(requireContext(),"Scan Barcode clicked",Toast.LENGTH_SHORT).show();
        }

        prismaHandler = PrismaHandler(requireContext())
        selverHandler = SelverHandler(requireContext())

        return view
    }
}