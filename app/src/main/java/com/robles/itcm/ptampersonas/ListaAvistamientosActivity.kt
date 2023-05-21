package com.robles.itcm.ptampersonas

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.robles.itcm.ptampersonas.databinding.ActivityListaAvistamientosBinding

class ListaAvistamientosActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var b: ActivityListaAvistamientosBinding
    val arrayList = arrayListOf<Avistamientos>()
    val adapter = AdaptadorAvistamientos(arrayList)
    var changeLayout = true;

    lateinit var curp: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityListaAvistamientosBinding.inflate(layoutInflater)
        setContentView(b.root)
        curp = intent.getStringExtra("curp").toString()
        Toast.makeText(this, "Curp: $curp", Toast.LENGTH_SHORT).show()

        b.recyclerViewAvistamientos.adapter = adapter
        b.recyclerViewAvistamientos.layoutManager = GridLayoutManager(this, 1)
        b.recyclerViewAvistamientos.setHasFixedSize(true)

        b.btnCambiarVista.setOnClickListener {
            if(changeLayout)
                b.recyclerViewAvistamientos.layoutManager = GridLayoutManager(this, 2);
            else
                b.recyclerViewAvistamientos.layoutManager = GridLayoutManager(this, 1);
            changeLayout = !changeLayout;
        }

        adapter.setOnItemClickListener(object : AdaptadorAvistamientos.onItemClickListener{
            override fun onItemClick(position: Int) {
                mostrarAvistamiento(position)
            }
        })


        db.collection("persons").document(curp).collection("avistamientos").get().addOnCompleteListener {
            for(ds in it.result){
                val id = ds.id
                val titulo = ds.data["titulo"].toString()
                val desc = ds.data["descripcion"].toString()
                val fecha = ds.data["fecha"].toString()
                val latlon = ds.data["latlon"] as GeoPoint?
                val zoom = ds.data["zoom"] as Double?
                arrayList.add(Avistamientos(id, titulo, desc, fecha, latlon, zoom))
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun mostrarAvistamiento(position: Int){
        val i = Intent(this, DetalleAvistamientoActivity::class.java)
        i.putExtra("id", adapter.getList()[position].id)
        i.putExtra("curp", curp)
        startActivity(i)
    }
}