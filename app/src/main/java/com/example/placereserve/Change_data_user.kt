package com.example.placereserve

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_changedata.*

class Change_data_user : AppCompatActivity()  {
    private lateinit var firebaseAuth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    private var UserName: String= ""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changedata)
        val animAlpha : Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        if (!isOnline()) {
            Dialog()
        }

        if (user != null) {
            var ref = database.getReference("Пользователи").child(user.phoneNumber!!).child("ИмяПользователя")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        edit_name_user.setText(dataSnapshot.getValue(String::class.java))
                        //after all, remove listener
                        ref.removeEventListener(this)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        ref.removeEventListener(this)
                    }
                })
        }

        save_userdata.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                v.startAnimation(animAlpha)
                if (!isOnline()) {
                    Dialog()
                    return
                }
                if (edit_name_user.text.isEmpty()) {
                    Toast.makeText(this@Change_data_user.applicationContext, "Поле не заполнено!", Toast.LENGTH_SHORT).show()
                } else {

                    UserName = edit_name_user.text.toString()
                    val user = firebaseAuth.currentUser
                    val myRef = database.getReference("Пользователи").child(user?.phoneNumber!!)
                    myRef.child("ИмяПользователя").setValue(UserName)
                    finish()
                }
            }
        })
        back_in_useer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                finish()
            }
        })
    }

    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun Dialog() {
        val dialogBuilder = AlertDialog.Builder(this@Change_data_user)

        // set message of alert dialog
        dialogBuilder.setMessage("Проверьте подключение к WiFi или сотовой сети")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Ок", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Отсутствует интернет-соединение")
        // show alert dialog
        alert.show()
    }

}

