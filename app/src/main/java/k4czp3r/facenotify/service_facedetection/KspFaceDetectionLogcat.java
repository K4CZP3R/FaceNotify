package k4czp3r.facenotify.service_facedetection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import k4czp3r.facenotify.misc.KspConfiguration;
import k4czp3r.facenotify.misc.KspLog;

public class KspFaceDetectionLogcat {

    private static String TAG = KspFaceDetectionLogcat.class.getCanonicalName();
    private KspConfiguration kspConfiguration = new KspConfiguration();
    KspLog kspLog = new KspLog();

    public void clearLogs(){
       try {
           Process process = new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
       }
       catch (Exception ex){
           kspLog.error(TAG, ex.getMessage(),true);
           ex.printStackTrace();
       }
    }

    public List<String> readLogs(String includeStringLogcat, String filterTag, String detectStartTime){
        kspLog.info(TAG, "Searching for: "+includeStringLogcat,false);
        //TODO: Implement filterTag when it will be needed
        float debug_startTime = System.currentTimeMillis();

        List<String> listLogcatMirror = new ArrayList<>();
        int count=0;
        boolean logsAfterDetectTime = false;
        try{
            Process processLogcat = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","logcat -d -T 512"});
            BufferedReader readerLogcat = new BufferedReader(new InputStreamReader(processLogcat.getInputStream()));

            String iterLine;

            while((iterLine= readerLogcat.readLine())!= null){
                count=count+1;
                if(!logsAfterDetectTime && (kspConfiguration.getLogcatTimeValue(iterLine) >= kspConfiguration.getLocalTimeValue(detectStartTime))){
                    logsAfterDetectTime=true;
                }

                //allow only after detect hourminsec
                if(logsAfterDetectTime) {
                    if (iterLine.contains(includeStringLogcat)) {
                        kspLog.info(TAG, "Adding to logcat list: '"+iterLine+"'",false);
                        listLogcatMirror.add(iterLine);
                    }
                }
            }
            readerLogcat.close();

        }
        catch (IOException e){
            kspLog.error(TAG, "Error while reading logcat: "+e.getMessage(),true);
            e.printStackTrace();
        }
        kspLog.info(TAG, String.format("Scanned %1$s lines",count),false);
        return listLogcatMirror;

    }
}
