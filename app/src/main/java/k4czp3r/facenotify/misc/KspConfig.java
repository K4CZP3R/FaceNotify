package k4czp3r.facenotify.misc;

import java.util.HashMap;
import java.util.Map;

public class KspConfig {
    private static final Map<String, String> oneplus = new HashMap<String, String>();
    private static final Map<String, String> smartlock = new HashMap<String, String>();
    private static final Map<String, String> miui = new HashMap<String, String>();
    private static final Map<String, String> samsung = new HashMap<>();

    private static final Map<String, Map<String, String>> phones = new HashMap<String, Map<String, String>>();


    private static final Map<String, String> phones_name = new HashMap<>();

    public KspConfig(){
        oneplus.put("detectLine","FacelockTrust");
        oneplus.put("readable", "OnePlus");
        oneplus.put("valid",",true");

        smartlock.put("detectLine","Unlock state:");
        smartlock.put("readable","AOSP (Smartlock)");
        smartlock.put("valid","UNLOCK_STATE_ALLOW");

        miui.put("detectLine","FaceAuthManager: compare");
        miui.put("readable","Xiaomi (MIUI)");
        miui.put("valid","return 201");


        samsung.put("detectLine","KeyguardFace: onAuthentication");
        samsung.put("valid","Succeeded()");
        samsung.put("readable","Samsung (S9)");

        phones.put("oneplus", oneplus);
        phones.put("smartlock", smartlock);
        phones.put("miui", miui);
        phones.put("samsung",samsung);

        phones_name.put("ONEPLUS_A6003", "OnePlus 6");
        phones_name.put("ONEPLUS_A6013", "OnePlus 6");
        phones_name.put("ONEPLUS_A5010", "OnePlus 5T");
        phones_name.put("ONEPLUS_A5000", "OnePlus 5");
        phones_name.put("XIAOMI_REDMI_NOTE_7", "Xiaomi Redmi Note 7");
        phones_name.put("SAMSUNG_SM-G965F", "Samsung Galaxy S9+");

    }
    public Map<String, Map<String, String>> getPhones(){
        return phones;
    }
    public Map<String, String> getPhoneNames(){return phones_name;}
    public String DM_miui(){
        return "miui";
    }
    public String DM_smartlock(){
        return "smartlock";
    }
    public String DM_oneplus(){
        return "oneplus";
    }
    public String DM_samsung() {return "samsung";}
}
