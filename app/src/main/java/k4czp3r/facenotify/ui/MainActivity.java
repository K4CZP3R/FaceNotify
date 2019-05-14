package k4czp3r.facenotify.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.misc.KspUpdater;
import k4czp3r.facenotify.service.KspAnotherService;
import k4czp3r.facenotify.service_facedetection.KspFaceDetectionFunctions;

import static com.topjohnwu.superuser.internal.InternalUtils.getContext;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getCanonicalName();

    KspFaceDetectionFunctions kspFaceDetectionFunctions = new KspFaceDetectionFunctions();
    KspUpdater kspUpdater = new KspUpdater();
    KspLog kspLog = new KspLog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        boolean bootserviceStart = intent.getBooleanExtra("startService",false);
        if(bootserviceStart){
            kspLog.info(TAG, "startService intent found, starting service and exiting!",true);

            boolean nvtd_success = kspFaceDetectionFunctions.setNotificationVisibilityToDefault(); //Be sure everything is reverted!
            if(!nvtd_success){
                Toast.makeText(this, "Setting secure settings failed, look at logcat/applog to see more info!",Toast.LENGTH_LONG).show();
                kspLog.error(TAG, "Setting to defaults, failed!",true);

            }
            else {
                startService(new Intent(getContext(), KspAnotherService.class));
                Toast.makeText(this, getString(R.string.btn_start_service_value_started), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        Context context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Check for updates
        kspUpdater.KspAppUpdateCheck(this);
        kspUpdater.KspShowChangelog(this);


        findViewById(R.id.button_startService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!kspFaceDetectionFunctions.isServiceRunning()){ //TODO: Move this function to k4czp3r.facenotify.service package
                    boolean nvtd_success = kspFaceDetectionFunctions.setNotificationVisibilityToDefault(); //Be sure everything is reverted!
                    if(!nvtd_success){
                        kspLog.error(TAG, "Setting to defaults, failed!",true);
                        Toast.makeText(context, "Setting secure settings failed, look at logcat/applog to see more info!",Toast.LENGTH_LONG).show();
                    }
                    else {
                        kspLog.info(TAG, "Starting service!", true);
                        startForegroundService(new Intent(view.getContext(), KspAnotherService.class));
                        Toast.makeText(context, getString(R.string.btn_start_service_value_started), Toast.LENGTH_LONG).show();
                    }
                    //finish();
                }
                else{
                    kspLog.warn(TAG, "Service is already running!",true);
                    Toast.makeText(context, getString(R.string.btn_start_service_value_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.button_stopService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(kspFaceDetectionFunctions.isServiceRunning()){
                    boolean nvtd_success = kspFaceDetectionFunctions.setNotificationVisibilityToDefault();
                    if(!nvtd_success){
                        kspLog.error(TAG, "Setting to defaults, failed!",true);
                        Toast.makeText(context, "Setting secure settings failed, look at logcat/applog to see more info!",Toast.LENGTH_LONG).show();
                    }
                    //We will stop service even if setting to defaults failed
                    kspLog.info(TAG, "Stopping service!",true);
                    stopService(new Intent(view.getContext(), KspAnotherService.class));
                    Toast.makeText(context, getString(R.string.btn_stop_service_value_ok), Toast.LENGTH_LONG).show();
                }
                else{
                    kspLog.warn(TAG, "Service is already stopped!",true);
                    Toast.makeText(context, getString(R.string.btn_stop_service_value_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_ksp_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        if(id == R.id.action_log_viewer){
            startActivity(new Intent(MainActivity.this, LogViewer.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Update UI items
     */
    Handler uiRefreshHandler = new Handler();
    static boolean uiRefresh = false;

    public void startUiUpdate(){
        uiRefreshHandler.post(new Runnable() {
            @Override
            public void run() {
                KspSetUiItems();
                if(uiRefresh){
                    uiRefreshHandler.postDelayed(this, 2000);
                }
            }
        });
    }

    public void KspSetUiItems(){
        boolean serviceActive = kspFaceDetectionFunctions.isServiceRunning();
        TextView serviceStatus_value = findViewById(R.id.textView_serviceStatus_value);
        if(serviceActive) serviceStatus_value.setText(getString(R.string.tv_service_status_value_enabled));
        else serviceStatus_value.setText(getString(R.string.tv_service_status_value_disabled));
    }

    @Override
    protected void onStop(){
        super.onStop();
        kspLog.info(TAG, "App minimized",true);
        uiRefresh=false;

    }

    @Override
    protected void onResume(){
        super.onResume();
        kspLog.info(TAG, "App resumed",true);
        uiRefresh=true;
        startUiUpdate();
    }
    @Override
    public void onBackPressed()
    {

    }
}
