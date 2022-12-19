package com.example.inflation_irl.prisma

import android.content.Context
import android.util.Log
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.example.inflation_irl.http.RequestHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONArray

class PrismaHandler(mContext: Context) {

    private val requestHelper: RequestHelper = RequestHelper(mContext, Store.PRISMA)
    private val prismaParser: PrismaParser = PrismaParser()

    fun interface ProductHandler {
        fun onResponse(product: Product)
    }

    fun getProduct(barCode: String, productHandler: ProductHandler) {
        CoroutineScope(IO).launch {
            requestHelper.getProductPageHtmlByBarCode(barCode) { htmlResponse ->
                if (htmlResponse is String) {
                    CoroutineScope(IO).launch {
                        prismaParser.parseProductPageHtml(htmlResponse, barCode) { product ->
                            productHandler.onResponse(product)
                        }
                    }
                }
            }
        }
    }

    fun interface InflationHandler {
        fun onInflationReceived(inflation: Double)
    }

    fun getInflationRateInEstonia(inflationHandler: InflationHandler) {
        requestHelper.getInflationRateInEstonia { response ->
            if (response is String) {
                val json = JSONArray(response)
                val inflationRate = json.getJSONObject(0).getDouble("yearly_rate_pct")
                inflationHandler.onInflationReceived(inflationRate)
            }
        }
    }
}