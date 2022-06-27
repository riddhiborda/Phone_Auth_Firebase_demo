package com.example.mykotlinproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinproject.databinding.ActivityMainBinding
import com.example.mykotlinproject.databinding.DialogItemBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

     var filterBottomBinding: DialogItemBinding ? = null
     var  filterBottomDialog: BottomSheetDialog ? = null
    lateinit var txt : TextView
lateinit var auth : FirebaseAuth
lateinit var binding:ActivityMainBinding
var storedVerificationId =""
   var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("TAG", "onVerificationCompleted:==>$credential")
            //signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("TAG", "onVerificationFailed==?", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("TAG", "onCodeSent:==>$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            //resendToken = token
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnsend.setOnClickListener {
//            showFilterBottomSheet()

            auth = FirebaseAuth.getInstance();
            sendOtp()
        }


        binding.btnVerify.setOnClickListener {

                // below line is used for getting
                // credentials from our verification id and code.
                var credential = PhoneAuthProvider.getCredential(storedVerificationId, binding.edtOTP.text.toString());

                // after getting credential we are
                // calling sign in method.
                signInWithCredential(credential);

        }
    }


    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        auth.signInWithCredential(credential)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    // if the code is correct and the task is successful
                    // we are sending our user to new activity.
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                } else {
                    // if the code is not correct then we are
                    // displaying an error message to the user.



                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendOtp() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+916354247752")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }





}