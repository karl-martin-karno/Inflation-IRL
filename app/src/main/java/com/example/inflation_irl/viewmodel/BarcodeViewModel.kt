package com.example.inflation_irl.viewmodel

import androidx.lifecycle.ViewModel
import com.example.inflation_irl.Store

class BarcodeViewModel : ViewModel() {
    lateinit var selectedStore: Store
    var shouldAddToDatabase = false

    fun isStoreSelected(): Boolean {
        return ::selectedStore.isInitialized
    }
}