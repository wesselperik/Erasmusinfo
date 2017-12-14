package com.wesselperik.erasmusinfo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.services.WearService;
import com.wesselperik.erasmusinfo.views.TextViewBold;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wessel on 3-9-2015.
 */
public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingtoolbar) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.toolbar_content_title) TextViewBold toolbarContentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                toolbarContentTitle.setAlpha(1.0f - Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
            }
        });

        toolbarLayout.setTitle(" ");
        toolbarContentTitle.setText("instellingen");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PrefsFragment())
                .commit();
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {

        public PrefsFragment() {
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

            addPreferencesFromResource(R.xml.preferences);

            final Preference schoolPreference = findPreference("settings_schoolname");
            schoolPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    restartMain(getActivity());
                    return true;
                }
            });

            final Preference wearPreference = findPreference("settings_wear_service");
            wearPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((boolean) newValue) {
                        getActivity().startService(new Intent(getActivity(), WearService.class));
                    } else {
                        getActivity().stopService(new Intent(getActivity(), WearService.class));
                    }
                    return true;
                }
            });

            /*final Preference notificationsPreference = findPreference("settings_notifications");
            notificationsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick (Preference preference) {
                    //Intent i = new Intent(getActivity().getApplicationContext(), NotificationsActivity.class);
                    //startActivity(i);

                    return true;
                }
            });*/

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
            String version = "v" + pInfo.versionName + " (build " + pInfo.versionCode + ")";
            return version;
        }

    }

    static void restartMain(Activity activity){
        activity.startActivity(new Intent(activity, MainActivity.class));
    }
}
