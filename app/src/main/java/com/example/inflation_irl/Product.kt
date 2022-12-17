package com.example.inflation_irl

import java.sql.Date

data class Product(
    var id: String?,
    var store: Store,
    var barCode: String?,
    var name: String?,
    var price: Double,
    var date: Date,
    var imageFilePath: String,
) {
    companion object {
        const val DATEFORMAT = "dd/MM/yyyy"
    }
}