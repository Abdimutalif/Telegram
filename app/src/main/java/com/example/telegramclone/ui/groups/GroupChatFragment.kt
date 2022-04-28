package com.example.telegramclone.ui.groups

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentSingleChatBinding
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.models.UserModel
import com.example.telegramclone.ui.base.BaseFragment
import com.example.telegramclone.ui.message_recycler_view.views.AppViewFactory
import com.example.telegramclone.ui.screens.main_list.HomeFragment
import com.example.telegramclone.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupChatFragment : BaseFragment(R.layout.fragment_single_chat) {

    private val binding by viewBinding(FragmentSingleChatBinding::bind)
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mToolbarInfo: View
    private lateinit var mReceivingUser: UserModel
    private lateinit var mRefUser: DatabaseReference
    private lateinit var group: CommonModel
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: GroupChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: ChildEventListener
    private var mCountMessage = 10
    private var mIsScrolling = false
    private var mSmoothScrollerToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    lateinit var view1: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group = arguments?.getSerializable("group") as CommonModel
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {

        view1 = LayoutInflater.from(requireContext())
            .inflate(R.layout.choice_upload, binding.root, false)
        mBottomSheetBehavior = BottomSheetBehavior.from(binding.choice.bottomSheetChoice)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = binding.chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)
        binding.apply {
            chatInputMessage.addTextChangedListener(AppTextWatcher {
                val string = chatInputMessage.text.toString()
                if (string.isEmpty() || string == "record") {
                    chatBtnSendMessage.visibility = View.GONE
                    chatBtnAttach.visibility = View.VISIBLE
                    chatBtnVoice.visibility = View.VISIBLE
                } else {
                    chatBtnSendMessage.visibility = View.VISIBLE
                    chatBtnAttach.visibility = View.GONE
                    chatBtnVoice.visibility = View.GONE
                }
            })
            chatBtnAttach.setOnClickListener {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                attach()
            }

            CoroutineScope(Dispatchers.IO).launch {
                chatBtnVoice.setOnTouchListener { v, event ->
                    if (checkPermission(RECORD_AUDIO)) {
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            chatInputMessage.setText("recording...")
                            chatBtnVoice.setColorFilter(
                                ContextCompat.getColor(
                                    APP_ACTIVITY,
                                    R.color.primary
                                )
                            )
                            val messageKey = getMessageKey(group.id)
                            mAppVoiceRecorder.startRecord(messageKey)
                        } else if (event.action == MotionEvent.ACTION_UP) {
                            chatInputMessage.setText("")
                            chatBtnVoice.colorFilter = null
                            mAppVoiceRecorder.stopRecord { file, messageKey ->
                                upLoadFileToStorage(
                                    Uri.fromFile(file),
                                    messageKey,
                                    group.id,
                                    TYPE_MESSAGE_VOICE,
                                )
                                mSmoothScrollerToPosition = true
                            }
                        }
                    }
                    true
                }
            }
        }
    }

    private fun attach() {
        binding.choice.btnAttachFile.setOnClickListener {
            attachFile()
        }
        binding.choice.btnAttachImage.setOnClickListener {
            attachImage()
        }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.chatRecycleView
        mAdapter = GroupChatAdapter()

        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_GROUPS)
            .child(group.id)
            .child(NODE_MESSAGES)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()

            if (mSmoothScrollerToPosition) {
                mAdapter.addItemToBottom(AppViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTom(AppViewFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        mRefMessages.limitToLast(mCountMessage).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollerToPosition = false
        mIsScrolling = false
        mCountMessage += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessage).addChildEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = (activity as MainActivity).getInfoToolbar()
        mToolbarInfo.visibility = View.VISIBLE

        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(NODE_GROUPS).child(group.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

        binding.apply {
            chatBtnSendMessage.setOnClickListener {
                mSmoothScrollerToPosition = true
                val message = chatInputMessage.text.toString()
                if (message.isNotEmpty()) {
                    sendMessageToGroup(message, group.id, TYPE_TEXT) {
                        chatInputMessage.setText("")
                    }
                } else showToast("Write message!")
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey =
                        getMessageKey(group.id)
                    upLoadFileToStorage(
                        uri,
                        messageKey,
                        group.id,
                        TYPE_MESSAGE_IMAGE,
                        getFilenameFromUri(uri)
                    )
                    mSmoothScrollerToPosition = true
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey =
                        getMessageKey(group.id)
                    val filename = getFilenameFromUri(uri!!)
                    upLoadFileToStorage(uri, messageKey, group.id, TYPE_MESSAGE_FILE, filename)
                    mSmoothScrollerToPosition = true
                }
            }
        }
    }

    private fun getFilenameFromUri(uri: Uri): String {
        var result = ""
        val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        } finally {
            cursor?.close()
            return result
        }
    }

    private fun initInfoToolbar() {
        mToolbarInfo.findViewById<CircleImageView>(R.id.toolbar_chat_image)
            .downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_fullname).text =
            mReceivingUser.fullName
        mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_status).text =
            mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.destroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(group.id) {
                showToast("Chat cleared!")
                replaceFragment(HomeFragment())
            }

            R.id.menu_delete_chat -> deleteChat(group.id) {
                showToast("Chat deleted!")
                replaceFragment(HomeFragment())
            }
        }
        return true
    }
}