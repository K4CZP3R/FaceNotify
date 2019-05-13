package k4czp3r.facenotify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.ui.MainActivity;

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
