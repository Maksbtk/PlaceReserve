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

import kotlinx.android.synthetic.main.activity_main.*

import java.util.Locale.filter
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.getSystemService
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.placereserve.AuthActivity.Companion.TOTAL_COUNT
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shobhitpuri.custombuttons.GoogleSignInButton
import kotlinx.android.synthetic.main.activity_changedata.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    //инициализируем ViewModel ленивым способом
    private val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
    val database = FirebaseDatabase.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
   // lateinit var mGoogleSignInClient: GoogleSignInClient
   // lateinit var gso: GoogleSignInOptions
  //  val RC_SIGN_IN: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

//        val signIn = findViewById<View>(R.id.signInBtn) as GoogleSignInButton
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1014005822352-fpebtqgcjo9o6h2phr6oq0jf3d79eube.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//
    //    mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        var signOut = findViewById<View>(R.id.btn_sign_out) as  ImageButton
//        signIn.setOnClickListener { view: View? ->
//            signInGoogle()
//        }
        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        val btnnvg: BottomNavigationView = this.findViewById(R.id.Navigationb)
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



        signOut.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                signOutt()
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
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.


        startAuth()

    }


    private fun startAuth() {
        val user=  firebaseAuth.currentUser
        if (user == null) {
            updateUI(user)
        }




    }
    private fun signOutt() {
        firebaseAuth.signOut()
        startAuth()

    }

//    private fun signInGoogle() {
//        val signInIntent: Intent = mGoogleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }


//public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    super.onActivityResult(requestCode, resultCode, data)
//
//    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//    if (requestCode == RC_SIGN_IN) {
//        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//        try {
//            // Google Sign In was successful, authenticate with Firebase
//            val account = task.getResult(ApiException::class.java)
//            firebaseAuthWithGoogle(account!!)
//        } catch (e: ApiException) {
//            // Google Sign In failed, update UI appropriately
//            Log.w(TAG, "Google sign in failed", e)
//            // [START_EXCLUDE]
//            updateUI(null)
//            // [END_EXCLUDE]
//        }
//    }
//}



//    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
//        // [START_EXCLUDE silent]
//       // showProgressDialog()
//        // [END_EXCLUDE]
//
//        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = firebaseAuth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Snackbar.make(layout_user, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//
//                // [START_EXCLUDE]
//              //  hideProgressDialog()
//                // [END_EXCLUDE]
//            }
//    }







//    private fun signOutt() {
//        // Firebase sign out
//        firebaseAuth.signOut()
//
//        // Google sign out
//       mGoogleSignInClient.signOut().addOnCompleteListener(this) {
//            updateUI(null)
//        }
//    }




    private fun updateUI(user: FirebaseUser?) {
       // hideProgressDialog()
        val btnnvg: BottomNavigationView = this.findViewById(R.id.Navigationb)
       // intent.removeExtra(TOTAL_COUNT)
      var user = firebaseAuth.currentUser
        var flag = intent.getIntExtra(TOTAL_COUNT, 1)

                if (flag==1) {
                    if (user != null) {
                        // val myRef = database.getReference("Пользователи").child(user.displayName!!)

                        // myRef.child("email").setValue(user.email)
                        //   myRef.child("статус").setValue("1")
                        //      nameUser.text= user.displayName


                        btnnvg.setVisibility(View.VISIBLE)
                        layout_user.setVisibility(View.INVISIBLE)
                        layout_places.setVisibility(View.VISIBLE)
                        btnnvg.menu.findItem(R.id.navigation_places).setEnabled(false)
                        btnnvg.menu.findItem(R.id.navigation_user).setEnabled(true)
                    } else {
                        val auth = Intent(this@MainActivity, AuthActivity::class.java)
                        startActivity(auth)
                    }
                } else if (flag == 2){
                    if (user != null) {
                        // val myRef = database.getReference("Пользователи").child(user.displayName!!)

                        // myRef.child("email").setValue(user.email)
                        //   myRef.child("статус").setValue("1")
                        //      nameUser.text= user.displayName


                        btnnvg.setVisibility(View.VISIBLE)
                        layout_user.setVisibility(View.VISIBLE)
                        layout_places.setVisibility(View.INVISIBLE)
                        btnnvg.menu.findItem(R.id.navigation_places).setEnabled(true)
                        btnnvg.menu.findItem(R.id.navigation_user).setEnabled(false)
                    } else {
                        val auth = Intent(this@MainActivity, AuthActivity::class.java)
                        startActivity(auth)
                    }
                    intent.removeExtra(TOTAL_COUNT)
                }
    }


    companion object {
        val TEXT_REQUEST = 1
        const val TOTAL_COUNT = "total_count"


    }
}