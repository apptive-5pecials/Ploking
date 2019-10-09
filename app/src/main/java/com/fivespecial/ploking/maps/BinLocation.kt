package com.fivespecial.ploking.maps

import java.io.Serializable

data class BinLocation(var id: String? = null,
                       var latitude: Double? = null,
                       var longitude: Double? = null,
                       var description: String? = null): Serializable