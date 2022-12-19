package com.example.inflation_irl.location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.inflation_irl.Store
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class LocationUtils {
    private val TAG = "LocationUtils"
    private val acceptedPlaces: Array<String> = arrayOf("selver", "prisma")

    fun interface LocationHandler {
        fun onStoreFound(store: Store)
    }

    @SuppressLint("MissingPermission")
    fun findNearestStore(context: Context, locationHandler: LocationHandler) {
        CoroutineScope(IO).launch {
            Places.initialize(context, "AIzaSyAViygAFms699z-JFoWsDB9MzVTegVHfQ4")
            val placesClient = Places.createClient(context)
            val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    val placesFound = response.placeLikelihoods.map { it.place.name }
                    placesFound.forEach { place ->
                        val match =
                            acceptedPlaces.filter {
                                place.lowercase().contains(it, ignoreCase = true)
                            }
                        if (match.isNotEmpty()) {
                            Log.i(TAG, place)
                            when (match.get(0)) {
                                "selver" -> locationHandler.onStoreFound(Store.SELVER)
                                "prisma" -> locationHandler.onStoreFound(Store.PRISMA)
                            }
                        }
                    }

                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.statusCode}")
                    }
                }
            }
        }
    }
}