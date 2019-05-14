package k4czp3r.facenotify.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import k4czp3r.facenotify.AppCompatPreferenceActivity;
import k4czp3r.facenotify.FaceNotifyApp;
import k4czp3r.facenotify.R;
import k4czp3r.facenotify.misc.KspConfig;
import k4czp3r.facenotify.misc.KspConfiguration;
import k4czp3r.facenotify.misc.KspLog;
import k4czp3r.facenotify.misc.KspPreferences;
import k4czp3r.facenotify.service_facedetection.KspFaceDetectionFunctions;
import k4czp3r.facenotify.service_facedetection.KspFaceDetectionLogcat;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || FrPreferenceFragment.class.getName().equals(fragmentName)
                || AppLoggerPreferenceFragment.class.getName().equals(fragmentName)
                || ToolsPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));

            //time hide TODO: Time option
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_ignore_fn_start__key)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_ignore_fn_end__key)));

            KspConfig kspConfig = new KspConfig();
            Preference current_device = findPreference(getString(R.string.pref_current_device__key));
            KspConfiguration kspConfiguration = new KspConfiguration();
            //current_device.setSummary(kspConfig.getPhoneNames().getOrDefault(kspConfiguration.getDeviceName(),"null"));
            current_device.setSummary(kspConfiguration.getDeviceName());

            Preference app_version = findPreference(getString(R.string.pref_app_version__key));
            try{
                String version = FaceNotifyApp.getAppContext().getPackageManager().getPackageInfo(FaceNotifyApp.getAppContext().getPackageName(),0).versionName;
                app_version.setSummary(version);
            }
            catch (Exception ex){
                app_version.setSummary(ex.getLocalizedMessage());

            }



            Preference device_supported = findPreference(getString(R.string.pref_device_supported__key));
            boolean isDeviceSupported = kspConfiguration.phoneModelCheck();
            String toSet;
            if(isDeviceSupported) toSet = getString(R.string.pref_device_supported__option_supported);
            else toSet = getString(R.string.pref_device_supported__option_not_supported);
            device_supported.setSummary(toSet);

            Preference ignore_battopt = findPreference(getString(R.string.pref_ignore_battopt__key));
            ignore_battopt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getContext(), "Disable it for FaceNotify", Toast.LENGTH_LONG).show();
                    Intent intent_Ps = new Intent();
                    intent_Ps.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivity(intent_Ps);
                    return true;
                }
            });
            /*Preference send_feedback = findPreference(getString(R.string.pref_send_feedback__key));
            send_feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getContext(), "Disabled!", Toast.LENGTH_SHORT).show();
                    //kspAppConfiguration.KspSendFeedback(getActivity());
                    return true;
                }
            });*/










        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            Log.i("k4czp3r","Joe: "+String.valueOf(id));
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }




    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ToolsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_tools);
            setHasOptionsMenu(true);

            Preference run_tester = findPreference(getString(R.string.pref_run_tester__key));
            run_tester.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(FaceNotifyApp.getAppContext(), CompCheck.class));
                    return true;
                }
            });

            KspFaceDetectionLogcat kspFaceDetectionLogcat = new KspFaceDetectionLogcat();
            Preference fix_logcat = findPreference(getString(R.string.pref_fix_logcat__key));
            fix_logcat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    kspFaceDetectionLogcat.clearLogs();
                    Snackbar.make(getView(), "Magically fixed it! (I hope it)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return true;
                }
            });

            KspFaceDetectionFunctions kspFaceDetectionFunctions = new KspFaceDetectionFunctions();
            Preference restore_default_settings = findPreference(getString(R.string.pref_restore_defaults__key));
            restore_default_settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    kspFaceDetectionFunctions.setNotificationVisibilityToDefault();
                    Snackbar.make(getView(), "Restored to default!", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FrPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_face_recognition);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_fr_delay_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_fr_hide_type_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_fr_start_at_boot)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_fr_detect_mode__key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_fr_anim_type__key)));



        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AppLoggerPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_app_logger);
            setHasOptionsMenu(true);


            Preference wipe_log_file = findPreference(getString(R.string.pref_wipe_log_file__key));
            wipe_log_file.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    KspLog kspLog = new KspLog();
                    kspLog.wipeLogFile();
                    return false;
                }
            });

            Preference log_to_file = findPreference(getString(R.string.pref_log_to_file__key));
            log_to_file.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    KspPreferences kspPreferences = new KspPreferences();
                    Snackbar.make(getView(), "Changed to: "+String.valueOf(kspPreferences.preferenceReadBoolean( getString(R.string.pref_log_to_file__key))),Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    if(kspPreferences.preferenceReadBoolean(getString(R.string.pref_log_to_file__key))){
                        if(ContextCompat.checkSelfPermission(FaceNotifyApp.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            kspPreferences.preferenceSaveBoolean(getString(R.string.pref_log_to_file__key),false);
                            Snackbar.make(getView(), "You need to grant file read/write permission!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                        }
                    }
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
