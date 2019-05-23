package k4czp3r.facenotify.service_facedetection;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import k4czp3r.facenotify.FaceNotifyApp;

import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.misc.KspPreferences;


public class KspFaceDetectionMain {
    private static String TAG = KspFaceDetectionMain.class.getCanonicalName();

    private KspFaceDetectionFunctions kspFaceDetectionFunctions = new KspFaceDetectionFunctions();
    private KspPreferences kspPreferences = new KspPreferences();
    KspLog kspLog = new KspLog();

    private Handler repeatCheck_handler = new Handler();
    private static boolean repeatCheck_continueChecking = true;

    public class RepeatCheck implements Runnable {
        private String TAG_RC =RepeatCheck.class.getCanonicalName();
        int config_delayAfterFaceUnlock;
        int repeatCheck_currentCheck = 0, repeatCheck_maxChecks=120, repeatSpeed=100;
        long startDetectDateMs;
        long startTime = System.currentTimeMillis();
        RepeatCheck(int config_delayAfterFaceUnlock, long startDetectDateMs){
            this.config_delayAfterFaceUnlock = config_delayAfterFaceUnlock;
            this.startDetectDateMs = startDetectDateMs;
            kspLog.debug(TAG_RC, "RepeatCheck init",false);
            kspLog.debug(TAG_RC, "Current check starts from: "+repeatCheck_currentCheck,true);

        }


        @Override
        public void run(){
            kspLog.info(TAG_RC, "============",false);
            kspLog.info(TAG_RC, "Check no."+repeatCheck_currentCheck,true);

            if(kspFaceDetectionFunctions.isFaceUnlocked(this.startDetectDateMs)){
                kspLog.info(TAG_RC, "[OK] Face recognized!",false);
                kspLog.info(TAG_RC,"This took "+(System.currentTimeMillis()-startTime)+"ms",true);


                kspLog.info(TAG_RC, String.format("Will show notification in %1$sms",config_delayAfterFaceUnlock),false);
                repeatCheck_handler.postDelayed(new FaceDetectedAction(),config_delayAfterFaceUnlock);
                repeatCheck_continueChecking = false;
            }
            else{
                kspLog.warn(TAG_RC, "Face not found!",false);
            }

            this.repeatCheck_currentCheck+=1;
            if(this.repeatCheck_currentCheck<repeatCheck_maxChecks && repeatCheck_continueChecking){
                repeatCheck_handler.postDelayed(this, repeatSpeed);
            }
            else{
                kspLog.info(TAG_RC, "Stopped checking!",false);
            }

        }
    }
    public class FaceDetectedAction implements Runnable{
        @Override
        public void run(){
            kspFaceDetectionFunctions.setNotificationVisibility(1);
        }
    }
    private void setContinueChecking(boolean value){
        KspFaceDetectionMain.repeatCheck_continueChecking = value;
    }


    public void startScreenOffAction(){
        kspLog.info(TAG, "Screen off action",false);
        setContinueChecking(false);
        kspFaceDetectionFunctions.setNotificationVisibility(0);

    }
    public void startUserPresentAction(){
        kspLog.info(TAG, "User present action",false);
        setContinueChecking(false);
    }

    //Function get executed after screen on bhandler
    public void startScreenOnAction(){
        kspLog.info(TAG, "Screen on action",false);
        setContinueChecking(true);
        int delayAfterFaceDetection=-1;
        try{
            //check if frDelay is saved as string
            String prefString = kspPreferences.preferenceReadString(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_delay_key));
            delayAfterFaceDetection = Integer.parseInt(prefString);
        }
        catch (Exception ex){
            kspLog.error(TAG, "frDelay is not a string convertable to int!",true);
            kspLog.error(TAG, ex.getMessage(), true);
        }

        if(delayAfterFaceDetection == -1){
            //delay value is not changed, so we assume is saved as int
            delayAfterFaceDetection = kspPreferences.preferenceReadInt(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_delay_key));
            kspLog.info(TAG, "We can recover from this error, trying to parse frDelay as int",true);

            if(delayAfterFaceDetection == -1){
                //some serious problem, dafd is still -1...
                delayAfterFaceDetection=0; //TODO: fix me later
            }
        }


        kspLog.info(TAG, "Posting repeatcheck as delayed",false);
        ZonedDateTime startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        long currentTimeMs = System.currentTimeMillis() - startOfToday.toEpochSecond() * 1000;
        repeatCheck_handler.postDelayed(new RepeatCheck(delayAfterFaceDetection, currentTimeMs),100);
    }




}
