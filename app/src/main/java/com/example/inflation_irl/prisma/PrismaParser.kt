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

    suspend fun parseProductPageHtml(html: String, parserHandler: ParserHandler) {
        withContext(IO) {
            val doc = Jsoup.parse(html)
            val name = doc.getElementById("product-name")?.text().toString()
            val price = ((doc.getElementsByClass("whole-number").text() + "."+ doc.getElementsByClass("decimal").text()).toString()).toDouble()
            val bar = doc.getElementsByClass("aisle").text().toString().substringAfterLast(":").trim()
            val thumb = doc.getElementById("product-image-zoom").toString().substringAfter("src=").
            substringBefore("alt=").trim().replace("\"","")

            val product = Product(
                "0",
                Store.PRISMA,
                bar,
                name,
                price,
                Timestamp.now(),
                thumb,
            )

            parserHandler.onParsed(product)
        }
    }
}