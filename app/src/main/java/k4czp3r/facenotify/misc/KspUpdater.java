package k4czp3r.facenotify.misc;


import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;

public class KspUpdater {

    private static String kspServerBackend = FaceNotifyApp.getAppContext().getString(R.string.ksup_java_kspupdater_serverbackendurl);

    private static String TAG = KspUpdater.class.getCanonicalName();
    private KspLog kspLog = new KspLog();
    private KspPreferences kspPreferences = new KspPreferences();

    public void KspShowChangelog(Context context) {
        boolean needToShowIt = kspPreferences.preferenceReadBoolean(context.getString(R.string.pref_new_version));
        if(!needToShowIt) return;

        String app_track = context.getString(R.string.app_track);
        String serverUrl = kspServerBackend+app_track;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, serverUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String changelog = response.getString("changelog");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.ksup_java_kspshowchangelogonresponse_dialog_title));
                    builder.setMessage(Html.fromHtml(changelog,0));
                    builder.show();
                    kspPreferences.preferenceSaveBoolean(context.getString(R.string.pref_new_version),false);

                } catch (Exception ex) {
                    kspLog.error(TAG, ex.getMessage(), true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kspLog.warn(TAG, "Service is not av. ",true);
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);

    }
    public void KspAppUpdateCheck(Context context){
        String app_track = context.getString(R.string.app_track);
        String serverUrl = kspServerBackend+app_track;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, serverUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String serverVersion = response.getString("version");

                    String appVersion = context.getString(R.string.app_version);
                    kspLog.info(TAG, "appVersion: "+appVersion,true);
                    kspLog.info(TAG, "appTrack: "+app_track,true);
                    kspLog.info(TAG, "serverVersion: "+serverVersion,true);
                    if(!appVersion.equals(serverVersion)){
                        kspLog.info(TAG, "Update required!",true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.ksup_java_kspappupdatecheck_dialog_title));
                        builder.setMessage(String.format(context.getString(R.string.ksup_java_kspappupdatecheck_dialog_content),serverVersion));
                        builder.show();
                    }
                    else{
                        kspLog.info(TAG, "Up-to-date",true);
                    }
                } catch (Exception e){
                    kspLog.error(TAG, e.getMessage(),true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kspLog.warn(TAG, "Update check service is not av.",true);
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }


}
