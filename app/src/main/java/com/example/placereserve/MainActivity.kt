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
import com.google.firebase.database.FirebaseDatabase
import com.shobhitpuri.custombuttons.GoogleSignInButton
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {



    //инициализируем ViewModel ленивым способом
    private val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }


    val database = FirebaseDatabase.getInstance()

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
  //  lateinit var signOut: Button





    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks









    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val signIn = findViewById<View>(R.id.signInBtn) as GoogleSignInButton
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1014005822352-fpebtqgcjo9o6h2phr6oq0jf3d79eube.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//




        firebaseAuth = FirebaseAuth.getInstance()
    //    mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        //var signOut = findViewById<View>(R.id.btn_sign_out) as  ImageButton


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



//        signOut.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View) {
//                v.startAnimation(animAlpha)
//                signOutt()
//            }
//        })

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
//                        var user = firebaseAuth.currentUser
//
//                        if (user!=null) {
//                            layout_user.setVisibility(View.VISIBLE)
//                            // User is signed in.
//                        } else {
//                            // No user is signed in.
//                            layout_auth.setVisibility(View.VISIBLE)
//                        }
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


        buttonStartVerification.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                if (!validatePhoneNumber()) {
                    return
                }
                startPhoneNumberVerification(fieldPhoneNumber.text.toString())
            }
        })


        buttonVerifyPhone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                val code = fieldVerificationCode.text.toString()
                if (TextUtils.isEmpty(code)) {
                    fieldVerificationCode.error = "Cannot be empty."
                    return
                }

                verifyPhoneNumberWithCode(storedVerificationId, code)
            }
        })


        btn_sign_out.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                signOut()
            }
        })



        buttonResend.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                resendVerificationCode(fieldPhoneNumber.text.toString(), resendToken)
            }
        })


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                // [START_EXCLUDE silent]
                verificationInProgress = false
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential)
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                // [START_EXCLUDE silent]
                verificationInProgress = false
                // [END_EXCLUDE]

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    fieldPhoneNumber.error = "Invalid phone number."
                    // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT).show()
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED)
                // [END_EXCLUDE]
            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId!!)

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT)
                // [END_EXCLUDE]
            }
        }

    }



    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,      // Phone number to verify
            60,               // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this,             // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        verificationInProgress = true
    }




    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)

        // [START_EXCLUDE]
        if (verificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(fieldPhoneNumber.text.toString())
        }
        // [END_EXCLUDE]
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, verificationInProgress)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        verificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            token) // ForceResendingToken from callbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    // [START_EXCLUDE]
                    updateUI(STATE_SIGNIN_SUCCESS, user)
                    // [END_EXCLUDE]
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        // [START_EXCLUDE silent]
                        fieldVerificationCode.error = "Invalid code."
                        // [END_EXCLUDE]
                    }
                    // [START_EXCLUDE silent]
                    // Update UI
                    updateUI(STATE_SIGNIN_FAILED)
                    // [END_EXCLUDE]
                }
            }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        updateUI(STATE_INITIALIZED)
    }


    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = fieldPhoneNumber.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            fieldPhoneNumber.error = "Invalid phone number."
            return false
        }

        return true
    }

    private fun enableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = true
        }
    }
    private fun disableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = false
        }
    }









