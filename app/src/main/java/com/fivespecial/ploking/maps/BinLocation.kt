package com.fivespecial.ploking.maps

import android.location.Location
import java.io.Serializable

data class BinLocation(var id: String? = null,
                       var latitude: Double = DefaultBinLocation.DEFAULT_BIN_LATITUDE,
                       var longitude: Double = DefaultBinLocation.DEFAULT_BIN_LONGITUDE,
                       var description: String = ""): Serializable {

    fun toLocation(): Location {

        return Location("BinLocation").apply {
            this.latitude = this@BinLocation.latitude
            this.longitude = this@BinLocation.longitude
        }

    }
}