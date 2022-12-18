package com.example.inflation_irl.dao

import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreDao {

    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val COLLECTION_PRODUCTS = "products"
    }

    fun interface ProductQueryHandler {
        fun onQueryResponse(response: List<Product>)
    }

    fun getProducts(productQueryHandler: ProductQueryHandler) {
        db.collection(COLLECTION_PRODUCTS)
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { document ->
                    Product(
                        document.id,
                        Store.PRISMA, // FIXME
                        document.data["barCode"] as String? ?: "",
                        document.data["name"] as String? ?: "",
                        1.1,
                        document["date"] as com.google.firebase.Timestamp,
                        document.data["imageFilePath"] as String? ?: "",
                        )
                }.sortedBy { it.date }.toList()
                productQueryHandler.onQueryResponse(products)
            }
            .addOnFailureListener { productQueryHandler.onQueryResponse(listOf<Product>()) }
    }

    fun addProduct(product: Product) {
        db.collection(COLLECTION_PRODUCTS).add(product)
    }
}