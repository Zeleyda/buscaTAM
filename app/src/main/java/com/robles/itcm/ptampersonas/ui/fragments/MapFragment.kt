package com.robles.itcm.ptampersonas.ui.fragments

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.robles.itcm.ptampersonas.R
import com.robles.itcm.ptampersonas.SessionData

class MapFragment : Fragment() {
    private var mCircle: Circle? = null
    private lateinit var btnGuardar: Button
    var lat = -1.0
    var lon = -1.0
    private val callback = OnMapReadyCallback { googleMap ->
        val location = LatLng(22.2784909,-97.8337838)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        googleMap.setMinZoomPreference(6f)
        googleMap.setMaxZoomPreference(15f)

        googleMap.setOnMapClickListener { latLng ->
            mCircle?.remove()

            val circleOptions = CircleOptions()
                .center(latLng)
                .radius(1000.0) // radio en metros
                .strokeColor(Color.RED) // color del borde
                .fillColor(0x220000FF) // color de relleno
            mCircle = googleMap.addCircle(circleOptions)
            SessionData.setData("lat", latLng.latitude)
            SessionData.setData("lon", latLng.longitude)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}