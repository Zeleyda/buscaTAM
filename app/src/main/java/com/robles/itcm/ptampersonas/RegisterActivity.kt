package com.robles.itcm.ptampersonas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.itcm.ptampersonas.databinding.ActivityRegisterBinding

lateinit var b: ActivityRegisterBinding
class RegisterAcivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)
        setup()
    }

    private fun setup(){
        b.btnRegister.setOnClickListener {
            val email = b.txtRegisterEmail.text.toString()
            val password = b.txtRegisterPassword.text.toString()
            val repeatPassword = b.txtRegisterRepeatPassword.text.toString()
            val name = b.txtRegisterName.text.toString()
            val phone = b.txtRegisterPhone.text.toString()

            var ok = true
            if (!isEmailValid(email)) {
                setErrorEmail()
                ok = false
            }
            if (password.length < 8) {
                b.layoutRegisterPassword.isErrorEnabled = true
                b.layoutRegisterPassword.isHelperTextEnabled = true
                b.layoutRegisterPassword.helperText = "Debe tener al menos 8 caracteres"
                ok = false
            }
            if (repeatPassword != password) {
                b.layoutRegisterRepeatPassword.isErrorEnabled = true
                b.layoutRegisterRepeatPassword.isHelperTextEnabled = true
                b.layoutRegisterRepeatPassword.helperText = "Contraseñas no coinciden"
                ok = false
            }
            if (name.length < 3) {
                b.layoutRegisterName.isErrorEnabled = true
                b.layoutRegisterName.isHelperTextEnabled = true
                b.layoutRegisterName.helperText = "Ingrese un nombre valido"
                ok = false
            }

            if(phone.length < 10){
                b.layoutRegisterPhone.isErrorEnabled = true
                b.layoutRegisterPhone.isHelperTextEnabled = true
                b.layoutRegisterPhone.helperText = "Ingrese un numero valido"
                ok = false
            }

            if (ok) {
                val firebase = FirebaseAuth.getInstance()
                firebase.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    if(it.isSuccessful){
                        val result = it.result.signInMethods
                        if(result.isNullOrEmpty()){
                            firebase.createUserWithEmailAndPassword(email, password).addOnCompleteListener {it2 ->
                                if(it2.isSuccessful){
                                    Toast.makeText(this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show()
                                    b.btnRegister.isEnabled = false
                                    db.collection("users").document(email).set(
                                        hashMapOf(
                                            "name" to name,
                                            "phone" to phone,
                                            "admin" to false
                                        )
                                    )
                                }
                                else{
                                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else{
                            Toast.makeText(this, "El correo ya esta registrado con otra cuenta", Toast.LENGTH_SHORT).show()
                            setErrorEmail("El correo ya se encuentra en uso por otro usuario")
                        }
                    }
                }
            }
        }

        b.txtRegisterEmail.addTextChangedListener {
            b.layoutRegisterEmail.isErrorEnabled = false
            b.layoutRegisterEmail.isHelperTextEnabled = false
        }

        b.txtRegisterPassword.addTextChangedListener {
            b.layoutRegisterPassword.isErrorEnabled = false
            b.layoutRegisterPassword.isHelperTextEnabled = false
        }

        b.txtRegisterRepeatPassword.addTextChangedListener{
            b.layoutRegisterRepeatPassword.isErrorEnabled = false
            b.layoutRegisterRepeatPassword.isHelperTextEnabled = false
        }

        b.txtRegisterName.addTextChangedListener {
            b.layoutRegisterName.isErrorEnabled = false
            b.layoutRegisterName.isHelperTextEnabled = false
        }

        b.txtRegisterPhone.addTextChangedListener {
            b.layoutRegisterName.isErrorEnabled = false
            b.layoutRegisterName.isHelperTextEnabled = false
        }

    }

    private fun setErrorEmail(message: String = "Correo no valido"){
        b.layoutRegisterEmail.isErrorEnabled = true
        b.layoutRegisterEmail.isHelperTextEnabled = true
        b.layoutRegisterEmail.helperText = message
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}