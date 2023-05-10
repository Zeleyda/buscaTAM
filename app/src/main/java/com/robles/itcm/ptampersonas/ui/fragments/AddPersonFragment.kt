package com.robles.itcm.ptampersonas.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.robles.itcm.ptampersonas.R
import com.robles.itcm.ptampersonas.curp
import com.robles.itcm.ptampersonas.databinding.FragmentAddPersonBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class AddPersonFragment : Fragment() {
    private lateinit var txtName: TextInputEditText
    private lateinit var txtCurp: TextInputEditText
    private lateinit var txtEstadoCivil: TextInputEditText
    private lateinit var txtNacionalidad: TextInputEditText
    private lateinit var txtLugarNacimiento: TextInputEditText
    private lateinit var txtFechaNacimiento: TextInputEditText
    private lateinit var txtEdad: TextInputEditText
    private lateinit var txtEstatura: TextInputEditText
    private lateinit var txtComplexion: TextInputEditText
    private lateinit var txtSeñas: TextInputEditText
    private lateinit var txtVestimenta: TextInputEditText
    private lateinit var txtFecha: TextInputEditText
    private lateinit var txtLugarDesaparicion: TextInputEditText
    private lateinit var txtDateTime: TextInputEditText
    private lateinit var txtCircunstancia: TextInputEditText
    private lateinit var txtDescripcion: TextInputEditText
    private lateinit var imgPerson: ImageView
    private lateinit var btnCargarImagen: MaterialButton
    private lateinit var btnGuardarPersona: MaterialButton

    private lateinit var imageUri: Uri

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            imgPerson.setImageURI(uri)
        }
    }
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_add_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtName = view.findViewById(R.id.txt_add_name)
        txtCurp = view.findViewById(R.id.txt_add_curp)
        txtEstadoCivil = view.findViewById(R.id.txt_add_estadoCivil)
        txtNacionalidad = view.findViewById(R.id.txt_add_nacionalidad)
        txtLugarNacimiento = view.findViewById(R.id.txt_add_lugarNacimiento)
        txtFechaNacimiento = view.findViewById(R.id.txt_add_fecha_nacimiento)
        txtEdad = view.findViewById(R.id.txt_add_Edad)
        txtEstatura = view.findViewById(R.id.txt_add_estatura)
        txtComplexion = view.findViewById(R.id.txt_add_complexion)
        txtSeñas = view.findViewById(R.id.txt_add_signs)
        txtVestimenta = view.findViewById(R.id.txt_add_outfit)
        txtDateTime = view.findViewById(R.id.txt_add_datetime)
        txtLugarDesaparicion = view.findViewById(R.id.txt_add_lugarDesaparicion)

        txtCircunstancia = view.findViewById(R.id.txt_add_circumstance)
        txtDescripcion = view.findViewById(R.id.txt_add_description)

        btnCargarImagen = view.findViewById(R.id.btn_add_image)
        imgPerson = view.findViewById(R.id.img_add_img_person)

        btnGuardarPersona = view.findViewById(R.id.btn_add_save_person)

        setup()
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddPersonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setup(){
        txtDateTime.setOnFocusChangeListener { view, b ->
            if(view.isFocused)
                showDateTimePicker()
        }

        btnCargarImagen.setOnClickListener{
            selectImageLauncher.launch("image/*")
        }
        //Comentario culero
        //https://www.youtube.com/watch?v=PEzdv38iKdY
        btnGuardarPersona.setOnClickListener{
            val bitmap = (imgPerson.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
            val data = stream.toByteArray()



            val storage = FirebaseStorage.getInstance()
            // Crear referencia a la ubicación en Firebase Storage donde se guardará la imagen
            val storageRef = storage.reference.child("${txtCurp.text.toString()}.jpg")
            val uploadTask = storageRef.putBytes(data)
            uploadTask.addOnSuccessListener {
                // La imagen se cargó exitosamente
            }.addOnFailureListener {
                // Ocurrió un error al cargar la imagen
            }


            sendImageToServer(data, txtCurp.text.toString()     )


            FirebaseFirestore.getInstance().collection("persons").document(txtCurp.text.toString()).set(
                hashMapOf(
                    "name" to txtName.text.toString(),
                    "curp" to txtCurp.text.toString(),
                    "edo_civil" to txtEstadoCivil.text.toString(),
                    "nacionalidad" to txtNacionalidad.text.toString(),
                    "nacimiento" to txtLugarNacimiento.text.toString(),
                    "fecha_nacimiento" to txtFechaNacimiento.text.toString(),
                    "edad" to txtEdad.text.toString(),
                    "estatura" to txtEstatura.text.toString(),
                    "complexion" to txtComplexion.text.toString(),
                    "senales" to txtSeñas.text.toString(),
                    "vestimenta" to txtVestimenta.text.toString(),
                    "fecha_desaparicion" to txtDateTime.text.toString(),
                    "lugar_desaparicion" to txtLugarDesaparicion.text.toString(),
                    "circunstancia" to txtCircunstancia.text.toString(),
                    "descripcion" to txtDescripcion.text.toString(),
                    "image" to "${txtCurp.text.toString()}.jpg",
                    "enabled" to false
                )
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(context, "Persona agregada con exito", Toast.LENGTH_SHORT).show()
                    btnGuardarPersona.isEnabled = false
                }
            }
        }
    }

    fun sendImageToServer(byteArray: ByteArray, name: String): Boolean {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        builder.setView(inflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        var result = false
        val client = OkHttpClient.Builder()
            .readTimeout(10 , TimeUnit.SECONDS)
            .build()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image", "image.jpg",
                byteArray.toRequestBody("image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
            )
            .addFormDataPart("name", name)
            .build()

        val request = Request.Builder()
            .url("https://elcesarmaat.pythonanywhere.com/save_face_encoding")
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
                activity?.runOnUiThread {
                    dialog.dismiss()
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
                result = response.isSuccessful
            }
        })
        return result

    }

    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context!!, { _, year, monthOfYear, dayOfMonth ->
            val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
                // Aquí puedes actualizar el valor del EditText con la fecha y hora seleccionadas
                val selectedDateTime = Calendar.getInstance()
                selectedDateTime.set(year, monthOfYear, dayOfMonth, hourOfDay, minute)
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                txtDateTime.setText(sdf.format(selectedDateTime.time))
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false)
            timePickerDialog.show()
        }, year, month, day)
        datePickerDialog.show()
    }


}