package com.robles.itcm.ptampersonas

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.robles.itcm.ptampersonas.databinding.ActivityDetalleAvistamientoBinding

class DetalleAvistamientoActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var b: ActivityDetalleAvistamientoBinding
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    lateinit var id_avistamiento: String
    lateinit var curp: String
    var circle: Circle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetalleAvistamientoBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.mapView.onCreate(savedInstanceState)
        b.mapView.getMapAsync(this)

        id_avistamiento = intent.getStringExtra("id").toString()
        curp = intent.getStringExtra("curp").toString()



    }

    override fun onMapReady(map: GoogleMap) {
        db.collection("persons").document(curp).collection("avistamientos").document(id_avistamiento).get().addOnCompleteListener {
            if(it.isSuccessful){
                val image_id = it.result.data?.get("image").toString()
                Toast.makeText(this, "$curp/$image_id.jpg", Toast.LENGTH_SHORT).show()
                storage.reference.child("$curp/$image_id.jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                    b.imgNuevoAvistamiento.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                }
                b.txtDetalleAvistamientoTitulo.setText(it.result.data?.get("titulo").toString())
                b.txtDetalleAvistamientoDescripcion.setText(it.result.data?.get("descripcion").toString())
                b.txtDetalleAvistamientoFecha.setText(it.result.data?.get("fecha").toString())
                val radius = it.result.data?.get("radio") as? Double ?: 1000.0
                val latlon = it.result.data?.get("latlon") as GeoPoint

                circle?.remove()
                val location = LatLng(latlon.latitude, latlon.longitude)
                val circleOptions = CircleOptions()
                    .center(location)
                    .radius(radius) // radio en metros
                    .strokeColor(Color.RED) // color del borde
                    .fillColor(0x220000FF) // color de relleno
                circle = map.addCircle(circleOptions)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.5F))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        b.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        b.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        b.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        b.mapView.onLowMemory()
    }

}

