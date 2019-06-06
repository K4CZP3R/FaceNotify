package k4czp3r.facenotify.misc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;

public class KspLog {
    private KspPreferences kspPreferences = new KspPreferences();
    private static String TAG = KspLog.class.getCanonicalName();

    private static<T> List<T> reverseList(List<T> list){
        List<T> reverse = new ArrayList<>(list);
        Collections.reverse(reverse);
        return reverse;
    }
    public void warn(String TAG, String content, boolean toLogcat){
        if(toLogcat) Log.w(TAG, content);
        appendLog(TAG, content);
    }
    public void info(String TAG, String content, boolean toLogcat){
        if(toLogcat) Log.i(TAG, content);
        appendLog(TAG, content);
    }
    public void error(String TAG, String content, boolean toLogcat){
        if(toLogcat) Log.e(TAG, content);
        appendLog(TAG, content);
    }
    public void debug(String TAG, String content, boolean toLogcat){
        if(toLogcat) Log.d(TAG, content);
        appendLog(TAG, content);
    }
    public void wipeLogFile(){
        if(permissionGranted()){
            File logFile = getLogFile();
            if(logFile == null){
                return;
            }
            try{
                logFile.delete();
            }
            catch (Exception ex){
                Log.e(TAG, ex.getMessage());
            }
        }
    }
    private File getLogFile(){
        if(permissionGranted() && saveToFile()){
            File logFile = new File(Environment.getExternalStorageDirectory(),"FaceNotify.log");
            if(!logFile.exists()){
                try{
                    boolean cnfResult = logFile.createNewFile();
                    info(TAG, "createNewFile():"+cnfResult,true);
                }
                catch (IOException e){
                    error(TAG, e.getMessage(),true);
                    return null;
                }
            }
            return logFile;
        }
        return null;
    }
    public List<String> readLog(){
        if(permissionGranted() && saveToFile()){
            List<String> listLog = new ArrayList<>();
            File logFile = getLogFile();

            if(logFile == null){
                return null;
            }

            try{
                BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
                String iterLine;
                while((iterLine = bufferedReader.readLine()) != null){
                    listLog.add(iterLine);
                }
                bufferedReader.close();
                return reverseList(listLog);
            }
            catch (Exception ex){
                Log.e(TAG, ex.getMessage());
            }
            return null;
        }
        return null;
    }

    private String getDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
    public void appendLog(String TAG, String text){
        if(permissionGranted() && saveToFile()) {
            String toLog = String.format("[%s]: <%s> %s", getDateString(), TAG,  text);
            File logFile = getLogFile();
            if(logFile == null){
                Log.e(TAG, "Can't write to log file!");
                return;
            }

            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(toLog);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean saveToFile(){
        return kspPreferences.preferenceReadBoolean(FaceNotifyApp.getAppContext().getString(R.string.pref_log_to_file__key));
    }
    private boolean permissionGranted(){
        if(ContextCompat.checkSelfPermission(FaceNotifyApp.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }
}
