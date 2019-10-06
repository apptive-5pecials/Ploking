package com.fivespecial.ploking.maps

import android.location.Location

class Calculation {

    //private val nearBin = ArrayList<BinLocation>()

    fun nearBins(binLocationList: List<BinLocation>, currentLat: Double, currentLong: Double): Int {

        var count = 0

        binLocationList.forEach { bin ->

            if(bin.latitude != null && bin.longitude != null) {

                // Point A
                val locationA: Location = Location("point A").apply {
                    latitude = bin.latitude!!
                    longitude = bin.longitude!!
                }

                // Point B
                val locationB = Location("point B").apply {
                    latitude = currentLat
                    longitude = currentLong
                }

                if(locationA.distanceTo(locationB) < 1000) {
                    // nearBin.add(bin)

                    count++
                }
            }
        }

        return count
    }

}
