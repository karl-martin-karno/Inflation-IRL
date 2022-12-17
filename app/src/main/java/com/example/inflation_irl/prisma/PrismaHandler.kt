package com.example.inflation_irl.prisma

import android.content.Context
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.example.inflation_irl.http.RequestHelper

class PrismaHandler(mContext: Context) {

    private val requestHelper: RequestHelper = RequestHelper(mContext, Store.PRISMA)
    private val prismaParser: PrismaParser = PrismaParser()

    fun getProduct(barCode: String): Product? {
        requestHelper.getProductPage(barCode) { response ->
            if (response is String) {
                // TODO: Return this
                prismaParser.parseProductPage(response)
            }
        }
        return null
    }
}