package com.example.telegramclone

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.ActivityMainBinding
import com.example.telegramclone.ui.screens.register.EnterPhoneNumberFragment
import com.example.telegramclone.ui.screens.main_list.HomeFragment
import com.example.telegramclone.ui.objects.AppDrawer
import com.example.telegramclone.utils.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        APP_ACTIVITY = this
        initFirebase()
        initUser {
            /*CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }*/
            initFields()
            initFunc()
        }
        Log.d(TAG, "onCreate: $USERModel")
    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser != null) {
            mAppDrawer.create()
            replaceFragment(HomeFragment(), false)
        } else {
            replaceFragment(EnterPhoneNumberFragment(), false)
        }
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    private fun initFields() {
        mToolbar = binding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                APP_ACTIVITY,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }

    fun getInfoToolbar(): View = mToolbar.findViewById(R.id.toolbar_info)
}