package k4czp3r.facenotify.service_facedetection;

import android.media.FaceDetector;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.KspAppConfiguration;
import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspConfiguration;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.misc.KspPreferences;

public class KspFaceDetectionFunctions {
    private static String TAG = KspFaceDetectionFunctions.class.getCanonicalName();
    private KspFaceDetectionLogcat kspFaceDetectionLogcat = new KspFaceDetectionLogcat();
    KspPreferences kspPreferences = new KspPreferences();
    KspConfiguration kspConfiguration = new KspConfiguration();
    KspLog kspLog = new KspLog();

    public boolean custom_isFaceUnlocked(String detectionLogcatLine){
        List<String> logs = kspFaceDetectionLogcat.readLogs(detectionLogcatLine,"","00:00:00");
        return logs.size() > 0;
    }

    public boolean isFaceUnlocked(String startDetectDate){
        //Log.v(TAG, startDetectDate);
        //Add only logs after start of kspface

        List<String> logs = kspFaceDetectionLogcat.readLogs(kspConfiguration.getDetectionLogcatLine(),"*:D",startDetectDate);
        kspLog.info(TAG, "Logs size: "+String.valueOf(logs.size()),false);
        if(logs.size() == 0){
            return false;
        }
        String last_result = logs.get(logs.size()-1);
        kspLog.info(TAG, "lastResult: "+last_result,false);

        return last_result.contains(kspConfiguration.getDetectionUnlockValid());

    }

    public void setNotificationVisibility(int value){
        String settingName = kspPreferences.preferenceReadString( FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_key));
        String animName = kspPreferences.preferenceReadString(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_anim_type__key));

        if(settingName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_notification)) && value == 1){
            kspLog.info(TAG, "Notification will be shown, so content needs to be shown too",false);
            secureSettingsPutInt(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_content), 1);
        }

        if(settingName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_content))&& value ==1){
            kspLog.info(TAG, "Content will be shown, so notification needs to be shown too",false);
            secureSettingsPutInt(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_notification),1);

        }


        //ANIMATION SHIT

        //CONTENT/X/SHOW
        if(settingName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_content))
                && value == 1
                && animName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_anim_type__value_flow))){

            kspLog.info(TAG, "Content/Flow/show",false);

            Handler handler = new Handler();
            secureSettingsPutInt(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_notification),0);
            secureSettingsPutInt(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_content),1);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    secureSettingsPutInt(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_notification),1);
                }
            }, 500);
        }
        else if(settingName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_content))
                && value == 1
                && animName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_anim_type__value_fast))){
            kspLog.info(TAG, "Content/Fast/show",false);
            secureSettingsPutInt(settingName, value);
        }

        //NOTIFICATION/X/SHOW
        else if(settingName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_notification))
                && value == 1
                && animName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_anim_type__value_flow))){

            kspLog.info(TAG, "Notification/Flow/show: it's always flow",false);
            secureSettingsPutInt(settingName, value);
        }
        else if(settingName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_notification))
                && value == 1
                && animName.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_anim_type__value_fast))){
            kspLog.info(TAG, "Notification/Fast/show: it's always flow",false);
            secureSettingsPutInt(settingName, value);
        }

        //CONTENT_NOTIFICATION/X/HIDE
        else{
            secureSettingsPutInt(settingName, value);
        }

        //TODO: Check if it will make big delay (if hide whole, why then hide content)
        //TODO: Reshow quickly notification to fix content overlap
    }
    public boolean setNotificationVisibilityToDefault(){
        kspLog.info(TAG, "Setting notification mode to default!",false);
        String settingName_allowPrivateNotifications =  FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_content);
        String settingName_showNotifications = FaceNotifyApp.getAppContext().getString(R.string.pref_fr_hide_type_value_notification);

        boolean apn_success = secureSettingsPutInt(settingName_allowPrivateNotifications, 1);
        boolean sn_success = secureSettingsPutInt(settingName_showNotifications, 1);
        return (apn_success && sn_success);
    }

    private boolean secureSettingsPutInt(String settingName, int settingValue){
        //TODO: Add verification that permissions are granted!
        try {
            android.provider.Settings.Secure.putInt(FaceNotifyApp.getAppContext().getContentResolver(), settingName, settingValue);
            return true;
        } catch (SecurityException ex){
            kspLog.error(TAG, "Error while changing secure setting! "+ex.getMessage(),true);
            ex.printStackTrace();
            return false;
        }
    }
    private int secureSettingsGetInt(String settingName){
        try{
            int ss = android.provider.Settings.Secure.getInt(FaceNotifyApp.getAppContext().getContentResolver(), settingName);
            return ss;
        } catch (Exception ex){
            return -1;
        }
    }

    public boolean isServiceRunning(){
        try{
            Process processDumpsys = Runtime.getRuntime().exec("dumpsys activity services .KspAnotherService");
            BufferedReader readerDumpsys = new BufferedReader(new InputStreamReader(processDumpsys.getInputStream()));

            String iterLine;
            while((iterLine = readerDumpsys.readLine()) != null){
                if(iterLine.contains("app=ProcessRecord")){
                    return true;
                }
            }
        }
        catch (Exception e){
            kspLog.error(TAG, "Error while checking service!: "+e.getMessage(),false);
            e.printStackTrace();
        }
        return false;

    }
}
