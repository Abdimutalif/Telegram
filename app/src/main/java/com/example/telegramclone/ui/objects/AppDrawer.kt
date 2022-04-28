package com.example.telegramclone.ui.objects

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView

import androidx.drawerlayout.widget.DrawerLayout

import com.example.telegramclone.R
import com.example.telegramclone.ui.screens.ContactsFragment
import com.example.telegramclone.ui.screens.settings.SettingsFragment
import com.example.telegramclone.database.APP_ACTIVITY
import com.example.telegramclone.database.USERModel
import com.example.telegramclone.ui.groups.AddContactsFragment
import com.example.telegramclone.utils.downloadAndSetImage
import com.example.telegramclone.utils.replaceFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

class AppDrawer() {

    private lateinit var mDrawer: Drawer
    lateinit var mHeader: AccountHeader
    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mCurrentProfile: ProfileDrawerItem

    fun create() {
        initLoader()
        createHolder()
        createDrawer()
        mDrawerLayout = mDrawer.drawerLayout
    }

    fun disableDrawer() {
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    fun enableDrawer() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            mDrawer.openDrawer()
        }
    }

    private fun createDrawer() {
        mDrawer = DrawerBuilder()
            .withActivity(APP_ACTIVITY)
            .withToolbar(APP_ACTIVITY.mToolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(mHeader)
            .addDrawerItems(
                PrimaryDrawerItem()
                    .withIdentifier(100)
                    .withIconTintingEnabled(true)
                    .withName("New Group")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_create_groups),
                PrimaryDrawerItem()
                    .withIdentifier(101)
                    .withIconTintingEnabled(true)
                    .withName("New Secret Chat")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_secret_chat),
                PrimaryDrawerItem()
                    .withIdentifier(102)
                    .withIconTintingEnabled(true)
                    .withName("New Channel")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_create_channel),
                PrimaryDrawerItem()
                    .withIdentifier(103)
                    .withIconTintingEnabled(true)
                    .withName("Contacts")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_contacts),
                PrimaryDrawerItem()
                    .withIdentifier(104)
                    .withIconTintingEnabled(true)
                    .withName("Calls")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_phone),
                PrimaryDrawerItem()
                    .withIdentifier(105)
                    .withIconTintingEnabled(true)
                    .withName("Saved Messages")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_favorites),
                PrimaryDrawerItem()
                    .withIdentifier(106)
                    .withIconTintingEnabled(true)
                    .withName("Settings")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_settings),
                DividerDrawerItem(),
                PrimaryDrawerItem()
                    .withIdentifier(108)
                    .withIconTintingEnabled(true)
                    .withName("New Secret Chat")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_invate),
                PrimaryDrawerItem()
                    .withIdentifier(109)
                    .withIconTintingEnabled(true)
                    .withName("New Secret Chat")
                    .withSelectable(true)
                    .withIcon(R.drawable.ic_menu_help)
            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when (position) {
                        1-> replaceFragment(AddContactsFragment())
                        7 -> replaceFragment(SettingsFragment())
                        4 -> replaceFragment(ContactsFragment())
                    }
                    return false
                }
            }).build()
    }

    private fun createHolder() {
        mCurrentProfile = ProfileDrawerItem()
            .withName(USERModel.fullName)
            .withEmail(USERModel.phone)
            .withIcon(USERModel.photoUrl)
            .withIdentifier(200)

        mHeader = AccountHeaderBuilder()
            .withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                mCurrentProfile
            ).build()
    }

    fun upDateHeader() {
        mCurrentProfile
            .withName(USERModel.fullName)
            .withEmail(USERModel.phone)
            .withIcon(USERModel.photoUrl)

        mHeader.updateProfile(mCurrentProfile)
    }

    private fun initLoader() {
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.downloadAndSetImage(uri.toString())
            }
        })
    }

}