package com.example.placereserve

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class AuthActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val database = FirebaseDatabase.getInstance()
    private var userName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        firebaseAuth = FirebaseAuth.getInstance()





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


//        btn_sign_out.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View) {
//                v.startAnimation(animAlpha)
//                signOut()
//            }
//        })



        buttonResend.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                resendVerificationCode(fieldPhoneNumber.text.toString(), resendToken)
            }
        })

        buttonSaveNameUser.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)

                userName = fieldNameUser.text.toString()

                buttonSaveNameUser.setVisibility(View.INVISIBLE)
                text_vvediteNameUser.setVisibility(View.INVISIBLE)
                fieldNameUser.setVisibility(View.INVISIBLE)

                buttonStartVerification.setVisibility(View.VISIBLE)
                text_vvedite.setVisibility(View.VISIBLE)
                fieldPhoneNumber.setVisibility(View.VISIBLE)

                fieldVerificationCode.setVisibility(View.INVISIBLE)
                buttonVerifyPhone.setVisibility(View.INVISIBLE)
                text_resend.setVisibility(View.INVISIBLE)
                buttonResend.setVisibility(View.INVISIBLE)


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
                    Snackbar.make(
                        findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT
                    ).show()
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
            callbacks
        ) // OnVerificationStateChangedCallbacks
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
            token
        ) // ForceResendingToken from callbacks
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

//    private fun signOut() {
//        firebaseAuth.signOut()
//        updateUI(STATE_INITIALIZED)
//    }


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



                buttonSaveNameUser.setVisibility(View.VISIBLE)
                text_vvediteNameUser.setVisibility(View.VISIBLE)
                fieldNameUser.setVisibility(View.VISIBLE)

                buttonStartVerification.setVisibility(View.INVISIBLE)
                text_vvedite.setVisibility(View.INVISIBLE)
                fieldPhoneNumber.setVisibility(View.INVISIBLE)

                fieldVerificationCode.setVisibility(View.INVISIBLE)
                buttonVerifyPhone.setVisibility(View.INVISIBLE)
                text_resend.setVisibility(View.INVISIBLE)
                buttonResend.setVisibility(View.INVISIBLE)

            }
            STATE_CODE_SENT -> {
                /* Code sent state, show the verification field, the */
                enableViews(buttonVerifyPhone, buttonResend, fieldPhoneNumber, fieldVerificationCode)
                disableViews(buttonStartVerification)
                Snackbar.make(layout_auth, "Код отправлен", Snackbar.LENGTH_SHORT).show()

                buttonStartVerification.setVisibility(View.INVISIBLE)
                text_vvedite.setVisibility(View.INVISIBLE)
                fieldPhoneNumber.setVisibility(View.INVISIBLE)

                fieldVerificationCode.setVisibility(View.VISIBLE)
                buttonVerifyPhone.setVisibility(View.VISIBLE)
                text_resend.setVisibility(View.VISIBLE)
                buttonResend.setVisibility(View.VISIBLE)


            }
            STATE_VERIFY_FAILED -> {
                // Verification has failed, show all options
                enableViews(
                    buttonStartVerification, buttonVerifyPhone, buttonResend, fieldPhoneNumber,
                    fieldVerificationCode
                )
                disableViews(buttonVerifyPhone)
                Snackbar.make(layout_auth, "Ошибка", Snackbar.LENGTH_SHORT).show()


            }
            STATE_VERIFY_SUCCESS -> {
                // Verification has succeeded, proceed to firebase sign in
                disableViews(
                    buttonStartVerification, buttonVerifyPhone, buttonResend, fieldPhoneNumber,
                    fieldVerificationCode
                )
                Snackbar.make(layout_auth, "Успешно", Snackbar.LENGTH_SHORT).show()
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
                Snackbar.make(layout_auth, "Ошибка!", Snackbar.LENGTH_SHORT).show()

            STATE_SIGNIN_SUCCESS -> {
            }
        } // Np-op, handled by sign-in check

                if (user != null) {

                    userName= fieldNameUser.text.toString()

                    val myRef = database.getReference("Пользователи").child(user.phoneNumber!!)
                    myRef.child("ИмяПользователя").setValue(userName)
                    myRef.child("Cтатус").setValue("1")
                    //   nameUser.text= user.displayName

                    val inte = Intent(this, MainActivity::class.java)
                    startActivity(inte)

                } else {

                }
    }

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