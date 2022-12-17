package com.example.inflation_irl.selver

import android.content.Context
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.example.inflation_irl.http.RequestHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelverHandler(context: Context,) {

    private val requestHelper: RequestHelper = RequestHelper(context, Store.SELVER)
    private val selverParser: SelverParser = SelverParser()

    fun interface ProductHandler {
        fun onResponse(t: Any)
    }

    fun getProduct(barCode: String, productHandler: ProductHandler){
        CoroutineScope(Dispatchers.IO).launch {
            requestHelper.getProductPageHtml(barCode) { htmlResponse ->
                if (htmlResponse is String) {
                    CoroutineScope(Dispatchers.IO).launch {
                        selverParser.parseProductPageHtml(htmlResponse) { product ->
                            productHandler.onResponse(product)
                        }
                    }
                }
            }
        }
    }
}