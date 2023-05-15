package com.robles.itcm.ptampersonas

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.robles.itcm.ptampersonas.databinding.ActivityPersonInfoBinding

class PersonInfoActivity : AppCompatActivity() {

    lateinit var curp: String
    var tam = false
    private lateinit var b: ActivityPersonInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPersonInfoBinding.inflate(layoutInflater)
        setContentView(b.root)

        curp = intent.getStringExtra("curp").toString()
        FirebaseFirestore.getInstance().collection("persons").document(curp).get().addOnCompleteListener {
            if(it.isSuccessful){
                FirebaseStorage.getInstance().reference.child("$curp.jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                    b.imgInfoImgPerson.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                }
                b.txtInfoName.setText(it.result.data?.get("name").toString())
                b.txtInfoCurp.setText(it.result.data?.get("curp").toString())
                b.txtInfoEstadoCivil.setText(it.result.data?.get("edo_civil").toString())
                b.txtInfoNacionalidad.setText(it.result.data?.get("nacionalidad").toString())
                b.txtInfoLugarNacimiento.setText(it.result.data?.get("nacimiento").toString())
                b.txtInfoFechaNacimiento.setText(it.result.data?.get("fecha_nacimiento").toString())
                b.txtInfoEdad.setText(it.result.data?.get("edad").toString())
                b.txtInfoEstatura.setText(it.result.data?.get("estatura").toString())
                b.txtInfoComplexion.setText(it.result.data?.get("complexion").toString())
                b.txtInfoSigns.setText(it.result.data?.get("senales").toString())
                b.txtInfoOutfit.setText(it.result.data?.get("vestimenta").toString())
                b.txtInfoDatetime.setText(it.result.data?.get("fecha_desaparicion").toString())
                b.txtInfoLugarDesaparicion.setText(it.result.data?.get("lugar_desaparicion").toString())
                b.txtInfoCircumstance.setText(it.result.data?.get("circunstancia").toString())
                b.txtInfoDescription.setText(it.result.data?.get("descripcion").toString())
                b.txtInfoEdicto.setText(it.result.data?.get("edicto").toString())
                b.txtInfoDependencia.setText(it.result.data?.get("dependencia").toString())
            }
        }

        b.imgInfoImgPerson.setOnClickListener {
            val imageView = b.imgInfoImgPerson
            val layoutParams = imageView.layoutParams
            if(!tam) {
                layoutParams.width = dpToPx(320)
                layoutParams.height = dpToPx(320)
                imageView.layoutParams = layoutParams
                tam = true
            }
            else if(tam){
                layoutParams.width = dpToPx(150)
                layoutParams.height = dpToPx(150)
                imageView.layoutParams = layoutParams
                tam = false
            }
        }

        b.btnAvistamientos.setOnClickListener {
            val i = Intent(this, ListaAvistamientosActivity::class.java)
            i.putExtra("curp", curp)
            startActivity(i)
        }

        b.btnNuevoAvistamiento.setOnClickListener {
            val i = Intent(this, NuevoAvistamientoActivity::class.java)
            i.putExtra("curp", curp)
            startActivity(i)
        }

    }
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }
}