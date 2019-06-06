package k4czp3r.facenotify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;


import androidx.annotation.Nullable;
import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspLog;


public class KspNewFgService extends Service  {
    KspBroadcastHandler kspBroadcastReceiver = new KspBroadcastHandler();
    KspLog kspLog = new KspLog();
    private static String TAG = KspNewFgService.class.getCanonicalName();

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String NOTIFICATION_CHANNEL_ID = "com.k4czp3r.facenotify";
        String ChannelName = "FaceNotify";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, ChannelName, NotificationManager.IMPORTANCE_LOW);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentTitle(getString(R.string.service_cname))
                .setContentText(getString(R.string.service_desc))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        kspLog.info(TAG, "startForeground(1337...",false);
        startForeground(1337, notification);

        HandlerThread handlerThread = new HandlerThread("FaceNotifyHandler");
        handlerThread.start();
        kspLog.info(TAG, "Starting handler thread",true);
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(kspBroadcastReceiver, filter, null, handler);
        kspLog.info(TAG, "registering receiver",false);

        handler.post(new Runnable() {
            @Override
            public void run() {
                kspLog.info(TAG, "Handler is still active! (5min)",false);
                handler.postDelayed(this, 1000*60*5);
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        kspLog.info(TAG, "onDestroy",false);
        try {
            kspLog.info(TAG, "unregistering receiver",false);
            unregisterReceiver(kspBroadcastReceiver);
        }
        catch (Exception ex){
            kspLog.error(TAG, "Error while trying to stop receiver:"+ex.getMessage(),true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        kspLog.appendLog(TAG,"onBind O.o");

        return null;
    }


}
