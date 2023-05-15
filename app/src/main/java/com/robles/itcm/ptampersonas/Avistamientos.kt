package com.robles.itcm.ptampersonas

import com.google.firebase.firestore.GeoPoint

data class Avistamientos(
    var id: String,
    var titulo: String,
    var descripcion: String,
    var fecha: String,
    var latlon: GeoPoint?,
    var zoom: Double?
)
