package com.example.inflation_irl.prisma

import android.util.Log
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.sql.Date
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*


class PrismaParser {

    fun interface ParserHandler {
        fun onParsed(product: Product)
    }

    suspend fun parseProductPageHtml(html: String, parserHandler: ParserHandler) {
        withContext(IO) {
            Log.d("PrismaParser", "parse: $html")
            val doc = Jsoup.parse(html)
            val name = doc.getElementById("product-name")?.text().toString()
            val price = ((doc.getElementsByClass("whole-number").text() + "."+ doc.getElementsByClass("decimal").text()).toString()).toDouble()
            val bar = doc.getElementsByClass("aisle").text().toString().substringAfterLast(":").trim()

            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date = Calendar.getInstance().time
            val current = formatter.format(date)

            val thumb = doc.getElementById("product-image-zoom").toString().substringAfter("src=").
            substringBefore("alt=").trim().replace("\"","")

            val product = Product(
                "0",
                Store.PRISMA,
                bar,
                name,
                price,
                Date.valueOf(current),
                thumb,
            )

            parserHandler.onParsed(product)
        }
    }
}