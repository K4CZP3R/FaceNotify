package k4czp3r.facenotify.ui;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspLog;

public class LogViewer extends AppCompatActivity {
    private KspLog kspLog = new KspLog();
    private static String TAG = LogViewer.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        Button button_clearLogs = findViewById(R.id.button_clearLogs);
        button_clearLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kspLog.wipeLogFile();
            }
        });
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
                setUiItems();
                if(uiRefresh){
                    uiRefreshHandler.postDelayed(this,2000);
                }
            }
        });
    }

    public void setUiItems(){
        EditText editText_logViewer = findViewById(R.id.editText_logViewer);
        editText_logViewer.post(new Runnable() {
            @Override
            public void run() {
                editText_logViewer.setText("");

                try {
                    List<String> listLog = kspLog.readLog();

                    if (listLog == null) {
                        return;
                    }

                    int scan_size;
                    if (listLog.size() <= 60) scan_size = listLog.size();
                    else scan_size = 60;

                    for (int n = scan_size - 1; n > 0; n--) {
                        editText_logViewer.append(listLog.get(n) + "\n");
                    }
                }
                catch (Exception ex){
                    editText_logViewer.setText("oof, "+ex.getMessage());
                }
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        kspLog.info(TAG, "Log viewer minimized",false);
        uiRefresh=false;
    }

    @Override
    protected  void onResume(){
        super.onResume();
        kspLog.info(TAG, "Log viewer shown", false);
        uiRefresh=true;
        startUiUpdate();
    }
}
