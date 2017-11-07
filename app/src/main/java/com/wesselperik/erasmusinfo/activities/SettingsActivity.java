package com.wesselperik.erasmusinfo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;

import com.wesselperik.erasmusinfo.MainActivity;
import com.wesselperik.erasmusinfo.R;

/**
 * Created by Wessel on 3-9-2015.
 */
public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        public static PrefsFragment newInstance() {
            PrefsFragment fragment = new PrefsFragment();
            return fragment;
        }

        public PrefsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            final Preference schoolPreference = findPreference("settings_schoolname");
            schoolPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    restartMain(getActivity());
                    return true;
                }
            });

            final Preference notificationsPreference = findPreference("settings_notifications");
            notificationsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick (Preference preference)
                {
                    //Intent i = new Intent(getActivity().getApplicationContext(), NotificationsActivity.class);
                    //startActivity(i);

                    return true;
                }
            });

            final Preference versionInfo = findPreference("info_version");
            try {
                versionInfo.setSummary(appVersion());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setClickable(true);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        public String appVersion() throws PackageManager.NameNotFoundException {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = "v" + pInfo.versionName;
            return version;
        }

    }


    static void restartMain(Activity activity){
        activity.startActivity(new Intent(activity, MainActivity.class));
    }
}
