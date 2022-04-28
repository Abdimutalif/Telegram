package com.example.telegramclone.utils

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.database.APP_ACTIVITY
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
    /*APP_ACTIVITY.runOnUiThread {
    }*/
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    /* Функция расширения для AppCompatActivity, позволяет устанавливать фрагменты */
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }
}

fun hideKeyBoard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get().load(url).fit().placeholder(R.drawable.default_photo).into(this)
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFragment = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFragment.format(time)
}

fun getPlurals(count: Int) = APP_ACTIVITY.resources.getQuantityString(
    R.plurals.count_members, count, count
)