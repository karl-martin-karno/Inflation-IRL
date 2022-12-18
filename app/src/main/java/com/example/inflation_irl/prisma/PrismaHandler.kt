package com.example.inflation_irl.prisma

import android.content.Context
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.example.inflation_irl.http.RequestHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

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
                        prismaParser.parseProductPageHtml(htmlResponse) { product ->
                            productHandler.onResponse(product)
                        }
                    }
                }
            }
        }
    }
}