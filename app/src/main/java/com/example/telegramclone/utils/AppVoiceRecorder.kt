package com.example.telegramclone.utils

import android.media.MediaRecorder
import com.example.telegramclone.database.APP_ACTIVITY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

class AppVoiceRecorder {

    private var mMediaRecorder = MediaRecorder()
    private lateinit var mFile: File
    private lateinit var mMessageKey: String

    fun startRecord(messageKey: String) {
        try {
            mMessageKey = messageKey
            createFileToRecorder()
            prepareMediaRecorder()
            mMediaRecorder.start()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    private fun prepareMediaRecorder() {
        mMediaRecorder.apply {
            mMediaRecorder.reset()
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            mMediaRecorder.setOutputFile(mFile.absolutePath)
            mMediaRecorder.prepare()
        }
    }

    private fun createFileToRecorder() {
        mFile = File(APP_ACTIVITY.filesDir, mMessageKey)
        mFile.createNewFile()

    }

    fun stopRecord(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            mMediaRecorder.stop()
            onSuccess(mFile, mMessageKey)
        } catch (e: Exception) {
            showToast(e.message.toString())
            mFile.delete()
        }
    }

    fun releaseRecorder() {
        try {
            mMediaRecorder.release()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }
}
