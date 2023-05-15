package com.robles.itcm.ptampersonas

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.robles.itcm.ptampersonas.databinding.ActivityNuevoAvistamientoBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class NuevoAvistamientoActivity : AppCompatActivity() {

    var imageUri = null as Uri?
    lateinit var b: ActivityNuevoAvistamientoBinding
    lateinit var curp: String
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            b.imgNuevoAvistamiento.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNuevoAvistamientoBinding.inflate(layoutInflater)
        setContentView(b.root)

        curp = intent.getStringExtra("curp").toString()
        Toast.makeText(this, "Curp: $curp", Toast.LENGTH_SHORT).show()
        SessionData.setData("lat", -1.0)
        SessionData.setData("lon", -1.0)
        SessionData.setData("id_avistamiento", "-1")

        b.btnAddImageNuevoAvistamiento.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        b.btnGuardarAvistamiento.setOnClickListener{
            b.btnGuardarAvistamiento.isEnabled = false
            if(SessionData.getData("lat") != -1.0 && SessionData.getData("lon") != -1.0 && !b.txtNuevoAvistamientoTitulo.text.isNullOrEmpty()
                && !b.txtNuevoAvistamientoDescripcion.text.isNullOrEmpty() && !b.txtFechaNuevoAvistamientoHora.text.isNullOrEmpty() && imageUri != null){

                showDialow()

                val bitmap = (b.imgNuevoAvistamiento.drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val data = stream.toByteArray()


                val uuid = UUID.randomUUID().toString()
                storage.reference.child("$curp/${uuid}.jpg").putBytes(data).addOnSuccessListener {
                    db.collection("persons").document(curp).collection("avistamientos").document().set(
                        mapOf(
                            "titulo" to b.txtNuevoAvistamientoTitulo.text.toString(),
                            "descripcion" to b.txtNuevoAvistamientoDescripcion.text.toString(),
                            "fecha" to b.txtFechaNuevoAvistamientoHora.text.toString(),
                            "latlon" to SessionData.getData("latlon"),
                            "image" to uuid
                        )
                    ).addOnCompleteListener {
                        dialog.dismiss()
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Se agrego con exito", Toast.LENGTH_SHORT).show()
                            val builder = AlertDialog.Builder(this)

                            builder.setTitle("Su reporte se ha guardado con exito")
                            builder.setMessage("¡Muchas gracias!")

                            builder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }

                            builder.show()

                            b.btnGuardarAvistamiento.isEnabled = false
                        }
                        else
                            b.btnGuardarAvistamiento.isEnabled = true

                    }
                }.addOnFailureListener{
                    dialog.dismiss()
                    b.btnGuardarAvistamiento.isEnabled = true
                }
            }
            else
            {
                Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show()
                b.btnGuardarAvistamiento.isEnabled = true
            }
        }
    }

    private fun showDialow() {
        builder = AlertDialog.Builder(this)
        builder.setView(LayoutInflater.from(this).inflate(R.layout.dialog_save_avistamiento, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }
}