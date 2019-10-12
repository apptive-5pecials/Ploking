package com.fivespecial.ploking.maps

import android.location.Location

class Calculation {

    companion object {
        const val MINIMUM_DISTANCE_DIFFERENCE: Int = 1000
    }

    //private val nearBin = ArrayList<BinLocation>()

    fun nearBins(binLocationList: List<BinLocation>?, currentLat: Double, currentLong: Double): Int {

        var count = 0

        binLocationList?.forEach { bin ->

            // Point A
            val locationA: Location = Location("point A").apply {
                latitude = bin.latitude
                longitude = bin.longitude
            }

            // Point B
            val locationB = Location("point B").apply {
                latitude = currentLat
                longitude = currentLong
            }

            if(locationA.distanceTo(locationB) < MINIMUM_DISTANCE_DIFFERENCE) {
                // nearBin.add(bin)

                count++
            }
        }

        return count
    }

}
