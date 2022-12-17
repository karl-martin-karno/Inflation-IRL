package com.example.inflation_irl

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.inflation_irl.databinding.ActivityMainBinding
import com.example.inflation_irl.image.ImageUtils
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

class MainActivity : AppCompatActivity() {
    // TODO: Use Dagger for dependency injection
    private lateinit var mapLocation: MapLocation
    private var currentLocation: Store? = null
    private lateinit var binding: ActivityMainBinding
    private val items = listOf(Store.PRISMA, Store.SELVER)
    private var imageFilePath: String? = null
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val barCodeScanner: BarCodeScanner = BarCodeScanner()
    private val imageUtils: ImageUtils = ImageUtils()
    private val permissionUtils: PermissionUtils = PermissionUtils()
    private val locationUtils: LocationUtils = LocationUtils()
    private val prismaHandler: PrismaHandler = PrismaHandler(this)
    private val selverHandler: SelverHandler = SelverHandler(this)

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val CAMERA_PERMISSION_CODE = 98
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                CoroutineScope(Main).launch {
                    imageFilePath?.let {
                        val img = imageUtils.createScaledImage(it)
                        binding.productLabelImageView.setImageBitmap(img)
                        barCodeScanner.findBarcode(img) { result, isBarCodeFound ->
                            if (!isBarCodeFound) {
                                handleBarCodeNotFound(result)
                            } else {
                                when (Store.valueOf(binding.shopField.text.toString())) {
                                    Store.PRISMA -> prismaHandler.getProduct(result)
                                    Store.SELVER -> selverHandler.getProduct(result)
                                }
                                binding.productTitleEditText.setText(result)
                            }
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_LOCATION)
        mapLocation = MapLocation(this)

        val adapter = ArrayAdapter(this, R.layout.shop_list_item, items)
        binding.shopField.setAdapter(adapter)

        // Create a new user with a first, middle, and last name
        setupAddPictureButton()
    }

    private fun setupAddPictureButton() {
        binding.buttonAddPicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                handleImageCaptureIntent()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION || permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                handleFindNearestStore()
            }
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
                this, grantResults
            ) { handleFindNearestStore() }
            CAMERA_PERMISSION_CODE -> permissionUtils.handleCameraPermissionsResult(
                this,
                grantResults
            ) { handleImageCaptureIntent() }
        }
    }

    private fun handleFindNearestStore() {
        CoroutineScope(Main).launch {
            val store = locationUtils.findNearestStore()
            store?.let {
                binding.shopField.setText(store.name, false)
            }
        }
    }

    private fun handleImageCaptureIntent() {
        CoroutineScope(Main).launch {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val photoFile = imageUtils.getPhotoFile(applicationContext, "$timestamp.png")
            val fileUri =
                FileProvider.getUriForFile(
                    applicationContext,
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
        Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
        binding.productTitleEditText.setText("")
        binding.productPriceEditText.setText("")
    }
}