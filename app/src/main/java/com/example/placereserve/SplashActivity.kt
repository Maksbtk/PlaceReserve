package com.example.placereserve

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.widget.Toast


class SplashActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        if (isOnline()) {
            if (user != null) {
                database.getReference("Пользователи").child(user.phoneNumber!!).child("Cтатус")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val flag = dataSnapshot.getValue(String::class.java)
                            if (flag == "1") {
                                goAuth()
                            } else if (flag == "2") {
                                goAdmin()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                        }
                    })
            } else {
                goAuth()
            }
        } else {
            Toast.makeText(this@SplashActivity, "Отсутствует интернет-соединение. Проверьте подключение к WiFi или сотовой сети", Toast.LENGTH_LONG).show()
        }
    }
    private fun goAuth(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun goAdmin(){
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}