package xyz.k4czp3r.facenotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class KspBroadcastReceiver() : BroadcastReceiver(){
    private val TAG = KspBroadcastReceiver::class.qualifiedName
    private val thread = KspFaceDetectionThread()

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "action=${intent?.action}")
        when(intent?.action){
            Intent.ACTION_SCREEN_ON -> thread.screenOnAction()
            Intent.ACTION_SCREEN_OFF -> thread.screenOffAction()
            Intent.ACTION_USER_PRESENT -> thread.userPresentAction()
        }
    }

}