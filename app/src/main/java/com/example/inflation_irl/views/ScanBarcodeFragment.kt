package com.example.inflation_irl.views

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.inflation_irl.Product
import com.example.inflation_irl.R
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


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

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val CAMERA_PERMISSION_CODE = 98
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBarcodeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.scanBarcodeButton.setOnClickListener {
            Toast.makeText(requireContext(), "Scan Barcode clicked", Toast.LENGTH_SHORT).show();
        }

        prismaHandler = PrismaHandler(requireContext())
        selverHandler = SelverHandler(requireContext())
        val adapter = ArrayAdapter(requireContext(), R.layout.shop_list_item, items)
        binding.shopField.setAdapter(adapter)
        binding.findPriceHistoryButton.isEnabled = false
        binding.shopField.setText(items[0].name, false)

        setupAddPictureButton()
        return view
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                CoroutineScope(Main).launch {
                    binding.productTitleEditText.setText("Loading...")
                    binding.productPriceEditText.setText("Loading...")
                    binding.productBarCodeEditText.setText("Loading...")
                    imageFilePath?.let {
                        val img = imageUtils.createScaledImage(it)
                        binding.productLabelImageView.setImageBitmap(img)
                        barCodeScanner.findBarcode(img) { result, isBarCodeFound ->
                            if (!isBarCodeFound) {
                                handleBarCodeNotFound(result)
                            } else {
                                handleBarCodeFound(result)
                            }
                        }
                    }
                }
            }
        }

    private fun setupAddPictureButton() {
        binding.scanBarcodeButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                handleImageCaptureIntent()
            } else {
                ActivityCompat.requestPermissions(
                    requireContext() as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    private fun handleBarCodeFound(barCode: String) {
        // TODO: Why does this not work
        if (binding.shopField.text == null) {
            Toast.makeText(requireContext(), "Please select your shop first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.findPriceHistoryButton.isEnabled = true

        when (Store.valueOf(binding.shopField.text.toString())) {
            Store.PRISMA -> prismaHandler.getProduct(barCode) { product ->
                handleProductFound(product)
            }
            Store.SELVER -> selverHandler.getProduct(barCode) { product ->
                handleProductFound(product)
            }
        }
    }

    private fun handleProductFound(product: Product) {
        binding.productBarCodeEditText.setText(product.barCode)
        binding.productPriceEditText.setText(product.price.toString())
        binding.productTitleEditText.setText(product.name)
        Log.d("MainActivity", "handleProductFound: $product")
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // TODO: Request permission
            requestPermissions(arrayOf(permission), requestCode)
        } else {
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION || permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                // handleFindNearestStore()
                // TODO
            }
        }
    }

    private fun handleFindNearestStore() {
        CoroutineScope(Main).launch {
            val store = locationUtils.findNearestStore()
        }
    }

    private fun handleImageCaptureIntent() {
        CoroutineScope(Main).launch {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val photoFile = imageUtils.getPhotoFile(requireContext(), "$timestamp.png")
            val fileUri =
                FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.inflation_irl.fileprovider",
                    photoFile
                )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

            // https://stackoverflow.com/questions/1910608/android-action-image-capture-intent
            imageFilePath = photoFile.toString()
            resultLauncher.launch(intent)
        }
    }

    private fun handleBarCodeNotFound(result: String) {
        binding.findPriceHistoryButton.isEnabled = false
        binding.productBarCodeEditText.setText("")
        binding.productPriceEditText.setText("")
        binding.productTitleEditText.setText("")
        Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
    }
}