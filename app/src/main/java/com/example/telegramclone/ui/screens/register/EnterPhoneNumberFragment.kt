package com.example.telegramclone.ui.screens.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.databinding.FragmentEnterPhoneNumberBinding
import com.example.telegramclone.database.APP_ACTIVITY
import com.example.telegramclone.database.AUTH
import com.example.telegramclone.utils.replaceFragment
import com.example.telegramclone.utils.restartActivity
import com.example.telegramclone.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import java.util.concurrent.TimeUnit

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    private val binding by viewBinding(FragmentEnterPhoneNumberBinding::bind)

    private val TAG = "EnterCodeFragment"
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    lateinit var phoneNumber: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).title = "Your Phone Number"
        binding.apply {
            registerBtnNext.setOnClickListener {
                if (registerInputPhoneNumber.text.toString().isNotEmpty()) {
                    phoneNumber = registerInputPhoneNumber.text.toString()
                    sendVerificationCode(phoneNumber)
                }
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(APP_ACTIVITY)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show()
            restartActivity()
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            showToast(e.message.toString())

            if (e is FirebaseAuthInvalidCredentialsException) {
                showToast(e.message.toString())
            } else if (e is FirebaseTooManyRequestsException) {
                showToast(e.message.toString())
            }

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token

            val bundle = Bundle()
            bundle.putString("id", storedVerificationId)
            bundle.putString("phone", phoneNumber)
            val fragment = EnterCodeFragment()
            fragment.arguments = bundle
            replaceFragment(fragment)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        AUTH.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    showToast("Welcome")
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                    showToast(task.exception?.message.toString())
                }
            }
    }
}