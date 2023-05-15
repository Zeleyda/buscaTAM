package com.robles.itcm.ptampersonas

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.itcm.ptampersonas.databinding.ActivityListaAvistamientosBinding

class ListaAvistamientosActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var b: ActivityListaAvistamientosBinding
    val arrayList = arrayListOf<Persons>()
    val adapter = MyAdapter(arrayList)

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

        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{
            override fun onItemClick(position: Int) { }
        })

        db.collection("persons").document(curp).collection("avistamientos").get().addOnCompleteListener {
            for(ds in it.result){
                val desc = ds.data["descripcion"].toString()
                val latlon = "${ds.data["lat"]}, ${ds.data["lon"]}"
                arrayList.add(Persons(desc, latlon, Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888), desc, true))
                adapter.notifyDataSetChanged()
            }
        }
    }
}