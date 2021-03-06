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
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog
import android.widget.Toast
import kotlinx.android.synthetic.main.places_item.view.*
import kotlin.concurrent.thread


class SplashActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        if (!isOnline()) {
            Dialog()
        }
        if (user != null) {
            var ref = database.getReference("Пользователи").child(user.phoneNumber!!).child("Cтатус")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val flag = dataSnapshot.getValue(String::class.java)
                    if (flag == "1") {
                        goAuth()
                    } else if (flag == "2") {
                        goAdmin()
                    }
                    ref.removeEventListener(this)
                }

                override fun onCancelled(error: DatabaseError) {
                    ref.removeEventListener(this)
                }
            })
        } else {
            goAuth()
        }
    }

    private fun goAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goAdmin() {
        val intent: Intent = Intent(this, PlaceActivity::class.java)
        intent.putExtra("place_name","Йохан Пивохан" )
        intent.putExtra("place_address", "Проспект Кирова, 58")
        intent.putExtra("place_status", "2")
        startActivity(intent)
        finish()
    }

    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun Dialog() {
        val dialogBuilder = AlertDialog.Builder(this@SplashActivity)
        // set message of alert dialog
        dialogBuilder.setMessage("Проверьте подключение к WiFi или сотовой сети")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Ок", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Отсутствует интернет-соединение")
        // show alert dialog
        alert.show()
    }
}