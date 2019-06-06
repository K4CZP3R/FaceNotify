package k4czp3r.facenotify.ui;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspConfig;
import k4czp3r.facenotify.misc.KspConfiguration;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.service_facedetection.KspFaceDetectionFunctions;

public class CompCheck extends AppCompatActivity {
    private static String TAG = CompCheck.class.getCanonicalName();
    KspConfiguration kspConfiguration = new KspConfiguration();
    KspConfig kspConfig = new KspConfig();
    KspLog kspLog = new KspLog();
    KspFaceDetectionFunctions kspFaceDetectionFunctions = new KspFaceDetectionFunctions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp_check);

        checkAllOptions();
    }

    @Override
    protected void onResume(){
        super.onResume();
        kspLog.info(TAG, "App resumed",true);
        checkAllOptions();
    }


    private void checkAllOptions(){
        TextView[] option_value = new TextView[]{
                findViewById(R.id.textView_option1_value),
                findViewById(R.id.textView_option2_value),
                findViewById(R.id.textView_option3_value),
                findViewById(R.id.textView_option4_value),
                findViewById(R.id.textView_option5_value),
                findViewById(R.id.textView_option6_value),
                findViewById(R.id.textView_option7_value)

        };
        TextView[] option_key = new TextView[]{
                findViewById(R.id.textView_option1_key),
                findViewById(R.id.textView_option2_key),
                findViewById(R.id.textView_option3_key),
                findViewById(R.id.textView_option4_key),
                findViewById(R.id.textView_option5_key),
                findViewById(R.id.textView_option6_key),
                findViewById(R.id.textView_option7_key)
        };


        String[] detect_type = new String[]{
                kspConfig.DM_miui(),
                kspConfig.DM_oneplus(),
                kspConfig.DM_smartlock(),
                kspConfig.DM_samsung_face(),
                kspConfig.DM_samsung_iris(),
                kspConfig.DM_samsung_intelligent(),
                kspConfig.DM_lg()
        };

        for(int n = 0; n<detect_type.length; n++){
            TextView current_option_key = option_key[n];
            TextView current_option_value = option_value[n];
            String current_detect_type =  detect_type[n];

            boolean detect_result = kspFaceDetectionFunctions.custom_isFaceUnlocked(kspConfig.getPhones().get(current_detect_type).get("detectLine_v2"));
            current_option_key.setText(kspConfig.getPhones().get(current_detect_type).get("readable"));
            current_option_value.setText(String.valueOf(detect_result));

            if(detect_result){
                current_option_key.setTextColor(Color.GREEN);
                current_option_value.setTextColor(Color.GREEN);
            }
            else{
                current_option_key.setTextColor(Color.RED);
                current_option_value.setTextColor(Color.RED);
            }

        }
    }
}
