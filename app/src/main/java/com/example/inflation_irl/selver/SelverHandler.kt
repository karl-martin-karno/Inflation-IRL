package com.example.inflation_irl.selver

import android.content.Context
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.example.inflation_irl.http.RequestHelper

class SelverHandler(context: Context,) {

    private val requestHelper: RequestHelper = RequestHelper(context, Store.SELVER)
    private val selverParser: SelverParser = SelverParser()

    fun getProduct(barCode: String) : Product? {
        requestHelper.getProductPageHtml(barCode) { response ->
            if (response is String) {
                // TODO: Return this
                selverParser.parseProductPage(response)
            }
        }
        return null
    }
}