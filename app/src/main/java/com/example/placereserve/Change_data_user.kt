package com.example.placereserve

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
                //val sud = Intent(this@Change_data_user, MainActivity::class.java)
                //sud.putExtra(TOTAL_COUNT,2 )
                //startActivity(sud)
                finish()
            }
        })
        back_in_useer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                //val biu = Intent(this@Change_data_user, MainActivity::class.java)
                //biu.putExtra(TOTAL_COUNT,2 )
                //startActivity(biu)
                finish()
            }
        })
    }



}

