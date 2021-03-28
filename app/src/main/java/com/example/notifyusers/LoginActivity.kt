package com.example.notifyusers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    lateinit var etEmail : EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : Button
    lateinit var btnRegister : Button

    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null


    var valid = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener(){

            checkValid(etEmail)
            checkValid(etPassword)
            Toast.makeText(this, "Login pressed", Toast.LENGTH_SHORT).show()

            if (valid){


                fAuth!!.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnSuccessListener { authResult ->
                    checkAccessLevel(authResult.user!!.uid)
                }.addOnFailureListener {
                    Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener(){
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }



    }

    private fun checkAccessLevel(uid: String) {
        val df = fStore!!.collection("Users").document(uid)
        df.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.getString("isUser") != null) {
                Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, UserActivity::class.java))
                finish()
            }
            if (documentSnapshot.getString("isAdmin") != null) {
                Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, AdminActivity::class.java))
                finish()
            }
        }
    }

    fun checkValid(text: EditText): Boolean {
        if (text.text.toString().isEmpty()) {
            text.error = "error"
            valid = false
        }
        return valid
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
//            var uid = FirebaseAuth.getInstance().uid
//            val df = fStore!!.collection("Users").document(uid!!)

//            df.get().addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.getString("isUser") != null) {
//                    Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT)
//                        .show()
//                    startActivity(Intent(applicationContext, UserActivity::class.java))
//                    finish()
//                }
//                if (documentSnapshot.getString("isAdmin") != null) {
//                    Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT)
//                        .show()
//                    startActivity(Intent(applicationContext, AdminActivity::class.java))
//                    finish()
//                }
//
//            }

            startActivity(Intent(applicationContext, UserActivity::class.java))
            finish()

        }
    }

}