package com.robles.itcm.ptampersonas

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.robles.itcm.ptampersonas.databinding.ActivityLoginBinding

private lateinit var binding: ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Completo")
        analytics.logEvent("InitScreen", bundle)

        val sesion_info = getSharedPreferences("session_data", Context.MODE_PRIVATE)
        val email = sesion_info.getString("email", "").toString()

        if(email.isNullOrEmpty())
            setup()
        else{
            //cargar ventana principal
            showHome(email)
        }
    }
    private fun setup() {
        title = "Autenticación"

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterAcivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.txtLoginEmail.text.toString()
            val password = binding.txtLoginPassword.text.toString()

            if(!isEmailValid(email)){
                binding.layoutLoginEmail.isErrorEnabled=true
                binding.layoutLoginEmail.isHelperTextEnabled=true
                binding.layoutLoginEmail.helperText = "Email no valido"
            }
            if(password.isEmpty()){
                binding.layoutpLoginPassword.isErrorEnabled = true
                binding.layoutpLoginPassword.isHelperTextEnabled = true
                binding.layoutpLoginPassword.helperText = "Introduzca este campo"
            }
            if (isEmailValid(email) && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = getSharedPreferences("session_data", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("email", email)
                        editor.apply()
                        showHome(email)
                    }
                    else{
                        when (it.exception) {
                            is FirebaseAuthInvalidUserException -> {
                                Toast.makeText(this, "No existe cuenta con este email", Toast.LENGTH_SHORT).show()

                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(this, "Datos de acceso incorrectos", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Error")
                                builder.setMessage("Se ha producido un error al iniciar la sesion")
                                builder.setPositiveButton("Aceptar", null)
                                val dialog = builder.create()
                                dialog.show()
                            }
                        }
                    }
                }
            }
        }

         binding.txtLoginEmail.addTextChangedListener {
             binding.layoutLoginEmail.isErrorEnabled = false
             binding.layoutLoginEmail.isHelperTextEnabled = false
         }

        binding.txtLoginPassword.addTextChangedListener {
            binding.layoutpLoginPassword.isErrorEnabled = false
            binding.layoutpLoginPassword.isHelperTextEnabled = false
        }

    }

    private fun showHome(email: String){
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    fun isEmailValid(email: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}