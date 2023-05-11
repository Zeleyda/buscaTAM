package com.robles.itcm.ptampersonas.ui.fragments

import android.app.AlertDialog
import android.app.Person
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robles.itcm.ptampersonas.MyAdapter
import com.robles.itcm.ptampersonas.PersonInfoActivity
import com.robles.itcm.ptampersonas.Persons
import com.robles.itcm.ptampersonas.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Character.toLowerCase
import java.util.Locale
import java.util.PropertyPermission
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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

    private lateinit var btnSubirImage: ImageButton
    private lateinit var btnResetList: ImageButton
    private lateinit var imageUri: Uri
    private lateinit var imgSearch: ImageView

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


        imgSearch = view.findViewById(R.id.img_search)
        btnResetList = view.findViewById(R.id.btn_reset_list)
        btnSubirImage = view.findViewById(R.id.btn_subir_buscar_imagen)
        txtSearch = view.findViewById(R.id.txt_search_person)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        adapter = MyAdapter(personsArrayList)
        recyclerView.adapter = adapter

        btnSubirImage.setOnClickListener {
            cargarImagen()
        }

        btnResetList.setOnClickListener{
            dataInitialize()
        }

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
            imgSearch.setImageURI(uri)
            imgSearch.visibility=View.GONE
            val bitmap = (imgSearch.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
            val data = stream.toByteArray()
            searchFace(data)

        }
    }

    private fun searchFace(byteArray: ByteArray): Boolean{
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        builder.setView(inflater.inflate(R.layout.dialog_loading_search, null))
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        var result = false
        val client = OkHttpClient.Builder()
            .readTimeout(15 , TimeUnit.SECONDS)
            .build()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image", "image.jpg",
                byteArray.toRequestBody("image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
            ).build()

        val request = Request.Builder()
            .url("https://elcesarmaat.pythonanywhere.com/face_recognition")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result = false
                activity?.runOnUiThread {
                    dialog.dismiss()
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    val resultados = response.body?.string()?.split(",") as List<String>
                    val filteredList = personsArrayList.filter { it.curp in resultados }
                    activity?.runOnUiThread {
                        adapter.setFilteredList(filteredList as ArrayList<Persons>)
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                        if(filteredList.isEmpty())
                            Toast.makeText(context, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(context, "Se encontraron los siguientes resultados", Toast.LENGTH_LONG).show()
                    }
                }
                else
                    dialog.dismiss()
                result = response.isSuccessful
            }
        })
        return result

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
        val x = adapter.getList()[position]
        val intent = Intent(context, PersonInfoActivity::class.java)
        intent.putExtra("curp", x.curp)
        startActivity(intent)
        Log.d("persona", x.toString())
    }
}