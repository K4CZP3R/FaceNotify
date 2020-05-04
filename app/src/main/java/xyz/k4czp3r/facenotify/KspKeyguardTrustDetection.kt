package xyz.k4czp3r.facenotify

import android.app.KeyguardManager
import android.content.Context

class KspKeyguardTrustDetection {
    fun isDeviceTrusted(): Boolean {
        val keyguardManager =
            FaceNotify.instance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return !keyguardManager.isDeviceLocked
    }


}