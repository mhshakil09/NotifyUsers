package com.example.notifyusers

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.HashMap

class UserActivity : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth
    lateinit var fStore : FirebaseFirestore

    // cvTopicList
    lateinit var tvTopics : TextView
    lateinit var tvChannelList : TextView
    var flagAdmin = 0

    // cvMyTopicList
    lateinit var tvMyTopicList : TextView

    // cvSubscribe
    lateinit var etTopicNameToSub :EditText
    lateinit var btnSubscribe : Button

    //cvUnsubscribe
    lateinit var etTopicNameToUnsub :EditText
    lateinit var btnUnsubscribe : Button


    val initTopic = "/topics/"

    lateinit var btnLogout : Button
    lateinit var btnGoToAdmin : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        tvTopics = findViewById(R.id.tvTopics)
        tvChannelList = findViewById(R.id.tvChannelList)


        tvMyTopicList = findViewById(R.id.tvMyTopicList)

        etTopicNameToSub = findViewById(R.id.etTopicNameToSub)
        btnSubscribe = findViewById(R.id.btnSubscribe)

        etTopicNameToUnsub = findViewById(R.id.etTopicNameToUnsub)
        btnUnsubscribe = findViewById(R.id.btnUnsubscribe)

        btnLogout = findViewById(R.id.btnLogout)
        btnGoToAdmin = findViewById(R.id.btnGoToAdmin)
        btnGoToAdmin.isVisible = false


        val user = fAuth.currentUser

        // to receive messages by topic
        FirebaseMessaging.getInstance().subscribeToTopic("users")
        FirebaseMessaging.getInstance().subscribeToTopic("allusers")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("asd")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("asdzxc")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("user")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("users")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("hola")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("holar")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("test")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("test2")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("\n"+"asd")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("\n"+"hola")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("\n"+"test")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("asd"+"\n")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("hola"+"\n")
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("test"+"\n")




        //------------------------------------------------------------
        if (FirebaseAuth.getInstance().currentUser != null) {
            var uid = FirebaseAuth.getInstance().uid
            val df = fStore!!.collection("Users").document(uid!!)

            df.get().addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot.getString("isAdmin") != null) {
                    Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show()
                    btnGoToAdmin.isVisible = true

                }

            }
        }


        //Display the Topic list
        var tempFullName = ""
        var tempEmail = ""
        var tempUser = ""
        var tempTopic = ""

        //------------------------------------------------------------------------------------------
        var df = fStore.collection("Users").document("ADMIN")
        df.addSnapshotListener(this){ value, error ->
            tvTopics.setText(value!!.getString("TOPICS"))
        }
        //------------------------------------------------------------------------------------------
        //Display My Topic list

        //------------------------------------------------------------------------------------------
        df = fStore.collection("Users").document(user!!.uid)
        df.addSnapshotListener(this){ value, error ->
            tempFullName = value!!.getString("Fullname").toString()
            tempEmail = value!!.getString("Email").toString()
            tempUser = value!!.getString("isUser").toString()
            tvMyTopicList.setText(value!!.getString("MyTopics"))
        }
        //------------------------------------------------------------------------------------------



        // to subscribe

        // cheking whether the topic exists or not----------------------------------------------
//            https://stackoverflow.com/questions/52628091/how-to-find-the-whole-word-in-kotlin-using-regex


        btnSubscribe.setOnClickListener{
            var tempTopic = tvMyTopicList.text.toString()
            var tempCheck = initTopic + etTopicNameToSub.text.toString().toLowerCase()
            val matcher = "(?i)(?<!\\p{L})$tempCheck(?!\\p{L})".toRegex()
            if (matcher.findAll(tvTopics.text.toString()).count() > 0){

                Toast.makeText(this, "Topic found", Toast.LENGTH_SHORT).show()

                // checking whether the user already subscribed or not
                var tempCheck = initTopic + etTopicNameToSub.text.toString().toLowerCase()
                val matcher = "(?i)(?<!\\p{L})$tempCheck(?!\\p{L})".toRegex()
                if (matcher.findAll(tvMyTopicList.text.toString()).count() > 0){
                    Toast.makeText(this, "Already subscribed", Toast.LENGTH_SHORT).show()
                }else{
//                    var tempNewTopicName = "/topics/" + etTopicNameToSub.text.toString().toLowerCase()
                    FirebaseMessaging.getInstance().subscribeToTopic(tempCheck)


                    val df = fStore.collection("Users").document(user!!.uid)
                    val userInfo: MutableMap<String, Any> = HashMap()
                    userInfo["Fullname"] = tempFullName
                    userInfo["Email"] = tempEmail
                    userInfo["isUser"] = "1"
                    tempTopic = tempTopic + " \n" + tempCheck
                    userInfo["MyTopics"] = tempTopic
                    df.set(userInfo)

                    Toast.makeText(this, "Subscribed to "+ tempCheck, Toast.LENGTH_SHORT).show()
                }


            }else{
                Toast.makeText(this, "Topic not found", Toast.LENGTH_SHORT).show()
            }
            etTopicNameToSub.setText("")
        }


//        // information for topic
//        btnInformation1.setOnClickListener{
//            Toast.makeText(this, "*20 character Limit \n*only \"a-z\" \"-\" \"_\" \"0-9\" allowed\n*all must be lowercase\n*no space allowed", Toast.LENGTH_LONG).show()
//        }
//        btnInformation2.setOnClickListener{
//            Toast.makeText(this, "*20 character Limit \n*only \"a-z\" \"-\" \"_\" \"0-9\" allowed\n*all must be lowercase\n*no space allowed", Toast.LENGTH_LONG).show()
//        }



        // to unsubscribe

        // cheking whether the topic exists or not----------------------------------------------
//            https://stackoverflow.com/questions/52628091/how-to-find-the-whole-word-in-kotlin-using-regex


        btnUnsubscribe.setOnClickListener{
            var tempCheck = initTopic + etTopicNameToUnsub.text.toString().toLowerCase()
            val matcher = "(?i)(?<!\\p{L})$tempCheck(?!\\p{L})".toRegex()
            if (matcher.findAll(tvMyTopicList.text.toString()).count() > 0){
                var tempMyTopicList = tvMyTopicList.text.toString().replace("\n" + tempCheck, "")


                FirebaseMessaging.getInstance().unsubscribeFromTopic(tempCheck)

                val df = fStore.collection("Users").document(user!!.uid)
                val userInfo: MutableMap<String, Any> = HashMap()
                userInfo["Fullname"] = tempFullName
                userInfo["Email"] = tempEmail
                userInfo["isUser"] = "1"
                userInfo["MyTopics"] = tempMyTopicList
                df.set(userInfo)

                Toast.makeText(this, "Topic found", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Unsubscribed from " + tempCheck, Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Topic not found", Toast.LENGTH_SHORT).show()
            }
            etTopicNameToUnsub.setText("")
        }


        tvChannelList.setOnClickListener {
            flagAdmin+=1
            if (flagAdmin == 7) {
                btnGoToAdmin.visibility = View.VISIBLE
            }
            Handler(Looper.myLooper()!!).postDelayed(Runnable { flagAdmin = 0 }, 1000)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

        btnGoToAdmin.setOnClickListener{
            startActivity(Intent(applicationContext, AdminActivity::class.java))
//            finish()
        }



    }
}