//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = firebaseAuth.currentUser
//        updateUI(currentUser)
//    }














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
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user)
        } else {
            updateUI(STATE_INITIALIZED)
        }
    }

    private fun updateUI(uiState: Int, cred: PhoneAuthCredential) {
        updateUI(uiState, null, cred)
    }

    private fun updateUI(
        uiState: Int,
        user: FirebaseUser? = firebaseAuth.currentUser,
        cred: PhoneAuthCredential? = null
    ) {
        when (uiState) {
            STATE_INITIALIZED -> {
                // Initialized state, show only the phone number field and start button
                enableViews(buttonStartVerification, fieldPhoneNumber)
                disableViews(buttonVerifyPhone, buttonResend, fieldVerificationCode)

            }
            STATE_CODE_SENT -> {
                // Code sent state, show the verification field, the
                enableViews(buttonVerifyPhone, buttonResend, fieldPhoneNumber, fieldVerificationCode)
                disableViews(buttonStartVerification)
                Snackbar.make(layout_user, "Код отправлен", Snackbar.LENGTH_SHORT).show()
                buttonStartVerification.isEnabled = false


            }
            STATE_VERIFY_FAILED -> {
                // Verification has failed, show all options
                enableViews(buttonStartVerification, buttonVerifyPhone, buttonResend, fieldPhoneNumber,
                    fieldVerificationCode)
                disableViews(buttonVerifyPhone)
                Snackbar.make(layout_user, "Ошибка", Snackbar.LENGTH_SHORT).show()


            }
            STATE_VERIFY_SUCCESS -> {
                // Verification has succeeded, proceed to firebase sign in
                disableViews(buttonStartVerification, buttonVerifyPhone, buttonResend, fieldPhoneNumber,
                    fieldVerificationCode)
                Snackbar.make(layout_user, "Успешно", Snackbar.LENGTH_SHORT).show()


                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.smsCode != null) {
                        fieldVerificationCode.setText(cred.smsCode)
                    } else {
                        fieldVerificationCode.setText("???")
                    }
                }
            }
            STATE_SIGNIN_FAILED ->
                // No-op, handled by sign-in check
                Snackbar.make(layout_user, "Ошибка!", Snackbar.LENGTH_SHORT).show()

            STATE_SIGNIN_SUCCESS -> {
            }
        } // Np-op, handled by sign-in check

        var flag = intent.getIntExtra(TOTAL_COUNT, 1)

         val btnnvg: BottomNavigationView = this.findViewById(R.id.Navigationb)
        when (flag) {
            1 -> {


                if (user!=null) {
//                    val myRef = database.getReference("Пользователи").child(user.displayName!!)

                  //  myRef.child("email").setValue(user.email)
                  //  myRef.child("статус").setValue("1")
                 //   nameUser.text= user.displayName
                    layout_auth.setVisibility(View.INVISIBLE)
                    btnnvg.setVisibility(View.VISIBLE)
                    layout_user.setVisibility(View.INVISIBLE)
                    layout_places.setVisibility(View.VISIBLE)
                    btnnvg.menu.findItem(R.id.navigation_places).setEnabled(false)
                    btnnvg.menu.findItem(R.id.navigation_user).setEnabled(true)
                } else {
                    btnnvg.setVisibility(View.INVISIBLE)
                    layout_user.setVisibility(View.INVISIBLE)
                    layout_places.setVisibility(View.INVISIBLE)
                    layout_auth.setVisibility(View.VISIBLE)
                }

            }
            2 -> {

                if (user!=null) {
                    nameUser.text= user.displayName
                    layout_auth.setVisibility(View.INVISIBLE)
                    btnnvg.setVisibility(View.VISIBLE)
                    layout_user.setVisibility(View.VISIBLE)
                    layout_places.setVisibility(View.INVISIBLE)
                    btnnvg.menu.findItem(R.id.navigation_places).setEnabled(true)
                    btnnvg.menu.findItem(R.id.navigation_user).setEnabled(false)
                } else {
                    btnnvg.setVisibility(View.INVISIBLE)
                    layout_user.setVisibility(View.INVISIBLE)
                    layout_places.setVisibility(View.INVISIBLE)
                    layout_auth.setVisibility(View.VISIBLE)
                }
                intent.removeExtra(TOTAL_COUNT)



            }
        }

    }


//    private fun updateUI(user: FirebaseUser?) {
//       // hideProgressDialog()
//        val btnnvg: BottomNavigationView = this.findViewById(R.id.Navigationb)
//       // intent.removeExtra(TOTAL_COUNT)
//      var user = firebaseAuth.currentUser
//        var flag = intent.getIntExtra(TOTAL_COUNT, 1)
//
// val btnnvg: BottomNavigationView = this.findViewById(R.id.Navigationb)
//
//        when (flag) {
//            1 -> {
//
//
//                if (user!=null) {
//                    val myRef = database.getReference("Пользователи").child(user.displayName!!)
//
//                    myRef.child("email").setValue(user.email)
//                    myRef.child("статус").setValue("1")
//                    nameUser.text= user.displayName
//                    layout_auth.setVisibility(View.INVISIBLE)
//                    btnnvg.setVisibility(View.VISIBLE)
//                    layout_user.setVisibility(View.INVISIBLE)
//                    layout_places.setVisibility(View.VISIBLE)
//                    btnnvg.menu.findItem(R.id.navigation_places).setEnabled(false)
//                    btnnvg.menu.findItem(R.id.navigation_user).setEnabled(true)
//                } else {
//                    btnnvg.setVisibility(View.INVISIBLE)
//                    layout_user.setVisibility(View.INVISIBLE)
//                    layout_places.setVisibility(View.INVISIBLE)
//                    layout_auth.setVisibility(View.VISIBLE)
//                }
//
//            }
//            2 -> {
//
//                if (user!=null) {
//                    nameUser.text= user.displayName
//                    layout_auth.setVisibility(View.INVISIBLE)
//                    btnnvg.setVisibility(View.VISIBLE)
//                    layout_user.setVisibility(View.VISIBLE)
//                    layout_places.setVisibility(View.INVISIBLE)
//                    btnnvg.menu.findItem(R.id.navigation_places).setEnabled(true)
//                    btnnvg.menu.findItem(R.id.navigation_user).setEnabled(false)
//                } else {
//                    btnnvg.setVisibility(View.INVISIBLE)
//                    layout_user.setVisibility(View.INVISIBLE)
//                    layout_places.setVisibility(View.INVISIBLE)
//                    layout_auth.setVisibility(View.VISIBLE)
//                }
//                intent.removeExtra(TOTAL_COUNT)
//
//
//
//            }
//        }
//
//
//
//
//    }


    companion object {
        private const val STATE_INITIALIZED = 1
        private const val STATE_VERIFY_FAILED = 3
        private const val STATE_VERIFY_SUCCESS = 4
        private const val STATE_CODE_SENT = 2
        private const val STATE_SIGNIN_FAILED = 5
        private const val STATE_SIGNIN_SUCCESS = 6
        const val TOTAL_COUNT = "total_count"
       // private const val TAG = "GoogleActivity"
       private const val TAG = "PhoneAuthActivity"
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"

    }
}