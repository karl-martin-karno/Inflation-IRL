package com.example.inflation_irl

import com.google.firebase.Timestamp

data class Product(
    var id: String?,
    var store: Store,
    var barCode: String?,
    var name: String?,
    var price: Double,
    var date: Timestamp,
    var imageFilePath: String,
)