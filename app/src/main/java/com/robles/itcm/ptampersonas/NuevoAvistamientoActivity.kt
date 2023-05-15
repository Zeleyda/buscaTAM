package com.robles.itcm.ptampersonas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.itcm.ptampersonas.databinding.ActivityNuevoAvistamientoBinding

class NuevoAvistamientoActivity : AppCompatActivity() {
    lateinit var b: ActivityNuevoAvistamientoBinding
    lateinit var curp: String
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNuevoAvistamientoBinding.inflate(layoutInflater)
        setContentView(b.root)

        curp = intent.getStringExtra("curp").toString()
        Toast.makeText(this, "Curp: $curp", Toast.LENGTH_SHORT).show()
        SessionData.setData("lat", -1.0)
        SessionData.setData("lon", -1.0)


        b.btnGuardarAvistamiento.setOnClickListener{
            if(SessionData.getData("lat") != -1.0 && SessionData.getData("lon") != -1.0)
            db.collection("persons").document(curp).collection("avistamientos").document().set(mapOf(
                "descripcion" to b.txtNuevoAvistamientoDescripcion.text.toString(),
                "fecha" to b.txtFechaNuevoAvistamientoHora.text.toString(),
                "lat" to SessionData.getData("lat"),
                "lon" to SessionData.getData("lon"),
            )).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Se agrego con exito", Toast.LENGTH_SHORT).show()
                    b.btnGuardarAvistamiento.isEnabled = false
                }
            }

        }
    }
}