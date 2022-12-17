package com.example.inflation_irl.selver

import android.util.Log
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.sql.Date

class SelverParser {

    fun interface ParserHandler {
        fun onParsed(product: Product)
    }

    suspend fun parseProductPageHtml(html: String, parserHandler: ParserHandler) {
        withContext(IO) {
            Log.d("PrismaParser", "parse: $html")
            // TODO: Add implementation

            val product = Product(
                "0",
                Store.SELVER,
                "123456789",
                "Coca-Cola",
                1.35,
                Date.valueOf("2020-01-01"),
                "imgFilePath",
            )

            parserHandler.onParsed(product)
        }
    }
}