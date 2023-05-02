package com.robles.itcm.ptampersonas.ui.fragments

import android.app.Person
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.robles.itcm.ptampersonas.MyAdapter
import com.robles.itcm.ptampersonas.PersonInfoActivity
import com.robles.itcm.ptampersonas.Persons
import com.robles.itcm.ptampersonas.R
import java.lang.Character.toLowerCase
import java.util.Locale
import java.util.PropertyPermission

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class PersonasFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var txtSearch: SearchView
    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var personsArrayList: ArrayList<Persons>

    private lateinit var btnSubirImange: ImageButton
    private lateinit var imageUri: Uri

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()

        btnSubirImange = view.findViewById(R.id.btn_subir_buscar_imagen)
        txtSearch = view.findViewById(R.id.txt_search_person)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        adapter = MyAdapter(personsArrayList)
        recyclerView.adapter = adapter

        btnSubirImange.setOnClickListener { cargarImagen() }

        txtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return false
            }
        })

    }

    private fun filterList(text: String) {
        val filteredList = ArrayList<Persons>()
        for (p in personsArrayList) {
            if (p.title.lowercase().contains(text.lowercase()) ||
                p.curp.lowercase().contains(text.lowercase())) {
                filteredList.add(p)
            }
        }
        if (filteredList.isNotEmpty()) {
            adapter.setFilteredList(filteredList)
            adapter.notifyDataSetChanged()
        }
    }


    private fun cargarImagen() {
        selectImageLauncher.launch("image/*")
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            //imgPerson.setImageURI(uri)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PersonasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun dataInitialize(){
        personsArrayList = arrayListOf<Persons>()
        val collectionRef = db.collection("persons")
        collectionRef.get().addOnSuccessListener {
            for(document in it.documents){
                Log.d(document.id, document.data?.get("name").toString())
                var imagen: Bitmap? = null
                val curp = document.data?.get("curp").toString()
                val name = document.data?.get("name").toString()
                val lugar = document.data?.get("lugar_desaparicion").toString()
                val enabled = document.data?.get("enabled") as Boolean
                FirebaseStorage.getInstance().reference.child("$curp.jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener {bytes ->
                    // Convertir bytes a Bitmap y mostrar en el ImageView
                    imagen = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }.addOnSuccessListener {
                    personsArrayList.add(Persons(name, lugar, imagen!!, curp, enabled))
                    adapter = MyAdapter(personsArrayList)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) { showPersonInfo(position) }
                    })
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error + ${it.toString()}", Toast.LENGTH_SHORT).show()
        }

    }
    private fun showPersonInfo(position: Int){
        val x = personsArrayList.get(position)
        val intent = Intent(context, PersonInfoActivity::class.java)
        intent.putExtra("curp", x.curp)
        startActivity(intent)
        Log.d("persona", x.toString())
    }
}