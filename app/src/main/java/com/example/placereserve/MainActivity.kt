package com.example.placereserve

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

import java.util.Locale.filter
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.*


class MainActivity : AppCompatActivity() {
    private val MY_REQUEST_CODE: Int = 7117


    //инициализируем ViewModel ленивым способом
    private val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }

    companion object {

        const val TOTAL_COUNT = "total_count"

    }

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
    lateinit var signOut: Button


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val signIn = findViewById<View>(R.id.signInBtn) as SignInButton
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1014005822352-fpebtqgcjo9o6h2phr6oq0jf3d79eube.apps.googleusercontent.com")
            .requestEmail()
            .build()



        firebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        signOut = findViewById<View>(R.id.btn_sign_out) as Button
        signOut.visibility = View.INVISIBLE
        signIn.setOnClickListener { view: View? ->
            signInGoogle()
        }


        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        val btnnvg: BottomNavigationView = findViewById(R.id.Navigationb)


        val flag = intent.getIntExtra(TOTAL_COUNT, 1)
        when (flag) {
            1 -> {
                layout_user.setVisibility(View.INVISIBLE)
                layout_places.setVisibility(View.VISIBLE)
                btnnvg.menu.findItem(R.id.navigation_places).setEnabled(false)
                btnnvg.menu.findItem(R.id.navigation_user).setEnabled(true)


            }
            2 -> {

                layout_user.setVisibility(View.VISIBLE)
                layout_places.setVisibility(View.INVISIBLE)
                btnnvg.menu.findItem(R.id.navigation_places).setEnabled(true)
                btnnvg.menu.findItem(R.id.navigation_user).setEnabled(false)

            }
        }
        // BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        //инициализируем адаптер и присваиваем его списку
        val adapter = PlacesAdapter()


        placesList.layoutManager = LinearLayoutManager(this)
        placesList.adapter = adapter

        //подписываем адаптер на изменения списка
        userViewModel.getListPlaces().observe(this, Observer {
            it?.let {
                adapter.refreshPlaces(it)
            }
        })


        layout_places.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {


            }
        })
        // слушатель на нажатиие всей области серчвью
        SearchId.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                SearchId.setIconified(false)
                // Navigationb.setVisibility(View.INVISIBLE)

            }
        })


        //слушаетль на изменение поля ввода серчвью
        SearchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                //    Toast.makeText(this@MainActivity, "слушаетль работает", Toast.LENGTH_SHORT).show()
                adapter.filter(newText)

                // adapter.refreshPlaces()
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

        })
        //слушатель меню итемов юзер\плейсес

        btnnvg.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_user -> {

                        layout_user.setVisibility(View.VISIBLE)
                        layout_places.setVisibility(View.INVISIBLE)
                        item.setEnabled(false)
                        btnnvg.menu.findItem(R.id.navigation_places).setEnabled(true)

                    }
                    R.id.navigation_places -> {
                        layout_user.setVisibility(View.INVISIBLE)
                        layout_places.setVisibility(View.VISIBLE)
                        item.setEnabled(false)

                        btnnvg.menu.findItem(R.id.navigation_user).setEnabled(true)


                    }
                }
                return false
            }
        })



        change_data.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                val cda = Intent(this@MainActivity, Change_data_user::class.java)
                startActivity(cda)
            }
        })


    }


    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        val account = completedTask.getResult(ApiException::class.java)
            updateUI(account!!)
//        try {
//            val account : GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
//            updateUI(account)
//        } catch (e: ApiException) {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
//        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            val dispTxt = findViewById<View>(R.id.dispTxt) as TextView
            dispTxt.text = account.displayName
            signOut.visibility = View.VISIBLE
            signOut.setOnClickListener { view: View? ->
                mGoogleSignInClient.signOut().addOnCompleteListener { task: Task<Void> ->
                    dispTxt.text = " "
                    signOut.visibility - View.INVISIBLE
                }
            }
        }


    }
}