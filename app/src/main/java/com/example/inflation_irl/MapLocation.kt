package com.example.inflation_irl

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MapLocation(private val applicationContext: Context) {
    val TAG = "MyMapsActivity"
    val acceptedPlaces: Array<String> = arrayOf("selver", "prisma")

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Store? {
        val match = withContext(IO) {
            Places.initialize(applicationContext, "AIzaSyAViygAFms699z-JFoWsDB9MzVTegVHfQ4")
            val placesClient = Places.createClient(applicationContext)
            val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
            val placeResponse = placesClient.findCurrentPlace(request)
            var finalMatch: Store? = null
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    val placesFound = response.placeLikelihoods.map { it.place.name }
                    run lit@{
                        placesFound.forEach { place ->
                            val match =
                                acceptedPlaces.filter { place.contains(it, ignoreCase = true) }
                            if (match.size > 0) {
                                Log.i(TAG, place)
                                when (match.get(0)) {
                                    "selver" -> finalMatch = Store.SELVER
                                    "prisma" -> finalMatch = Store.PRISMA
                                }
                                return@lit;
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
            Tasks.await(placeResponse);
            delay(1000) // Idiootsus kuna ma ei tea kuidas siin funktsioonis mingi Promise moodi asja tekitada :)
            Log.i(TAG, finalMatch.toString())
            finalMatch
        }
        return match;
    }

    fun stringContainsElementFromArray(string: String, array: Array<String>): Boolean {
        array.forEach {
            if (string.contains(it, ignoreCase = true)) return true;
        }
        return false;
    }
}