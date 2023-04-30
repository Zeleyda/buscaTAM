package com.robles.itcm.ptampersonas.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.itcm.ptampersonas.R

class ProfileFragment : Fragment() {
    lateinit var name: String
    lateinit var phone: String
    lateinit var email: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val db = FirebaseFirestore.getInstance()

        val txt_name = view.findViewById<TextInputEditText>(R.id.txt_update_name)
        val txt_phone = view.findViewById<TextInputEditText>(R.id.txt_update_phone)

        email = context?.getSharedPreferences("session_data", Context.MODE_PRIVATE)
            ?.getString("email", "").toString()

        Toast.makeText(context, email, Toast.LENGTH_SHORT).show()
        db.collection("users").document(email).get().addOnSuccessListener {
            name = it.getString("name").toString()
            phone = it.getString("phone").toString()
            txt_name.setText(name)
            txt_phone.setText(phone)
        }

        val btn = view.findViewById<Button>(R.id.btn_update_info)
        btn.setOnClickListener {
            name = txt_name.text.toString()
            phone = txt_phone.text.toString()
            db.collection("users").document(email).set(
                hashMapOf(
                    "name" to name,
                    "phone" to phone
                )
            ).addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
                    btn.isEnabled = false
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}