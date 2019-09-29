package com.fivespecial.ploking.model;

import java.io.Serializable

data class Album(val id: Int = 0,
                 var path: String = "",
                 var file_name: String = "",
                 var longitude: Double = 0.0,
                 var latitude: Double = 0.0) : Serializable