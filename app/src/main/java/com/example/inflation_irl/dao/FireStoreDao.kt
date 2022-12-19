package com.example.inflation_irl.dao

import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class FireStoreDao {

    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val COLLECTION_PRODUCTS = "products"
    }

    fun interface ProductQueryHandler {
        fun onQueryResponse(response: List<Product>)
    }

    suspend fun getProducts(productQueryHandler: ProductQueryHandler) {
        withContext(IO) {
            db.collection(COLLECTION_PRODUCTS)
                .get()
                .addOnSuccessListener { result ->
                    val products = result.map { document ->
                        mapDocumentToProduct(document)
                    }.sortedBy { it.date }.toList()
                    productQueryHandler.onQueryResponse(products)
                }
                .addOnFailureListener { productQueryHandler.onQueryResponse(listOf()) }
        }
    }

    suspend fun getProductsByBarCodeAndStore(barCode: String, store: Store, productQueryHandler: ProductQueryHandler) {
        withContext(IO) {
            db.collection(COLLECTION_PRODUCTS)
//                .orderBy("date", Query.Direction.DESCENDING) // TODO request fails with this
                .whereEqualTo("barCode", barCode)
                .whereEqualTo("store", store)
                .get()
                .addOnSuccessListener { result ->
                    val products = result.map { document ->
                        mapDocumentToProduct(document)
                    }.sortedBy { it.date }.toList()
                    productQueryHandler.onQueryResponse(products)
                }
                .addOnFailureListener { productQueryHandler.onQueryResponse(listOf()) }
        }
    }

    private fun mapDocumentToProduct(document: QueryDocumentSnapshot): Product {
        return Product(
            document.id,
            Store.valueOf(document.data["store"] as String? ?: ""),
            document.data["barCode"] as String? ?: "",
            document.data["name"] as String? ?: "",
            document.data["price"] as Double? ?: 0.0,
            document["date"] as com.google.firebase.Timestamp,
            document.data["imageFilePath"] as String? ?: "",
        )
    }

    suspend fun addProduct(product: Product) {
        withContext(IO) {
            db.collection(COLLECTION_PRODUCTS).add(product)
        }
    }
}