package com.example.inflation_irl.location

import com.example.inflation_irl.Store
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class LocationUtils {

    suspend fun findNearestStore(): Store? {
        return withContext(IO) {
            // TODO: Implement this correctly to find the nearest store based on our location
            // Currently this is hardcoded
            Store.PRISMA
        }
    }
}