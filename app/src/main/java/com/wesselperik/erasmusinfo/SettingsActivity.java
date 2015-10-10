package com.wesselperik.erasmusinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

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
            final Context context = getActivity().getApplication().getApplicationContext();

            /*android.app.ActionBar ab = getActivity().getActionBar();
            ab.setTitle("Instellingen");
            ab.setSubtitle("");*/

            addPreferencesFromResource(R.xml.preferences);

            final Preference ratePreference = findPreference("info_rate");
            ratePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
                    startActivity(intent);
                    return false;
                }
            });

            final Preference themepreference = findPreference("settings_theme");
            themepreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    restartMain(getActivity());
                    return true;
                }
            });

            final Preference changelogPreference = findPreference("info_changelog");
            changelogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    ChangeLogDialog changelogDialog = new ChangeLogDialog(getActivity());
                    changelogDialog.show();
                    return false;
                }
            });

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
    }


    static void restartMain(Activity activity){
        activity.startActivity(new Intent(activity, MainActivity.class));
    }
}
