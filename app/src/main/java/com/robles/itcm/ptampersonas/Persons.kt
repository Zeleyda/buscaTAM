package com.robles.itcm.ptampersonas

import android.graphics.Bitmap

data class Persons(
    var title: String,
    var description: String,
    var image: Bitmap,
    var curp: String,
    var enabled: Boolean
    )
