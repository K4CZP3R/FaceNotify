package k4czp3r.facenotify.misc;

import android.os.Build;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KspConfiguration {
    private static String TAG = KspConfiguration.class.getCanonicalName();
    KspPreferences kspPreferences = new KspPreferences();
    KspConfig kspConfig = new KspConfig();
    KspLog kspLog = new KspLog();



    public String getDeviceName(){
        String manufacturer = Build.MANUFACTURER.toUpperCase();
        String model = Build.MODEL.toUpperCase();
        kspLog.info(TAG,"Phone manu: "+manufacturer,false);
        kspLog.info(TAG, "Phone model: "+model,false);
        if(model.startsWith(manufacturer)){
            return model.replace(' ','_');
        }
        return (manufacturer+"."+model).replace(' ','_');
    }

    public boolean phoneModelCheck(){
        if(getDeviceName().contains("SAMSUNG")) return true;
        if(getDeviceName().contains("ONEPLUS")) return true;
        if(getDeviceName().contains("LG")) return true;
        if(isOOS()) return true;
        if(isOmni()) return true;
        if(isMIUI()) return true;

        kspLog.warn(TAG, "This device is probably not supported",false);
        return false;
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
        else if(getDeviceName().contains("LG")) detectionMode = kspConfig.DM_lg();
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
        return kspConfig.getPhones().get(dDm).get("detectLine_v2"); //TODO: CHANGE
    }

    public String getDetectionUnlockValid(){
        String dDm = kspPreferences.getDetectionMode();
        return kspConfig.getPhones().get(dDm).get("valid");
    }


    public boolean convertTimeNeeded(String logcatLine){
        for(int i=0; i<logcatLine.length(); i++){
            char ch = logcatLine.charAt(i);
            if(ch >= 0x0660 && ch <= 0x0669){
                return true;
            }
            else if(ch >= 0x06f0 && ch <= 0x06F9){
                return true;
            }
        }
        return false;
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
    public long getLogcatTimeInMs(String logcatLine){
        if(convertTimeNeeded(logcatLine)){
            logcatLine = convertLogcatTimeIfNeeded(logcatLine);
        }

        Pattern pattern = Pattern.compile("\\d+-\\d+\\s+(\\d+):(\\d+):(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(logcatLine);

        if(matcher.find()){
            return Long.parseLong(matcher.group(1)) * 3600000L
                    + Long.parseLong(matcher.group(2)) * 60000
                    + Long.parseLong(matcher.group(3)) * 1000
                    + Long.parseLong(matcher.group(4));
        }
        else{
            throw new IllegalArgumentException("Invalid format '"+logcatLine+"'");
        }

    }

}
