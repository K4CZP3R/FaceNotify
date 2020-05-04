package xyz.k4czp3r.facenotify.helpers

import android.provider.Settings
import xyz.k4czp3r.facenotify.FaceNotify

enum class SecureSetting{
    lock_screen_show_notifications, lock_screen_allow_private_notifications
}

class SecureSettingsHelper() {
    fun putStr(key: SecureSetting, value: String): Boolean{
        Settings.Secure.putString(FaceNotify.instance.contentResolver, key.name, value)
        return true
    }
    /*fun getStr(key: SecureSetting): String{
        return Settings.Secure.getString(FaceNotify.instance.contentResolver, key.name)
    }*/



}