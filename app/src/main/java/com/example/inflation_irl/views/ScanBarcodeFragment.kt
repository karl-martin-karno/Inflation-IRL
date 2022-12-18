package com.example.inflation_irl.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.inflation_irl.R
import com.example.inflation_irl.Store
import com.example.inflation_irl.databinding.FragmentScanBarcodeBinding
import com.example.inflation_irl.location.LocationUtils
import com.example.inflation_irl.permission.PermissionUtils
import com.example.inflation_irl.scanner.BarCodeScanner
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

        val adapter = ArrayAdapter(requireContext(), R.layout.shop_list_item, items)
        binding.shopField.setAdapter(adapter)
        binding.shopField.setText(items[0].name, false)

        // Enable the button if the EditText is not empty
        handleBarcodeEdit()
        binding.findPriceHistoryButton.setOnClickListener { handleQueryProductInfo(view) }

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_LOCATION)
        setupAddPictureButton()
        return view
    }

    private fun handleBarcodeEdit() {
        binding.findPriceHistoryButton.isEnabled = false
        binding.productBarCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                binding.findPriceHistoryButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    private fun handleQueryProductInfo(view: ConstraintLayout) {
        if (binding.shopField.text == null) {
            Toast.makeText(requireContext(), "Please select your shop first", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val barCodeLength = binding.productBarCodeEditText.text.length
        if (barCodeLength < 12 || barCodeLength > 14) {
            Toast.makeText(requireContext(), "Barcode should have 12-14 numbers", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = bundleOf("icon" to R.drawable.red_bull, "store" to R.drawable.prisma, "title" to "some description goes here")
        findNavController(view).navigate(R.id.productInfoFragment2, bundle)
    }

    @SuppressLint("SetTextI18n")
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                CoroutineScope(Main).launch {
                    binding.productBarCodeEditText.setText(getString(R.string.loading_text))
                    imageFilePath?.let {
                        val img = imageUtils.createScaledImage(it)
                        val imageView = binding.productLabelImageView
                        barCodeScanner.findBarcode(img, imageView) { result, isBarCodeFound ->
                            if (!isBarCodeFound) {
                                handleBarCodeNotFound(result)
                            } else {
                                binding.productBarCodeEditText.setText(result)
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
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(arrayOf(permission), requestCode)
        } else {
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION || permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                handleFindNearestStore()
            }
        }
    }

    private fun handleFindNearestStore() {
        CoroutineScope(Main).launch {
            // TODO:
            val store = getActivity()?.let { locationUtils.findNearestStore(it.applicationContext) }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> permissionUtils.handleLocationPermissionsResult(
                requireContext(), grantResults
            ) { handleFindNearestStore() }
            CAMERA_PERMISSION_CODE -> permissionUtils.handleCameraPermissionsResult(
                requireContext(),
                grantResults
            ) { handleImageCaptureIntent() }
        }
    }

    private fun handleBarCodeNotFound(result: String) {
        binding.findPriceHistoryButton.isEnabled = false
        binding.productBarCodeEditText.setText("")
        Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
    }
}