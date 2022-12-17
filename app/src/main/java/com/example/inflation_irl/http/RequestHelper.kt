package com.example.inflation_irl.http

import android.content.Context
import com.example.inflation_irl.Store
import com.koushikdutta.ion.Ion

class RequestHelper(
    private val mContext: Context,
    private val store: Store
) {

    companion object {
        const val PRISMA_BASE_URL = "https://prismamarket.ee/entry/"
        const val SELVER_BASE_URL = "https://www.selver.ee/search?q="
    }

    fun interface RequestHandler {
        fun onResponse(t: Any)
    }

    private fun combineWithBaseUrl(barCode: String): String {
        return when (store) {
            Store.PRISMA -> PRISMA_BASE_URL + barCode
            Store.SELVER -> SELVER_BASE_URL + barCode
        }
    }

    fun getProductPage(barCode: String, requestHandler: RequestHandler) {
        val url = combineWithBaseUrl(barCode)
        Ion.with(mContext)
            .load(url)
            .asString()
            .setCallback { e, htmlResponse ->
                if (e != null) {
                    requestHandler.onResponse(e)
                } else {
                    requestHandler.onResponse(htmlResponse)
                }
            }
    }
}