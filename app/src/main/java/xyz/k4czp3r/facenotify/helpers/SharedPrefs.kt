package xyz.k4czp3r.facenotify.helpers

import android.content.Context
import android.content.SharedPreferences
import xyz.k4czp3r.facenotify.FaceNotify

enum class PrefsKeys {
    SELECTED_MODE, START_AT_BOOT, PERMISSIONS_GRANTED, SELECTED_NOTIFICATION_MODE, COMP_CHECK_SEEN, DELAY_AFTER_DETECT, COMPLY_WITH_GOOGLE_TRUST_AGENT
}

class SharedPrefs {
    private var sharedPrefs: SharedPreferences =
        FaceNotify.instance.getSharedPreferences("FaceNotifyPreferences", Context.MODE_PRIVATE)

    fun putStr(key: PrefsKeys, value: String){
        with(sharedPrefs.edit()){
            putString(key.name, value)
            commit()
        }
    }
    fun getStr(key: PrefsKeys): String{
        return sharedPrefs.getString(key.name,"")!!
    }

    fun putInt(key: PrefsKeys, value: Int){
        with(sharedPrefs.edit()){
            putInt(key.name, value)
            commit()
        }
    }
    fun getInt(key: PrefsKeys): Int{
        return sharedPrefs.getInt(key.name,0)
    }

    fun putBoolean(key: PrefsKeys, value: Boolean){
        with(sharedPrefs.edit()){
            putBoolean(key.name, value)
            commit()
        }
    }
    fun getBoolean(key: PrefsKeys): Boolean{
        return sharedPrefs.getBoolean(key.name, false)
    }
}