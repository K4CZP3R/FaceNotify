package k4czp3r.facenotify.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.topjohnwu.superuser.Shell;

import java.io.File;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspConfiguration;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.misc.KspPreferences;

public class StartupActivity extends AppCompatActivity {
    KspPreferences kspPreferences = new KspPreferences();
    KspConfiguration kspConfiguration = new KspConfiguration();
    KspLog kspLog = new KspLog();
    private static String TAG = StartupActivity.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        kspLog.info(TAG,"\n===\nFaceNotify: 1.5.2\n===\n/bin/sh: "+new File("/bin/sh").exists()+"\n/system/bin/sh: "+new File("/system/bin/sh").exists(),true);

        boolean skipToMain = true;
        kspLog.info(TAG, "first run: "+kspPreferences.firstRun(),true);

        // <editor-fold desc="preferences check">
        if(kspPreferences.firstRun()){
            kspPreferences.setDefaultPreferences();
        }
        if(kspPreferences.detectionModeUnset() || !kspPreferences.detectionModeValid()){
            kspConfiguration._onlyinit_determineDetectionMode();
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Permissions and buttons">
        Button button_continue = findViewById(R.id.button_continue);
        button_continue.setVisibility(View.INVISIBLE);
        Button button_adbGrant = findViewById(R.id.button_adb_permissions_grant);
        Button button_logGrant = findViewById(R.id.button_log_permissions_grant);
        Button button_ignore_battopt = findViewById(R.id.button_ignore_battopt);
        Button button_reboot = findViewById(R.id.button_reboot);

        //Check ADB permissions
        TextView tv_adb_permission_status = findViewById(R.id.textView_adb_permission_status);
        String adb_permGranted = adbPermissionsGranted();
        if(adb_permGranted.equals("")){
            button_continue.setVisibility(View.VISIBLE);
            button_adbGrant.setEnabled(false);
            button_adbGrant.setText(getString(R.string.btn_adb_permissions_granted));
            tv_adb_permission_status.setText("");
        }
        else{
            skipToMain=false;
            tv_adb_permission_status.setText(adb_permGranted);
            kspPreferences.setFirstRun("true");
        }

        //Check log permissions
        TextView tv_log_permissions_status = findViewById(R.id.textView_log_permissions_status);
        String log_permGranted = logPermissionsGranted();
        if(log_permGranted.equals("")){
            button_logGrant.setEnabled(false);
            button_logGrant.setText(getString(R.string.btn_adb_permissions_granted));
            tv_log_permissions_status.setText("");
        }
        else{
            //skipToMain=false;
            tv_log_permissions_status.setText(log_permGranted);
        }



        //button beh.

        //ignore batt opt
        button_ignore_battopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPowerSettings();
            }
        });
        //adb grant
        button_adbGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantAdbPermissions_rootQuestion();
            }
        });

        //log grant
        button_logGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pmAskForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
        });
        //reboot
        button_reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reboot_question();
            }
        });

        //continue
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });
        // </editor-fold>
        if(skipToMain && !kspPreferences.firstRun()){
            goToMainActivity();
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Reboot dialog">
    private void reboot_question(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.stac_java_rebootquestion_dialog_title));
        builder.setMessage(getString(R.string.stac_java_rebootquestion_dialog_content));
        builder.setPositiveButton(getString(R.string.univ___ok), new StartupActivity.RequestReboot());
        builder.show();
    }

    class RequestReboot implements DialogInterface.OnClickListener {
        RequestReboot(){}

        @Override
        public void onClick(DialogInterface dialog, int which){
            kspLog.debug(TAG, "Rebooting phone.",true);
            kspPreferences.setFirstRun("false");
            Shell.su("reboot").exec();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Grant ADB permissions dialogs">
    private void grantAdbPermissions_rootQuestion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.stac_java_grantadbpermissionsrootquestion_dialog_title));
        builder.setMessage(getString(R.string.stac_java_grantadbpermissionsrootquestion_dialog_content));
        builder.setPositiveButton(getString(R.string.univ___yes), new StartupActivity.RequestRootPermissions());
        builder.setNegativeButton(getString(R.string.univ___no), new StartupActivity.IgnoreRootPermissions());
        builder.show();
    }
    class RequestRootPermissions implements DialogInterface.OnClickListener {
        RequestRootPermissions(){}

        @Override
        public void onClick(DialogInterface dialog, int which){
            Shell.su("pm grant "+getPackageName()+" android.permission.READ_LOGS").exec();
            Shell.su("pm grant "+getPackageName()+" android.permission.WRITE_SECURE_SETTINGS").exec();
            Shell.su("pm grant "+getPackageName()+" android.permission.DUMP").exec();
            Shell.su("pm grant "+getPackageName()+" android.permission.PACKAGE_USAGE_STATS").exec();
            restartActivity();



        }
    }
    class IgnoreRootPermissions implements DialogInterface.OnClickListener {
        IgnoreRootPermissions(){}

        @Override
        public void onClick(DialogInterface dialog, int which){
            AlertDialog.Builder builder = new AlertDialog.Builder(StartupActivity.this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.stac_java_ignorerootpermissionsonclick_dialog_title));
            builder.setMessage(Html.fromHtml(getString(R.string.stac_java_ignorerootpermissionsonclick_dialog_content)));
            builder.setPositiveButton(getString(R.string.univ___done),new StartupActivity.RestartAppToTest());
            builder.show();
        }
    }
    class RestartAppToTest implements DialogInterface.OnClickListener {
        RestartAppToTest(){}

        @Override
        public void onClick(DialogInterface dialog, int which){
            restartActivity();
        }
    }
    // </editor-fold>

    public void restartActivity(){
        startActivity(Intent.makeRestartActivityTask(new ComponentName(FaceNotifyApp.getAppContext(), StartupActivity.class)));
    }
    private void goToPowerSettings(){
        Intent intent_Ps = new Intent();
        intent_Ps.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        startActivity(intent_Ps);
    }
    private void goToMainActivity(){
        kspPreferences.setFirstRun("false");
        startActivity(new Intent(StartupActivity.this, MainActivity.class));
        finish();
    }
    private String logPermissionsGranted(){
        boolean perm_WRITE_EXTERNAL_STORATE = pmCheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String summary = String.format("WRITE_EXTERNAL_STORAGE: %s",perm_WRITE_EXTERNAL_STORATE);
        if(perm_WRITE_EXTERNAL_STORATE){
            return "";
        }
        return summary;
    }
    private String adbPermissionsGranted(){
        boolean perm_READ_LOGS = pmCheckPermission(Manifest.permission.READ_LOGS);
        boolean perm_WRITE_SECURE_SETTINGS = pmCheckPermission(Manifest.permission.WRITE_SECURE_SETTINGS);
        boolean perm_DUMP = pmCheckPermission(Manifest.permission.DUMP);
        boolean perm_PACKAGE_USAGE_STATS = pmCheckPermission(Manifest.permission.PACKAGE_USAGE_STATS);

        String summary = String.format("READ_LOGS: %s\nWRITE_SECURE_SETTINGS: %s\nDUMP: %s\nPACKAGE_USAGE_STATS: %s",perm_READ_LOGS, perm_WRITE_SECURE_SETTINGS, perm_DUMP, perm_PACKAGE_USAGE_STATS);
        if(perm_READ_LOGS&&perm_WRITE_SECURE_SETTINGS&&perm_DUMP&&perm_PACKAGE_USAGE_STATS){
            return "";
        }
        return summary;
    }

    private boolean pmCheckPermission(String permissionManifest){
        return getPackageManager().checkPermission(permissionManifest, getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }
    private void pmAskForPermission(String[] permissions, int requestCode){
        ActivityCompat.requestPermissions(StartupActivity.this, permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions , int[] grantResults){
        switch(requestCode){
            case 0:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    restartActivity();
                }
        }
    }
}
