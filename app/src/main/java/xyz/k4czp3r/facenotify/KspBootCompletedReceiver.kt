package xyz.k4czp3r.facenotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import xyz.k4czp3r.facenotify.helpers.PrefsKeys
import xyz.k4czp3r.facenotify.helpers.SharedPrefs

class KspBootCompletedReceiver : BroadcastReceiver(){
    private val TAG = KspBootCompletedReceiver::class.qualifiedName
    private val sharedPrefs = SharedPrefs()
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action != Intent.ACTION_BOOT_COMPLETED) return


        if(sharedPrefs.getBoolean(PrefsKeys.START_AT_BOOT)){
            Log.i(TAG, "Start at boot is enabled, so starting service!")
            KspBroadcastService.start(context!!)
        }
        else{
            Log.i(TAG, "Start at boot is disabled, so skipping service start!")
        }
    }
}