package k4czp3r.facenotify.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import k4czp3r.facenotify.misc.KspConfig;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.service_facedetection.KspFaceDetectionMain;

public class KspBroadcastHandler extends BroadcastReceiver {

    public static String TAG = KspBroadcastHandler.class.getCanonicalName();
    KspLog kspLog = new KspLog();
    KspFaceDetectionMain kspFaceDetectionMain = new KspFaceDetectionMain();
    @Override
    public void onReceive(Context context, Intent intent){
        kspLog.info(TAG, "receiving broadcast",false);
        if(intent.getAction() == null){
            return;
        }

        kspLog.info(TAG,"Action: "+intent.getAction(),false);

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            kspLog.info(TAG, "Screen off event; hiding notifications",true);
            kspFaceDetectionMain.startScreenOffAction();
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            kspLog.info(TAG, "Screen on event; starting checking",true);
            kspFaceDetectionMain.startScreenOnAction();
        }
        else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            kspLog.info(TAG, "User present; stopping checking",true);
            kspFaceDetectionMain.startUserPresentAction();
        }
    }
}
