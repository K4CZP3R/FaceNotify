package k4czp3r.facenotify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspLog;

public class KspAnotherService extends Service {
    private static final int ID_SERVICE = 1338;
    private static String TAG = KspAnotherService.class.getCanonicalName();
    KspLog kspLog = new KspLog();
    KspBroadcastHandler KspBroadcastHandler = new KspBroadcastHandler();

    @Override
    public void onDestroy(){
        super.onDestroy();
        kspLog.info(TAG, "onDestroy",false);
        try {
            kspLog.info(TAG, "unregistering receiver",false);
            unregisterReceiver(KspBroadcastHandler);
        }
        catch (Exception ex){
            kspLog.error(TAG, "Error while trying to stop receiver:"+ex.getMessage(),true);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //Register breceiver
        HandlerThread handlerThread = new HandlerThread("FaceNotifyHandler");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(KspBroadcastHandler, filter, null, handler);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentTitle(getString(R.string.service_cname))
                .setContentText(getString(R.string.service_desc))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(ID_SERVICE, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "facenotify_foreground";
        String channelName = "FaceNotify - Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

}
