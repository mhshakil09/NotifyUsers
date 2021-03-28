package com.example.notifyusers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


var TOPIC = ""



class AdminActivity : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth
    lateinit var fStore : FirebaseFirestore

    // cvTopicList
    lateinit var tvTopics :TextView

    //cvMain
    lateinit var etTopic : EditText
    lateinit var etTitle : EditText
    lateinit var etMessage : EditText
    lateinit var btnSend : Button
    lateinit var btnLogout : Button

    //cvNewTopicName
    lateinit var etNewTopicName : EditText
    lateinit var btnInformation : ImageButton
    lateinit var btnNewTopicName : Button

    var valid = true


    val initTopic = "/topics/"

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()



        // cvTopicList
        tvTopics = findViewById(R.id.tvTopics)

        //cvMain
        etTopic = findViewById(R.id.etTopic)
        etTitle = findViewById(R.id.etTitle)
        etMessage = findViewById(R.id.etMessage)

        btnSend = findViewById(R.id.btnSend)
        btnLogout = findViewById(R.id.btnLogout)

        //cvNewTopicName
        etNewTopicName = findViewById(R.id.etNewTopicName)
        btnInformation = findViewById(R.id.btnInformation)
        btnNewTopicName = findViewById(R.id.btnNewTopicName)


        TOPIC = initTopic+"users"

        // receiving the notification
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        FirebaseMessaging.getInstance().subscribeToTopic("allusers")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("asd")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("asdzxc")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("user")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("users")
//        FirebaseMessaging.getInstance().subscribeToTopic("users")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("hola")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("holar")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("test")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("test2")



        //Display the Topic list
        var tempTopic = TOPIC
        tvTopics.text = tempTopic

//        val df = fStore.collection("Users").document("ADMIN")
//        val userInfo: MutableMap<String, Any> = HashMap()
//        userInfo["TOPICS"] = TOPIC
//        df.set(userInfo)


        //------------------------------------------------------------------------------------------
        val df = fStore.collection("Users").document("ADMIN")
        df.addSnapshotListener(this) { value, error ->
            tvTopics.setText(value!!.getString("TOPICS"))

//            var testing = tvTopics.text.toString().replace(initTopic, "")
//            tvTopics.text = testing

            tempTopic = tvTopics.text.toString()
        }
//        if (tempTopic.isEmpty()){
//            val userInfo: MutableMap<String, Any> = HashMap()
//            userInfo["TOPICS"] = initTopic+"users"
//            df.set(userInfo)
//
//            df.addSnapshotListener(this) { value, error ->
//                tvTopics.setText(value!!.getString("TOPICS"))
//                tempTopic = tvTopics.text.toString()
//            }
//
//        }

        //------------------------------------------------------------------------------------------


        //Sending notification
        btnSend.setOnClickListener{
            valid = true
            checkValid(etTopic)
            checkValid(etTitle)
            checkValid(etMessage)

            var tempCheck = initTopic + etTopic.text.toString().toLowerCase()


            // checking whether the topic exists or not----------------------------------------------
//            https://stackoverflow.com/questions/52628091/how-to-find-the-whole-word-in-kotlin-using-regex

            val matcher = "(?i)(?<!\\p{L})$tempCheck(?!\\p{L})".toRegex()
            if (matcher.findAll(tvTopics.text.toString()).count() > 0){
                Toast.makeText(this, "Topic found", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Topic not found", Toast.LENGTH_SHORT).show()
            }
            //------------------------------------------------------------------------------------------


            if (etTopic.text.isNotEmpty() && etTitle.text.isNotEmpty() && etMessage.text.isNotEmpty() ){
                TOPIC = initTopic + etTopic.text.toString().toLowerCase()
                val title = etTitle.text.toString()
                val message = etMessage.text.toString()

                if (title.isNotEmpty() && message.isNotEmpty()){
                    PushNotification(
                        NotificationData(title, message),
                        TOPIC
                    ).also {
                        sendNotification(it)
                    }
                }
            }else{
                Toast.makeText(this, "hola", Toast.LENGTH_SHORT).show()
//                if (checkValid(etTopic) == false){etTopic.error = "must contain a Topic"}
//                if (checkValid(etTitle) == false){etTitle.error = "must contain a Title"}
//                if (checkValid(etMessage) == false){etMessage.error = "must contain a Message"}
//

            }
        }




        // information to create new topic
        btnInformation.setOnClickListener{
            Toast.makeText(this, "*no space allowed\n*20 character Limit\n*all converts to lowercase\n*only \"a-z\"  \"0-9\"  \"-\"  \"_\"  are allowed", Toast.LENGTH_LONG).show()
        }


        // creating new topic
        btnNewTopicName.setOnClickListener {

            if (etNewTopicName.text.isNotEmpty()){
                // checking whether the topic exists or not----------------------------------------------
//            https://stackoverflow.com/questions/52628091/how-to-find-the-whole-word-in-kotlin-using-regex

                var tempCheck = initTopic + etNewTopicName.text.toString().toLowerCase()

                val matcher = "(?i)(?<!\\p{L})$tempCheck(?!\\p{L})".toRegex()
                if (matcher.findAll(tvTopics.text.toString()).count() > 0){
                    Toast.makeText(this, "Topic already exists", Toast.LENGTH_SHORT).show()
                }else{

                    var tempNewTopicName = "/topics/" + etNewTopicName.text.toString().toLowerCase()
                    tempTopic = tempTopic + " \n" + tempNewTopicName
                    tvTopics.text = tempTopic

                    FirebaseMessaging.getInstance().subscribeToTopic(tempNewTopicName)

                    // to store the topic list in firestore
                    val df = fStore.collection("Users").document("ADMIN")
                    val userInfo: MutableMap<String, Any> = HashMap()
                    userInfo["TOPICS"] = tempTopic
                    df.set(userInfo)


                    Toast.makeText(this, "New topic Created", Toast.LENGTH_SHORT).show()
                }

                etNewTopicName.setText("")
                //------------------------------------------------------------------------------------------


            }else{
                etNewTopicName.error = "give a name"
            }

        }


        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

    }


    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            }else{
                Log.e(TAG, response.errorBody().toString())
            }

        } catch (e: Exception){
            Log.e(TAG, e.toString())
        }
    }

    fun checkValid(text: EditText): Boolean {
        if (text.text.toString().isEmpty()) {
            text.error = "error"
            valid = false
        }
        return valid
    }


}