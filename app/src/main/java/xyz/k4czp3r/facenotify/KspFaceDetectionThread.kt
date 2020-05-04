package xyz.k4czp3r.facenotify

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import xyz.k4czp3r.facenotify.helpers.PrefsKeys
import xyz.k4czp3r.facenotify.helpers.SharedPrefs
import xyz.k4czp3r.facenotify.models.DelayAfterDetectType
import xyz.k4czp3r.facenotify.models.DelayAfterDetectTypes


class KspFaceDetectionThread {
    private val tagName = KspFaceDetectionThread::class.qualifiedName
    private val sharedPrefs = SharedPrefs()
    private var repeatCheckHandler = Handler()
    private var repeatCheckHandlerThread = HandlerThread("repeatCheck")
    private var continueChecking = true
    private var timeOutAfterMs: Long = 4000
    private var checkEveryMs: Long = 100
    private var currentCheckMs: Long = 0
    private lateinit var currentDelayAfterDetect: DelayAfterDetectType

    var faceUnlock: Runnable = Runnable {
        KspFaceDetection().showNotifications()
    }

    private var repeatCheckRunnable: Runnable = object : Runnable {
        override fun run() {
            val faceUnlocked = KspKeyguardTrustDetection().isDeviceTrusted()
            Log.i(tagName, "isDeviceTrusted=${KspKeyguardTrustDetection().isDeviceTrusted()}")
            if (faceUnlocked) {
                Log.i(
                    tagName,
                    "posting delayed showing notification delay=${currentDelayAfterDetect.value}"
                )
                repeatCheckHandler.postDelayed(faceUnlock, currentDelayAfterDetect.value.toLong())
                continueChecking = false
            }
            currentCheckMs += checkEveryMs
            if (continueChecking && currentCheckMs <= timeOutAfterMs) repeatCheckHandler.postDelayed(
                this,
                checkEveryMs
            )
        }

    }

    private fun startDetecting() {
        this.continueChecking = true
        this.currentCheckMs = 0

        repeatCheckHandlerThread = HandlerThread("HandlerThread")
        repeatCheckHandlerThread.start()
        repeatCheckHandler = Handler(repeatCheckHandlerThread.looper)

        repeatCheckHandler.postDelayed(repeatCheckRunnable, 0)
    }

    private fun stopDetecting() {
        repeatCheckHandlerThread.quit()
        this.continueChecking = false
    }


    fun screenOnAction() {
        Log.v(tagName, "Screen on!")
        currentDelayAfterDetect =
            DelayAfterDetectTypes[sharedPrefs.getInt(PrefsKeys.DELAY_AFTER_DETECT)]
        startDetecting()

    }

    fun userPresentAction() {
        Log.v(tagName, "User after screenlock!")
        KspFaceDetection().hideNotifications()
        stopDetecting()
    }

    fun screenOffAction() {
        Log.v(tagName, "Screen off!")
        KspFaceDetection().hideNotifications()
        stopDetecting()
    }


}