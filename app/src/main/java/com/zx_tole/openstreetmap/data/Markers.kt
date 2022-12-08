package com.zx_tole.openstreetmap.data

data class Markers(
    val name: String,
    val comment: String,
    val items: List<City>
    )