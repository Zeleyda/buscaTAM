package com.robles.itcm.ptampersonas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.itcm.ptampersonas.databinding.ActivityEstadoPersonBinding

class EstadoPersonActivity : AppCompatActivity() {

    private lateinit var b: ActivityEstadoPersonBinding
    private lateinit var curp: String
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEstadoPersonBinding.inflate(layoutInflater)
        setContentView(b.root)
        curp = intent.getStringExtra("curp").toString()
        Toast.makeText(this, curp, Toast.LENGTH_SHORT).show()

        val situaciones = arrayOf("Desaparecida", "Encontrada")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, situaciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.spinnerSituacion.adapter = adapter


        b.btnActualizarDatos.setOnClickListener {
            val estado = b.switchAprobada.isChecked
            val situacion = b.spinnerSituacion.selectedItem.toString()
            Toast.makeText(this, "$estado $situacion", Toast.LENGTH_SHORT).show()
            val res = mapOf(
                "enabled" to estado,
                "situacion" to situacion
            )
            db.collection("persons").document(curp).update(res).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
                }
            }
        }

        b.btnPersonInfo.setOnClickListener {
            val i = Intent(this, PersonInfoActivity::class.java)
            i.putExtra("curp", curp)
            startActivity(i)
        }
    }
}