package com.example.telegramclone.ui.screens.register

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentEnterCodeBinding
import com.example.telegramclone.utils.*
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class EnterCodeFragment : Fragment(R.layout.fragment_enter_code) {

    private val binding by viewBinding(FragmentEnterCodeBinding::bind)

    lateinit var id: String
    lateinit var phoneNumber: String
    private val TAG = "EnterCodeFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = arguments?.getString("id").toString()
        phoneNumber = arguments?.getString("phone").toString()

        (activity as MainActivity).title = phoneNumber

        binding.apply {
            registerInputCode.addTextChangedListener(AppTextWatcher {
                val code = registerInputCode.text.toString()
                if (code.length == 6) {
                    enterCode(code)
                }
            })
        }

    }

    private val countDownTimer = object : CountDownTimer(1000 * 60, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            /*binding.time.text = getString(
                com.google.firebase.auth.R.string.time_format,
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
            )*/
        }

        override fun onFinish() {
            /* binding.reseand.visibility = View.VISIBLE
             Toast.makeText(requireContext(), "Qayta urinib ko'ring", Toast.LENGTH_SHORT).show()*/
        }
    }

    private fun enterCode(code: String) {
        if (code.length == 6) {
            val credential = PhoneAuthProvider.getCredential(id, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        AUTH.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    val uid = AUTH.currentUser?.uid.toString()
                    val dateMap = mutableMapOf<String, Any>()
                    dateMap[CHILD_ID] = uid
                    dateMap[CHILD_PHONE] = phoneNumber
                    dateMap[CHILD_USERNAME] = uid

                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                        .addListenerForSingleValueEvent(AppValueEventListener {

                            if (!it.hasChild(CHILD_USERNAME)) {
                                dateMap[CHILD_USERNAME] = uid
                            }

                            REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                                .addOnSuccessListener {
                                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                                        .updateChildren(dateMap)
                                        .addOnSuccessListener {
                                            showToast("Welcome")
                                            restartActivity()
                                        }.addOnFailureListener { showToast(it.message.toString()) }
                                }.addOnFailureListener { showToast(it.message.toString()) }
                        })

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                    showToast(task.exception?.message.toString())
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
    }

}