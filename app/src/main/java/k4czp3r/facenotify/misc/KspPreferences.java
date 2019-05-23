package k4czp3r.facenotify.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;

public class KspPreferences {
    private String prefsName = "k4czp3r.facenotify.prefs";
    KspConfig kspConfig = new KspConfig();
    private static String TAG = KspPreferences.class.getCanonicalName();


    public void preferenceSaveString(String prefName, String prefValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }
    public void preferenceSaveBoolean(String prefName, Boolean prefValue){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(prefName, prefValue);
        editor.apply();
    }
    public void preferenceSaveInt(String prefName, int prefValue){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(prefName, prefValue);
        editor.apply();
    }
    public int preferenceReadInt(String prefName){
        try{
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
            return settings.getInt(prefName, -1);
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        return -1;
    }
    public String preferenceReadString( String prefName) {
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
            return settings.getString(prefName, "undefined");
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        return "undefined";
    }
    public boolean preferenceReadBoolean(String prefName){
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
            return settings.getBoolean(prefName, false);
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        return false;
    }

    public boolean firstRun(){
        boolean fr_und =  preferenceReadString( FaceNotifyApp.getAppContext().getString(R.string.pref_first_boot)).toLowerCase().equals("undefined");
        boolean fr_true =  preferenceReadString(FaceNotifyApp.getAppContext().getString(R.string.pref_first_boot)).toLowerCase().equals("true");
        return fr_und || fr_true;
    }
    public boolean detectionModeUnset(){
        return preferenceReadString(FaceNotifyApp.getAppContext().getString(R.string.pref_fr_detect_mode__key)) == "undefined";
    }
    public boolean detectionModeValid(){
        String detectMode = preferenceReadString( FaceNotifyApp.getAppContext().getString(R.string.pref_fr_detect_mode__key));
        return detectMode.equals(kspConfig.DM_miui()) ||
                detectMode.equals(kspConfig.DM_oneplus()) ||
                detectMode.equals(kspConfig.DM_smartlock()) ||
                detectMode.equals(kspConfig.DM_samsung_face()) ||
                detectMode.equals(kspConfig.DM_samsung_intelligent()) ||
                detectMode.equals(kspConfig.DM_samsung_iris()) ||
                detectMode.equals(kspConfig.DM_lg());
    }
    public void setDetectionMode(String detectionMode){
        preferenceSaveString( FaceNotifyApp.getAppContext().getString(R.string.pref_fr_detect_mode__key), detectionMode);
    }
    public String getDetectionMode(){
        return preferenceReadString( FaceNotifyApp.getAppContext().getString(R.string.pref_fr_detect_mode__key));
    }
    public void setFirstRun(String value){
        preferenceSaveString( FaceNotifyApp.getAppContext().getString(R.string.pref_first_boot), value);
    }

    public void setDefaultPreferences(){
        Context context = FaceNotifyApp.getAppContext();
        preferenceSaveString(context.getString(R.string.pref_fr_delay_key), "0");
        preferenceSaveString( context.getString(R.string.pref_fr_hide_type_key), context.getString(R.string.pref_fr_hide_type_value_content));
        preferenceSaveString(context.getString(R.string.pref_fr_anim_type__key), context.getString(R.string.pref_fr_anim_type__value_flow));
        preferenceSaveString( context.getString(R.string.pref_key_fr_start_at_boot), "true");
        preferenceSaveBoolean(FaceNotifyApp.getAppContext().getString(R.string.pref_log_to_file__key),false);
        preferenceSaveBoolean(FaceNotifyApp.getAppContext().getString(R.string.pref_new_version),true);

    }
}
