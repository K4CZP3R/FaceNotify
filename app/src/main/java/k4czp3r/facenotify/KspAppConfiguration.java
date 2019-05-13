package k4czp3r.facenotify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.topjohnwu.superuser.Shell;

import org.json.JSONObject;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class KspAppConfiguration {
    /*private String PrefsName = "k4czp3r.FaceNotify.prefs";
    private final static String TAG = "FaceNotify.KspAppConfiguration";
    private String[] kspSupportedDevices = {
            "ONEPLUS_A6003", "ONEPLUS_A6000", //oneplus6
            "ONEPLUS_A6013", //oneplus6t
            "ONEPLUS_A5010" //oneplus5t
    };
    private String kspUpdateCheckURL = "https://kspfacenotify.kacperswebsite.xyz/check";

    private KspUiDialogs kspUiDialogs = new KspUiDialogs();
    Context appContext = FaceNotifyApp.getAppContext();



    public String kspGetDeviceName(){
        String manufacturer = Build.MANUFACTURER.toUpperCase();
        String model = Build.MODEL.toUpperCase();
        if(model.startsWith(manufacturer)){
            return model.replace(' ','_');
        }
        return (manufacturer+"."+model).replace(' ','_');
    }
    public void KspAppUpdateCheck(Context context){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, kspUpdateCheckURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String serverVersion = response.getString("latest_version");
                    String appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                    if(!appVersion.equals(serverVersion)){

                        Log.v(TAG, "Update required!");
                        AlertDialog.Builder kspUpdateReady = kspUiDialogs.KspInfoDialog(context, "Update ready!","There is a new update. Go to XDA and download it! ("+serverVersion+")");
                        kspUpdateReady.setNeutralButton("Ok!",null);
                        kspUpdateReady.create().show();
                    }
                    else{
                        Log.v(TAG, "Up-to-date");
                    }
                } catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Update check service is not avaiable!");
                //Log.e(TAG, error.getLocalizedMessage());
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }
    public void KspPhoneModelCheck(Context context){
        String kspPhoneChecked = KspPreferenceReadString(appContext,
                appContext.getString(R.string.pref_key_ksp_phoneModelChecked));

        if(kspPhoneChecked.equals("false") || kspPhoneChecked.equals("undefined")){
            if(Arrays.asList(kspSupportedDevices).contains(kspGetDeviceName())){
                Log.v(TAG, "Device is supported!");
                KspPreferenceSaveString(appContext,
                        appContext.getString(R.string.pref_key_device_supported),
                        "true");
            }
            else{
                AlertDialog.Builder notSupported = kspUiDialogs.KspErrorDialog(
                        context,
                        "Your phone ("+kspGetDeviceName()+") is not supported!"
                );
                notSupported.setNeutralButton("Ok",null);
                notSupported.create().show();
            }
            KspPreferenceSaveString(appContext,
                    appContext.getString(R.string.pref_key_ksp_phoneModelChecked),
                    "true");
        }
    }
    public void KspSendFeedback(Context context){
        String body = null;
        try {
            String appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nWRITE FEEDBACK ABOVE THIS LINE\n-----------------------------\n" +
                    "Please don't remove this information\n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + appVersion + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {

        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:contact@kacperswebsite.xyz"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"FaceNotify - feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(emailIntent, "Send it using..."));

    }

    public void KspStartupCheck(Context context){ //if needed
        String kspFirstRunCompleted = KspPreferenceReadString(FaceNotifyApp.getAppContext(), appContext.getString(R.string.pref_key_ksp_firstRunCompleted));
        String kspRooted = KspPreferenceReadString(FaceNotifyApp.getAppContext(), appContext.getString(R.string.pref_key_ksp_root));
        String kspWorkingMode = KspPreferenceReadString(FaceNotifyApp.getAppContext(), FaceNotifyApp.getAppContext().getString(R.string.pref_key_working_mode));

        if(kspFirstRunCompleted.equals("false") || kspFirstRunCompleted.equals("undefined")){
            KspPreferenceSaveString(appContext, appContext.getString(R.string.pref_key_fr_hide_type), appContext.getString(R.string.pref_value_fr_hide_type_content));
            KspPreferenceSaveString(appContext, appContext.getString(R.string.pref_key_fr_delay),"0");
            KspPreferenceSaveString(appContext, appContext.getString(R.string.pref_key_fr_start_at_boot),"true");
            KspPreferenceSaveString(appContext, kspWorkingMode, appContext.getString(R.string.pref_working_mode_noroot));

            AlertDialog.Builder kspDialogSetupBuilder = kspUiDialogs.KspInfoDialog(context, appContext.getString(R.string.KspAppConfiguration_firstRun_howTo_title),appContext.getString(R.string.KspAppConfiguration_firstRun_howTo_msg));
            kspDialogSetupBuilder.setNeutralButton(appContext.getString(R.string.KspAppConfiguration_firstRun_neutralButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(kspWorkingMode.equals(appContext.getString(R.string.pref_working_mode_root))) {
                        Toast.makeText(context, appContext.getString(R.string.KspAppConfiguration_toast_rootCheck),Toast.LENGTH_SHORT).show();
                        if (!Shell.rootAccess()) {
                            kspUiDialogs.KspErrorDialog(context, appContext.getString(R.string.KspAppConfiguration_rootDenied)).create().show();
                            KspPreferenceSaveString(appContext, appContext.getString(R.string.pref_key_ksp_root), "false");
                        } else {
                            Toast.makeText(context, appContext.getString(R.string.KspAppConfiguration_toast_rootGranted), Toast.LENGTH_SHORT).show();
                            KspPreferenceSaveString(appContext, appContext.getString(R.string.pref_key_ksp_root), "true");
                        }
                    }
                    KspPreferenceSaveString(appContext, appContext.getString(R.string.pref_key_ksp_firstRunCompleted),"true");

                }
            });
            kspDialogSetupBuilder.setNegativeButton(appContext.getString(R.string.KspAppConfiguration_firstRun_negativeButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KspExitAPP();
                }
            });
            kspDialogSetupBuilder.create().show();
        }
        else if(kspWorkingMode.equals(FaceNotifyApp.getAppContext().getString(R.string.pref_working_mode_root))&&(kspRooted.equals("false") || kspRooted.equals("undefined"))){
            AlertDialog.Builder kspDialogRootBuilder = kspUiDialogs.KspInfoDialog(context,appContext.getString(R.string.KspAppConfiguration_kspRooted_notDetected_title),appContext.getString(R.string.KspAppConfiguration_kspRooted_notDetected_msg));
            kspDialogRootBuilder.setPositiveButton(appContext.getString(R.string.KspAppConfiguration_kspRooted_positiveButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KspPreferenceSaveString(FaceNotifyApp.getAppContext(), appContext.getString(R.string.pref_key_ksp_firstRunCompleted),"false");
                    kspUiDialogs.KspInfoDialog(context,appContext.getString(R.string.KspAppConfiguration_kspRooted_onClick_title),appContext.getString(R.string.KspAppConfiguration_kspRooted_onClick_msg)).create().show();
                }
            });
            kspDialogRootBuilder.setNegativeButton(appContext.getString(R.string.KspAppConfiguration_kspRooted_negativeButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    kspUiDialogs.KspErrorDialog(context,appContext.getString(R.string.KspAppConfiguration_kspRooted_negative_onClick)).create().show();
                }
            });
            kspDialogRootBuilder.create().show();
        }
        else{
            Log.v(TAG, "No check needed!");
        }
    }
    public void KspReportError(String tag, String error){
        Log.e(TAG+".KspReportError",String.format("Something went wrong at %1$s: %2$s", tag, error));
        //Add server report Ctodo
    }
    public void KspExitAPP(){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    public void KspPreferenceSaveString(Context context, String prefName, String prefValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }
    public String KspPreferenceReadString(Context context, String prefName) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FaceNotifyApp.getAppContext());
        return settings.getString(prefName, "undefined");

    }*/
}
