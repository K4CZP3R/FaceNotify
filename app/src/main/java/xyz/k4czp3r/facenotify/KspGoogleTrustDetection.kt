package xyz.k4czp3r.facenotify

import android.app.KeyguardManager
import android.content.Context
import android.util.Log
import xyz.k4czp3r.facenotify.helpers.SecureSettingsHelper
import xyz.k4czp3r.facenotify.helpers.SharedPrefs

class KspGoogleTrustDetection {
    private val TAG = KspGoogleTrustDetection::class.qualifiedName
    private val kspLogcat = KspLogcat()
    private val secureSettingsHelper = SecureSettingsHelper()
    private val sharedPrefs = SharedPrefs()

    fun isDeviceTrusted(): Boolean{
        val keyguardManager = FaceNotify.instance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return !keyguardManager.isDeviceLocked
    }


}