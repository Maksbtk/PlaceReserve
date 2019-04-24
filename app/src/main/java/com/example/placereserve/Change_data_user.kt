package com.example.placereserve

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.placereserve.MainActivity.Companion.TOTAL_COUNT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_changedata.*
import kotlinx.android.synthetic.main.activity_main.*

class Change_data_user : AppCompatActivity()  {
    private lateinit var firebaseAuth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    private var UserName: String= ""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changedata)
        val animAlpha : Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)


        save_userdata.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                firebaseAuth = FirebaseAuth.getInstance()
                v.startAnimation(animAlpha)
                //
                // Русик, здесть надо занести новое имя и телефон в бд
                //
                UserName = edit_name_user.text.toString()
                val user = firebaseAuth.currentUser
                val myRef = database.getReference("Пользователи").child(user?.phoneNumber!!)
                myRef.child("ИмяПользователя").setValue(UserName)
                val sud = Intent(this@Change_data_user, MainActivity::class.java)
                sud.putExtra(MainActivity.TOTAL_COUNT,2 )
                startActivity(sud)


            }
        })
        back_in_useer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                val biu = Intent(this@Change_data_user, MainActivity::class.java)
                biu.putExtra(MainActivity.TOTAL_COUNT,2 )
                startActivity(biu)
            }
        })
    }



}

