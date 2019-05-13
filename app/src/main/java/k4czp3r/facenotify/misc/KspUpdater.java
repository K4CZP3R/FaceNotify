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

import k4czp3r.facenotify.R;

public class KspUpdater {

    private static String kspUpdateCheckURL = "https://kspfacenotify.kacperswebsite.xyz/check";
    private static String kspChangelogURL = "https://kspfacenotify.kacperswebsite.xyz/changelog";

    private static String TAG = KspUpdater.class.getCanonicalName();
    KspLog kspLog = new KspLog();
    KspPreferences kspPreferences = new KspPreferences();

    public void KspShowChangelog(Context context) {
        boolean needToShowIt = kspPreferences.preferenceReadBoolean(context.getString(R.string.pref_new_version));
        if(!needToShowIt) return;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, kspChangelogURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String changelog = response.getString("changelog");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Changelog");
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, kspUpdateCheckURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String serverVersion = response.getString("latest_version");
                    String appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                    if(!appVersion.equals(serverVersion)){
                        kspLog.info(TAG, "Update required!",true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Update ready!");
                        builder.setMessage("There is a new update. Go to XDA and download it! ("+serverVersion+")");
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
