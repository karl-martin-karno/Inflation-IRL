package com.example.inflation_irl.http

import android.content.Context
import android.util.Log
import com.example.inflation_irl.Store
import com.koushikdutta.ion.Ion

class RequestHelper(
    private val mContext: Context,
    private val store: Store
) {

    companion object {
        const val PRISMA_BASE_URL = "https://prismamarket.ee/entry/"
        const val SELVER_BASE_URL = "https://www.selver.ee/search?q="
        const val KAUBAMAJA_BASE_URL = "https://www.kaubamaja.ee/search/?q="
        const val WAYBACK_MACHINE_BASE_URL = "https://archive.org/wayback/available?url="
    }

    fun interface RequestHandler {
        fun onResponse(t: Any)
    }

    private fun combineWithBaseUrl(barCode: String): String {
        return when (store) {
            Store.PRISMA -> PRISMA_BASE_URL + barCode
            Store.SELVER -> SELVER_BASE_URL + barCode
            Store.MAXIMA -> PRISMA_BASE_URL + barCode
            Store.KAUBAMAJA -> KAUBAMAJA_BASE_URL + barCode
        }
    }

    fun getProductPageHtmlByUrl(url: String, requestHandler: RequestHandler) {

        Ion.with(mContext)
            .load(url)
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    requestHandler.onResponse(result)
                } else {
                    requestHandler.onResponse(e)
                }
            }

    }

    fun getProductPageHtmlByBarCode(barCode: String, requestHandler: RequestHandler) {
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

    fun getProductPageSnapshots(barCode: String, requestHandler: RequestHandler) {
        val url = WAYBACK_MACHINE_BASE_URL + combineWithBaseUrl(barCode)
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

    fun getInflationRateInEstonia(requestHandler: RequestHandler) {
        val url = "https://api.api-ninjas.com/v1/inflation?country=Estonia"
        Ion.with(mContext)
            .load(url)
            .setHeader("X-Api-Key", "OE1X8aWGiPtCw/LdPLPPTA==ZKmrdrF07epTFe0q")
            .asString()
            .setCallback { e, htmlResponse ->
                Log.d("RequestHelper", "getInflationRateInEstonia: $htmlResponse")
                if (e == null) {
                    requestHandler.onResponse(htmlResponse)
                }
            }

    }
}
