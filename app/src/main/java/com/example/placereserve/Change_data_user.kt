package com.example.placereserve

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

        save_userdata.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                v.startAnimation(animAlpha)
                //
                // Русик, здесть надо занести новое имя и телефон в бд
                //
                UserName = edit_name_user.text.toString()
                val user = firebaseAuth.currentUser
                val myRef = database.getReference("Пользователи").child(user?.phoneNumber!!)
                myRef.child("ИмяПользователя").setValue(UserName)
                finish()
            }
        })
        back_in_useer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                finish()
            }
        })
    }



}

