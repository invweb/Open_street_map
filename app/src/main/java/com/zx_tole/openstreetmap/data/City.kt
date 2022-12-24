package com.zx_tole.openstreetmap.data

data class City(
    val lat: Double,
    val lon: Double,
    val name: String,
    val info: String,
    val attractions: List<Attraction>
    )