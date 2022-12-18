package com.example.inflation_irl.selver

import android.util.Log
import com.example.inflation_irl.Product
import com.example.inflation_irl.Store
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class SelverParser {

    fun interface ParserHandler {
        fun onParsed(product: Product)
    }

    suspend fun parseProductPageHtml(html: String, parserHandler: ParserHandler) {
        withContext(IO) {
            Log.d("SelverParser", "parse: $html")
            val doc = Jsoup.parse(html)
            val name: String = doc.getElementsByClass("page-title")[0].text()
            val price: Double = doc.getElementsByClass("price")[1].text().split(" ")[0].replace(',', '.').toDouble()
            val bar: String = doc.getElementsByClass("data")[0].text()

            // Pilt ei ole salvestatud wayback machines aga link hangitud siiski
            val thumb: String = doc.getElementsByAttributeValue("title", name).toString().split("\"")[1]

            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date = Calendar.getInstance().time
            val current = formatter.format(date)

            val product = Product(
                "0",
                Store.SELVER,
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