package k4czp3r.facenotify.service;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.misc.KspLog;

public class KspBootService extends JobIntentService {
    public static final int JOB_ID = 0x01;
    private final static String TAG = KspBootService.class.getCanonicalName();
    KspLog kspLog = new KspLog();

    public static void enqueueWork(Context context, Intent work){
        enqueueWork(context, KspBootService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent){
        kspLog.info(TAG, "onHandleWork",true);
        startForegroundService(new Intent(FaceNotifyApp.getAppContext(), KspAnotherService.class));



    }


}
