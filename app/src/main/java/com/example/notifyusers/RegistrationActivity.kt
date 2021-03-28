package com.example.notifyusers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegistrationActivity : AppCompatActivity() {


    lateinit var fAuth : FirebaseAuth
    lateinit var fStore : FirebaseFirestore
    lateinit var df : DocumentReference
    lateinit var user : FirebaseUser

    lateinit var etFullName : EditText
    lateinit var etEmail : EditText
    lateinit var etPassword : EditText
    lateinit var btnRegister : Button
    lateinit var btnGoBack : Button
    lateinit var btnInformation : ImageView
    var valid = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoBack = findViewById(R.id.btnGoBack)
        btnInformation = findViewById(R.id.btnInformation)



        btnRegister.setOnClickListener(){
            checkfield(etFullName)
            checkfield(etEmail)
            checkfield(etPassword)

            if (valid){

                Toast.makeText(this, "Register pressed", Toast.LENGTH_SHORT).show()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success")
                            val user = fAuth.currentUser
                            Toast.makeText(this@RegistrationActivity, "Account created", Toast.LENGTH_SHORT).show()
                            val df = fStore.collection("Users").document(user!!.uid)
                            val userInfo: MutableMap<String, Any> = HashMap()
                            userInfo["Fullname"] = etFullName.text.toString()
                            userInfo["Email"] = etEmail.text.toString()
                            userInfo["isUser"] = "1"
                            df.set(userInfo)
                            startActivity(Intent(applicationContext, UserActivity::class.java))
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }

                        // ...
                    }


            }

        }

        btnGoBack.setOnClickListener(){
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnInformation.setOnClickListener{
            Toast.makeText(this, "*minimum 6 character is needed for password", Toast.LENGTH_LONG).show()
        }

    }

    fun checkfield(textfield: EditText) : Boolean {
        if (textfield.text.toString().isEmpty()){
            textfield.setError("Error")
            valid = false
        }else{
            valid = true
        }

        return valid
    }
}