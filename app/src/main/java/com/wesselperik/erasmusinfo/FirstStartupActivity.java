package com.wesselperik.erasmusinfo;

import android.app.Activity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class FirstStartupActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_startup);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void CloseOverlay(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                finish();
                break;
        }
    }

    public static class PrefsFragment extends PreferenceFragment {

        public static PrefsFragment newInstance() {
            PrefsFragment fragment = new PrefsFragment();
            return fragment;
        }

        public PrefsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            addPreferencesFromResource(R.xml.first_startup_preferences);

            return view;
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
}
