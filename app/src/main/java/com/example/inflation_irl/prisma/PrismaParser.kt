package com.example.inflation_irl.prisma

import android.util.Log
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.sql.Date

class PrismaParser {

    fun interface ParserHandler {
        fun onParsed(product: Product)
    }

    suspend fun parseProductPageHtml(html: String, parserHandler: ParserHandler) {
        withContext(IO) {
            Log.d("PrismaParser", "parse: $html")
            // TODO: Add implementation


            val product = Product(
                "0",
                Store.PRISMA,
                "123456789",
                "Prisma",
                0.35,
                Date.valueOf("2020-01-01"),
                "imgFilePath",
            )

            parserHandler.onParsed(product)
        }
    }
}