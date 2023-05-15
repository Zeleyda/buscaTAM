package com.robles.itcm.ptampersonas.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.robles.itcm.ptampersonas.EstadoPersonActivity
import com.robles.itcm.ptampersonas.MyAdapter
import com.robles.itcm.ptampersonas.Persons
import com.robles.itcm.ptampersonas.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminFragment : Fragment() {
    private lateinit var currentBackground: Drawable
    private lateinit var listPersons: RecyclerView
    private val personsArrayList = arrayListOf<Persons>()
    private val adapter = MyAdapter(personsArrayList)
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val toolbar = (activity as AppCompatActivity).findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setBackgroundResource(R.drawable.banner_admin)
        listPersons = view.findViewById(R.id.persons_list_admin)
        listPersons.adapter = adapter
        listPersons.layoutManager = LinearLayoutManager(context)
        listPersons.setHasFixedSize(true)
        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val i = Intent(context, EstadoPersonActivity::class.java)
                i.putExtra("curp", adapter.getList().get(position).curp)
                startActivity(i)
            }
        })
        dataInitialize()
    }

    private fun dataInitialize(){
        val collectionRef = db.collection("persons")
        collectionRef.orderBy("enabled").get().addOnSuccessListener {
            for(document in it.documents){
                Log.d(document.id, document.data?.get("name").toString())
                var imagen: Bitmap? = null
                val curp = document.data?.get("curp").toString()
                val name = document.data?.get("name").toString()
                val lugar = document.data?.get("lugar_desaparicion").toString()
                val enabled = document.data?.get("enabled") as Boolean
                FirebaseStorage.getInstance().reference.child("$curp.jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                    imagen = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imagen = Bitmap.createScaledBitmap(imagen!!, imagen!!.width/5, imagen!!.height/5, true)
                }.addOnSuccessListener {
                    personsArrayList.add(Persons(name, lugar, imagen!!, curp, enabled))
                    adapter.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error + ${it.toString()}", Toast.LENGTH_SHORT).show()
        }

    }

}