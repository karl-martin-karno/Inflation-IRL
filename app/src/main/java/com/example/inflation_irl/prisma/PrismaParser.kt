package com.example.inflation_irl.prisma

import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class PrismaParser {

    fun interface ParserHandler {
        fun onParsed(product: Product)
    }

    suspend fun parseProductPageHtml(html: String, barCode: String, parserHandler: ParserHandler) {
        withContext(IO) {
            try {
                val doc = Jsoup.parse(html)
                val name = doc.getElementById("product-name")?.text().toString()
                val priceText = (doc.getElementsByClass("whole-number")
                    .text() + "." + doc.getElementsByClass("decimal").text());
                var price = 0.0
                if (priceText != ".") {
                    price = priceText.toDouble()
                }
                val thumb =
                    doc.getElementById("product-image-zoom").toString().substringAfter("src=")
                        .substringBefore("alt=").trim().replace("\"", "")
                val product = Product(
                    "0",
                    Store.PRISMA,
                    barCode,
                    name,
                    price,
                    Timestamp.now(),
                    thumb,
                )
                parserHandler.onParsed(product)
            } catch (e: Exception) {
                val product = Product(
                    "0",
                    Store.PRISMA,
                    barCode,
                    "Error when searching for product",
                    0.0,
                    Timestamp.now(),
                    "",
                )
                parserHandler.onParsed(product)
            }

        }
    }
}