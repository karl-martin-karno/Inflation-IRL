package com.example.inflation_irl.selver

import android.content.Context
import com.example.inflation_irl.Store
import com.example.inflation_irl.http.RequestHelper

class SelverHandler(context: Context,) {

    private val requestHelper: RequestHelper = RequestHelper(context, Store.SELVER)
    private val selverParser: SelverParser = SelverParser()

    fun getProduct(barCode: String) {
        requestHelper.getProductPage(barCode) { response ->
            if (response is String) {
                selverParser.parseProductPage(response)
            }
        }
    }
}