package k4czp3r.facenotify.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.misc.KspPreferences;
import k4czp3r.facenotify.service_facedetection.KspFaceDetectionFunctions;

public class KspBootBroadcastReceiver extends BroadcastReceiver {

    private final static String TAG = KspBootBroadcastReceiver.class.getCanonicalName();
    KspLog kspLog = new KspLog();
    KspFaceDetectionFunctions kspFaceDetectionFunctions = new KspFaceDetectionFunctions();

    @Override
    public void onReceive(Context context, Intent intent){
        kspLog.info(TAG, "Boot broadcast received",true);
        KspPreferences kspPreferences = new KspPreferences();
        String allowedToStartAtBoot = kspPreferences.preferenceReadString( FaceNotifyApp.getAppContext().getString(R.string.pref_key_fr_start_at_boot));
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && allowedToStartAtBoot.equals("true") && !kspFaceDetectionFunctions.isServiceRunning()){
            kspLog.info(TAG, "Boot completed, starting service...",true);
            KspBootService.enqueueWork(context, new Intent());
        }
    }
}
