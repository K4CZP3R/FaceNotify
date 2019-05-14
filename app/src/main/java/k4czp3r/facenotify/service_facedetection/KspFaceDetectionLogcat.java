package k4czp3r.facenotify.service_facedetection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspConfiguration;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.misc.KspPreferences;

public class KspFaceDetectionLogcat {

    private static String TAG = KspFaceDetectionLogcat.class.getCanonicalName();
    private KspConfiguration kspConfiguration = new KspConfiguration();
    private KspLog kspLog = new KspLog();
    private KspPreferences kspPreferences = new KspPreferences();

    public void clearLogs(){
       try {
           Process process = new ProcessBuilder().command("/bin/sh -c logcat -c").redirectErrorStream(true).start();
       }
       catch (Exception ex){
           kspLog.error(TAG, ex.getMessage(),true);
           ex.printStackTrace();
       }
    }
    public boolean foundInLogs(String includeString){

        String[] logcatCommand = new String[]{"/bin/sh","-c","logcat -d -T 2048 -e \""+includeString+"\""};
        try {
            Process processLogcat = Runtime.getRuntime().exec(logcatCommand);
            BufferedReader readerLogcat = new BufferedReader(new InputStreamReader(processLogcat.getInputStream()));
            String iterLine;

            int count = 0;
            while ((iterLine = readerLogcat.readLine()) != null) {
                if(iterLine.contains(includeString)) {
                    count += 1;
                }
            }
            return count > 0;
        }
        catch (Exception ex){
            kspLog.error(TAG, ex.getMessage(),true);
        }
        return false;
    }


    public List<String> readLogs(String includeStringLogcat, String filterTag, long detectStartTimeMs){
        kspLog.info(TAG, "Searching for: '"+includeStringLogcat+"'",false);
        String[] logcatCommand = new String[]{"/bin/sh","-c","logcat -d -e \""+includeStringLogcat+"\""};


        //TODO: Implement filterTag when it will be needed
        List<String> listLogcatMirror = new ArrayList<>();
        int count=0;
        boolean logsAfterDetectTime = false;
        try{
            Process processLogcat = Runtime.getRuntime().exec(logcatCommand);
            BufferedReader readerLogcat = new BufferedReader(new InputStreamReader(processLogcat.getInputStream()));

            String iterLine;
            while((iterLine= readerLogcat.readLine())!= null){
                count=count+1;
                if(iterLine.contains("beginning of")) continue;
                if(!logsAfterDetectTime && (kspConfiguration.getLogcatTimeInMs(iterLine) >= detectStartTimeMs)){
                    logsAfterDetectTime=true;
                }
                //allow only after detect hourminsec
                if(logsAfterDetectTime) {
                    kspLog.info(TAG, "Adding to logcat list: '" + iterLine + "'", false);
                    listLogcatMirror.add(iterLine);
                }
            }
            readerLogcat.close();

        }
        catch (IOException e){
            kspLog.error(TAG, "Error while reading logcat: "+e.getMessage(),true);
            e.printStackTrace();
        }
        return listLogcatMirror;

    }
}
