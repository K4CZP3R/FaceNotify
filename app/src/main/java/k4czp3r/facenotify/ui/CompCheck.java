package k4czp3r.facenotify.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        kspLog.info(TAG, "Option 1 - "+kspConfig.getPhones().get(kspConfig.DM_miui()).get("readable"),true);
        boolean option1 = kspFaceDetectionFunctions.custom_isFaceUnlocked(kspConfig.getPhones().get(kspConfig.DM_miui()).get("detectLine"));

        kspLog.info(TAG, "Option 2 - "+kspConfig.getPhones().get(kspConfig.DM_oneplus()).get("readable"),true);
        boolean option2 = kspFaceDetectionFunctions.custom_isFaceUnlocked(kspConfig.getPhones().get(kspConfig.DM_oneplus()).get("detectLine"));

        kspLog.info(TAG, "Option 3 - "+kspConfig.getPhones().get(kspConfig.DM_smartlock()).get("readable"),true);
        boolean option3 = kspFaceDetectionFunctions.custom_isFaceUnlocked(kspConfig.getPhones().get(kspConfig.DM_smartlock()).get("detectLine"));

        kspLog.info(TAG, "Option 4 -"+kspConfig.getPhones().get(kspConfig.DM_samsung()).get("readable"),true);
        boolean option4 = kspFaceDetectionFunctions.custom_isFaceUnlocked(kspConfig.getPhones().get(kspConfig.DM_samsung()).get("detectLine"));

        TextView option1_value = findViewById(R.id.textView_option1_value);
        TextView option1_key = findViewById(R.id.textView_option1_key);
        option1_key.setText(kspConfig.getPhones().get(kspConfig.DM_miui()).get("readable"));
        option1_value.setText(String.valueOf(option1));



        TextView option2_value = findViewById(R.id.textView_option2_value);
        TextView option2_key = findViewById(R.id.textView_option2_key);
        option2_key.setText(kspConfig.getPhones().get(kspConfig.DM_oneplus()).get("readable"));
        option2_value.setText(String.valueOf(option2));

        TextView option3_value = findViewById(R.id.textView_option3_value);
        TextView option3_key = findViewById(R.id.textView_option3_key);
        option3_key.setText(kspConfig.getPhones().get(kspConfig.DM_smartlock()).get("readable"));
        option3_value.setText(String.valueOf(option3));

        TextView option4_value = findViewById(R.id.textView_option4_value);
        TextView option4_key = findViewById(R.id.textView_option4_key);
        option4_key.setText(kspConfig.getPhones().get(kspConfig.DM_samsung()).get("readable"));
        option4_value.setText(String.valueOf(option4));






    }
}
