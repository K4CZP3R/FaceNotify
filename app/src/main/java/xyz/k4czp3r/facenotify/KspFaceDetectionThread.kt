package xyz.k4czp3r.facenotify

import android.content.Context
import android.util.Log
import android.os.Handler;
import android.os.HandlerThread
import xyz.k4czp3r.facenotify.helpers.*
import xyz.k4czp3r.facenotify.models.DelayAfterDetectType
import xyz.k4czp3r.facenotify.models.DelayAfterDetectTypes
import xyz.k4czp3r.facenotify.models.UnlockType
import xyz.k4czp3r.facenotify.models.UnlockTypes

class KspFaceDetectionThread() {
    private val TAG = KspFaceDetectionThread::class.qualifiedName
    private val sharedPrefs = SharedPrefs()
    private val secureSettingsHelper = SecureSettingsHelper()
     private var repeatCheckHandler = Handler();
    private var repeatCheckHandlerThread = HandlerThread("repeatCheck")
     private var continueChecking = true;
     private var timeOutAfterMs : Long = 4000;
     private var checkEveryMs : Long = 100;
     private var currentCheckMs : Long = 0;
    private var startCheckDateTime: String = ""
    private lateinit var currentUnlockType: UnlockType
    private lateinit var currentDelayAfterDetect: DelayAfterDetectType

    var faceUnlock: Runnable = Runnable {
        KspFaceDetection().showNotifications()
    }

     private var repeatCheckRunnable: Runnable = object : Runnable {
         override fun run() {
             //val faceUnlocked = KspFaceDetection().isFaceUnlocked(currentUnlockType, startCheckDateTime)
             val faceUnlocked = KspGoogleTrustDetection().isDeviceTrusted()
             Log.i(TAG, "isDeviceTrusted=${KspGoogleTrustDetection().isDeviceTrusted()}")
             //Log.i(TAG, "faceUnlocked=$faceUnlocked")
             if(faceUnlocked) {
                 Log.i(TAG, "posting delayed showing notification delay=${currentDelayAfterDetect.value}")
                 repeatCheckHandler.postDelayed(faceUnlock, currentDelayAfterDetect.value.toLong())
                 continueChecking = false
             }
             currentCheckMs += checkEveryMs
             if (continueChecking && currentCheckMs <= timeOutAfterMs) repeatCheckHandler.postDelayed(this, checkEveryMs)
         }

     }

    private fun startDetecting(){
        this.continueChecking = true
        this.currentCheckMs = 0
        this.startCheckDateTime = TimeHelpers().getCurrentDayTime()

        repeatCheckHandlerThread = HandlerThread("HandlerThread")
        repeatCheckHandlerThread.start()
        repeatCheckHandler = Handler(repeatCheckHandlerThread.looper)

        repeatCheckHandler.postDelayed(repeatCheckRunnable, 1)
    }

    private fun stopDetecting(){
        repeatCheckHandlerThread.quit()
        this.continueChecking = false
    }


     fun screenOnAction() {
         Log.v(TAG, "Screen on!")
         if (sharedPrefs.getBoolean(PrefsKeys.COMPLY_WITH_GOOGLE_TRUST_AGENT)) {
             Log.v(TAG, "User uses smartlock, so show notification if google trusts you")
             //User uses google smartlock
             if (KspGoogleTrustDetection().isDeviceTrusted()) {
                 KspFaceDetection().showNotifications()
                 return
             }
         }
         currentUnlockType = UnlockTypes[sharedPrefs.getInt(PrefsKeys.SELECTED_MODE)]
         currentDelayAfterDetect =
             DelayAfterDetectTypes[sharedPrefs.getInt(PrefsKeys.DELAY_AFTER_DETECT)]
         startDetecting()

     }
     fun userPresentAction(){
         Log.v(TAG, "User after screenlock!")
         KspFaceDetection().hideNotifications()
         stopDetecting()
     }
     fun screenOffAction() {
         Log.v(TAG, "Screen off!")
         KspFaceDetection().hideNotifications()
         stopDetecting()
     }


 }