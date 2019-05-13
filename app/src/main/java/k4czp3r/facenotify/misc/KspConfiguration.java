package k4czp3r.facenotify.misc;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.KspAppConfiguration;
import k4czp3r.facenotify.R;

public class KspConfiguration {
    private static String TAG = KspConfiguration.class.getCanonicalName();
    KspPreferences kspPreferences = new KspPreferences();
    KspConfig kspConfig = new KspConfig();
    KspLog kspLog = new KspLog();



    public String getDeviceName(){
        String manufacturer = getProp("ro.product.vendor.manufacturer");
        String model = getProp("ro.product.vendor.model");
        if(model.startsWith(manufacturer)){
            return model.replace(' ','_');
        }
        return (manufacturer+"."+model).replace(' ','_');
    }

    public boolean phoneModelCheck(){

        if(!kspConfig.getPhoneNames().getOrDefault(getDeviceName(), "null").equals("null")){
            kspLog.info(TAG, "Device is supported",true);
            return  true;
        }
        else{
            kspLog.warn(TAG, "Device is probably not supported!",true);
            return false;
        }
    }
    private boolean isOmni(){
        return checkGetProp("OmniROM");
    }
    private boolean isMIUI(){
        return  checkGetProp("miui");
    }
    private boolean isOOS(){
        return  checkGetProp("Oxygen");
    }
    private String getProp(String value){
        try{
            Process getProp = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","getprop "+value});
            BufferedReader getPropReader = new BufferedReader(new InputStreamReader(getProp.getInputStream()));

            String result = getPropReader.readLine();
            getPropReader.close();
            return result;
        } catch (Exception ex){
            kspLog.error(TAG, ex.getMessage(), true);
            ex.printStackTrace();
        }
        return "";
    }
    private boolean checkGetProp(String valueToCheck){
        try{
            Process getProp = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","getprop | grep "+valueToCheck+" | wc -l"});
            BufferedReader getPropReader = new BufferedReader(new InputStreamReader(getProp.getInputStream()));

            int x_in_props = Integer.parseInt(getPropReader.readLine());
            getPropReader.close();
            return x_in_props > 0;
        }
        catch (Exception ex){
            kspLog.error(TAG, ex.getMessage(),true);
            ex.printStackTrace();
        }
        return false;
    }



    public void _onlyinit_determineDetectionMode(){
        String detectionMode;


        if(isOOS()) detectionMode = kspConfig.DM_oneplus();
        else if(isMIUI()) detectionMode = kspConfig.DM_miui();
        else if(isOmni()) detectionMode = kspConfig.DM_smartlock();
        else if(getDeviceName().contains("ONEPLUS")) detectionMode = kspConfig.DM_oneplus();
        else if(getDeviceName().contains("XIAOMI")) detectionMode = kspConfig.DM_miui();
        else if(getDeviceName().contains("SAMSUNG")) detectionMode = kspConfig.DM_samsung_face();
        else detectionMode = kspConfig.DM_smartlock();

        kspPreferences.setDetectionMode(detectionMode);

    }
    public String getDetectionTagFilter(){
        String dDm= kspPreferences.getDetectionMode();
        //TODO: Implement if needed
        return null;

    }

    public String getDetectionLogcatLine(){
        String dDm = kspPreferences.getDetectionMode();
        return kspConfig.getPhones().get(dDm).get("detectLine");
    }

    public String getDetectionUnlockValid(){
        String dDm = kspPreferences.getDetectionMode();
        return kspConfig.getPhones().get(dDm).get("valid");
    }

    //RegexFunctions
    public int getLocalTimeValue(String timeString){
        //kspLog.info(TAG, "Converting local time to readable (english) one...",false);
        timeString = convertLogcatTimeIfNeeded(timeString);

        Pattern pattern = Pattern.compile("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$");
        Matcher matcher = pattern.matcher(timeString);

        while(matcher.find()){
            int detectHour = Integer.parseInt(matcher.group(1));
            int detectMinute = Integer.parseInt(matcher.group(2));
            int detectSecond = Integer.parseInt(matcher.group(3));
            return (100*detectHour)+(10*detectMinute)+(1*detectSecond);
        }
        return 0;
    }


    public String convertLogcatTimeIfNeeded(String logcatLine){
        char[] chars = new char[logcatLine.length()];
        for(int i=0; i<logcatLine.length(); i++){
            char ch = logcatLine.charAt(i);
            if(ch >= 0x0660 && ch <= 0x0669){ //arabic 0-9 numbers
                ch -= 0x0660 - '0';
            }
            else if(ch >= 0x06f0 && ch <= 0x06F9){
                ch -= 0x06f0 - '0';
            }
            chars[i] = ch;
        }
        return new String(chars);
    }
    public int getLogcatTimeValue(String logcatLine){
        //kspLog.info(TAG, "Converting logcat time to readable (english) one...",false);
        logcatLine = convertLogcatTimeIfNeeded(logcatLine);

        Pattern pattern = Pattern.compile("\\d+-\\d+\\s+(\\d+):(\\d+):(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(logcatLine);

        while(matcher.find()){
            int logcatHour=Integer.parseInt(matcher.group(1));
            int logcatMinute=Integer.parseInt(matcher.group(2));
            int logcatSecond=Integer.parseInt(matcher.group(3));
            return (100*logcatHour)+(10*logcatMinute)+(1*logcatSecond);
        }
        return 0;

    }

}
