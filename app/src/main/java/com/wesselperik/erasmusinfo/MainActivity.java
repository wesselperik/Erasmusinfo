package com.wesselperik.erasmusinfo;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private static String havovwo_url = "http://api.erasmusinfo.nl/roosterwijzigingen/havo-vwo/";
    private static String vmbo_url = "http://api.erasmusinfo.nl/roosterwijzigingen/vmbo/";
    private static String pro_url = "http://api.erasmusinfo.nl/roosterwijzigingen/pro/";

    // JSON Node names
    private static final String TAG_MEDEDELINGEN = "mededelingen";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "titel";
    private static final String TAG_TEXT = "mededeling";

    public MainActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getIntent().hasExtra("bundle") && savedInstanceState==null){
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String theme = prefs.getString("settings_theme", "blue");

        switch(theme)
        {
            case "blue":
                setTheme(R.style.BlueTheme);
                break;
            case "green":
                setTheme(R.style.GreenTheme);
                break;
            case "red":
                setTheme(R.style.RedTheme);
                break;

            default:
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        FragmentManager fragmentManager = getSupportFragmentManager();

        Boolean startup = prefs.getBoolean("settings_startup", false);
        String schoolName = prefs.getString("settings_schoolname", "havovwo");

        if(startup == true){
            switch(schoolName)
            {
                case "havovwo":
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("HavovwoFragment")
                            .replace(R.id.container, HavovwoFragment.newInstance())
                            .commit();
                    break;
                case "vmbo":
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("VmboFragment")
                            .replace(R.id.container, VmboFragment.newInstance())
                            .commit();
                    break;
                case "pro":
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("ProFragment")
                            .replace(R.id.container, ProFragment.newInstance())
                            .commit();
                    break;

                default:
            }
        }

        if(!isNetworkAvailable(this)) {
            Toast.makeText(this,"Geen internetverbinding", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (position == 0) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .addToBackStack("HomeFragment")
                    .replace(R.id.container, HomeFragment.newInstance())
                    .commit();
        } else if (position == 1) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .addToBackStack("HavovwoFragment")
                    .replace(R.id.container, HavovwoFragment.newInstance())
                    .commit();
        } else if (position == 2) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .addToBackStack("VmboFragment")
                    .replace(R.id.container, VmboFragment.newInstance())
                    .commit();
        } else if (position == 3) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .addToBackStack("ProFragment")
                    .replace(R.id.container, ProFragment.newInstance())
                    .commit();
        } else if (position == 4) {
            fragmentManager.popBackStack();
            getFragmentManager().beginTransaction()
                    .addToBackStack("PrefsFragment")
                    .replace(R.id.container, new PrefsFragment()).commit();
        }

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.action_settings:
                fragmentManager.popBackStack();
                getFragmentManager().beginTransaction()
                        .addToBackStack("PrefsFragment")
                        .replace(R.id.container, new PrefsFragment()).commit();
                return true;

            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Download de Erasmusinfo Android app via de Play Store: https://play.google.com/store/apps/details?id=com.wesselperik.erasmusinfo";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Erasmusinfo app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Deel via..."));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToSchool(View view) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        FragmentManager fragmentManager = getSupportFragmentManager();

        String schoolName = prefs.getString("settings_schoolname", "havovwo");

        switch(schoolName)
        {
            case "havovwo":
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .addToBackStack("HavovwoFragment")
                        .replace(R.id.container, HavovwoFragment.newInstance())
                        .commit();
                break;
            case "vmbo":
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .addToBackStack("VmboFragment")
                        .replace(R.id.container, VmboFragment.newInstance())
                        .commit();
                break;
            case "pro":
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .addToBackStack("ProFragment")
                        .replace(R.id.container, ProFragment.newInstance())
                        .commit();
                break;

            default:
        }
    }


    public static Handler UIHandler = new Handler(Looper.getMainLooper());

    /* ------------------------------------------------- */
    /* ------------------- Fragments ------------------- */
    /* ------------------------------------------------- */


    public static class HomeFragment extends Fragment {

        public static HomeFragment newInstance() {
            HomeFragment fragment = new HomeFragment();
            return fragment;
        }

        public HomeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container,
                    false);
            TextView Text = (TextView) rootView.findViewById(R.id.hometext);
            Text.setText("Home");

            ((MainActivity) getActivity()).setActionBarTitle("Home");

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setBackgroundColor(Color.WHITE);
            getView().setClickable(true);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //((MainActivity) activity).onSectionAttached(1);
        }
    }

    public static class HavovwoFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray mededelingen = null;
        ArrayList<HashMap<String, CharSequence>> mededelingList = new ArrayList<HashMap<String, CharSequence>>();

        public static HavovwoFragment newInstance() {
            HavovwoFragment fragment = new HavovwoFragment();
            return fragment;
        }

        public HavovwoFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_havovwo, container, false);
                listView = (ListView) view.findViewById(R.id.listView);
                new GetMededelingen().execute();
                view.getBackground().setAlpha(255);

                ((MainActivity) getActivity()).setActionBarTitle("HAVO/VWO");

                mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Refreshing data on server
                        new GetMededelingen().execute();
                        mededelingList.clear();
                        Log.i("SwipeRefresh", "Refreshing data...");
                    }
                });

                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "HavovwoFragment.onPause() has been called.");
            }
        }

        private class GetMededelingen extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // ServiceHandler
                ServiceHandler sh = new ServiceHandler();

                // Request maken naar server
                String jsonStr = sh.makeServiceCall(havovwo_url, ServiceHandler.GET);

                Log.d("JSON Data: ", jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON array ophalen
                        mededelingen = jsonObj.getJSONArray(TAG_MEDEDELINGEN);

                        for (int i = 0; i < mededelingen.length(); i++) {
                            JSONObject c = mededelingen.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String title = c.getString(TAG_TITLE);
                            CharSequence text = Html.fromHtml(c.getString(TAG_TEXT));

                            HashMap<String, CharSequence> mededeling = new HashMap<String, CharSequence>();

                            mededeling.put(TAG_ID, id);
                            mededeling.put(TAG_TITLE, title);
                            mededeling.put(TAG_TEXT, text);

                            // Mededeling aan list toevoegen
                            mededelingList.add(mededeling);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Kon geen data van de Erasmusinfo API ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {

                        ListAdapter adapter = null;

                        if(getActivity() != null){
                            adapter = new SimpleAdapter(getActivity(), mededelingList, R.layout.list_item, new String[]{TAG_TITLE, TAG_TEXT}, new int[]{R.id.title, R.id.text});
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new HavovwoFragment()).commit();
                        }

                        // Dismiss progress-spinner
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        // Adapter

                        listView.setAdapter(adapter);
                    }
                });
            }

        }


    }

    public static class VmboFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray mededelingen = null;
        ArrayList<HashMap<String, CharSequence>> mededelingList = new ArrayList<HashMap<String, CharSequence>>();

        public static VmboFragment newInstance() {
            VmboFragment fragment = new VmboFragment();
            return fragment;
        }

        public VmboFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_vmbo, container, false);
                listView = (ListView) view.findViewById(R.id.listView);
                new GetMededelingen().execute();
                view.getBackground().setAlpha(255);

                ((MainActivity) getActivity()).setActionBarTitle("VMBO");

                mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Refreshing data on server
                        new GetMededelingen().execute();
                        mededelingList.clear();
                        Log.i("SwipeRefresh", "Refreshing data...");
                    }
                });

                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "VmboFragment.onPause() has been called.");
            }
        }

        private class GetMededelingen extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // ServiceHandler
                ServiceHandler sh = new ServiceHandler();

                // Request maken naar server
                String jsonStr = sh.makeServiceCall(vmbo_url, ServiceHandler.GET);

                Log.d("JSON Data: ", jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON array ophalen
                        mededelingen = jsonObj.getJSONArray(TAG_MEDEDELINGEN);

                        for (int i = 0; i < mededelingen.length(); i++) {
                            JSONObject c = mededelingen.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String title = c.getString(TAG_TITLE);
                            CharSequence text = Html.fromHtml(c.getString(TAG_TEXT));

                            HashMap<String, CharSequence> mededeling = new HashMap<String, CharSequence>();

                            mededeling.put(TAG_ID, id);
                            mededeling.put(TAG_TITLE, title);
                            mededeling.put(TAG_TEXT, text);

                            // Mededeling aan list toevoegen
                            mededelingList.add(mededeling);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Kon geen data van de Erasmusinfo API ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {

                        ListAdapter adapter = null;

                        if(getActivity() != null){
                            adapter = new SimpleAdapter(getActivity(), mededelingList, R.layout.list_item, new String[]{TAG_TITLE, TAG_TEXT}, new int[]{R.id.title, R.id.text});
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new VmboFragment()).commit();
                        }

                        // Dismiss progress-spinner
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        // Adapter

                        listView.setAdapter(adapter);
                    }
                });
            }

        }


    }

    public static class ProFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray mededelingen = null;
        ArrayList<HashMap<String, CharSequence>> mededelingList = new ArrayList<HashMap<String, CharSequence>>();

        public static ProFragment newInstance() {
            ProFragment fragment = new ProFragment();
            return fragment;
        }

        public ProFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_pro, container, false);
                listView = (ListView) view.findViewById(R.id.listView);
                new GetMededelingen().execute();
                view.getBackground().setAlpha(255);

                ((MainActivity) getActivity()).setActionBarTitle("PrO");

                mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Refreshing data on server
                        new GetMededelingen().execute();
                        mededelingList.clear();
                        Log.i("SwipeRefresh", "Refreshing data...");
                    }
                });

                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "ProFragment.onPause() has been called.");
            }
        }

        private class GetMededelingen extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // ServiceHandler
                ServiceHandler sh = new ServiceHandler();

                // Request maken naar server
                String jsonStr = sh.makeServiceCall(pro_url, ServiceHandler.GET);

                Log.d("JSON Data: ", jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON array ophalen
                        mededelingen = jsonObj.getJSONArray(TAG_MEDEDELINGEN);

                        for (int i = 0; i < mededelingen.length(); i++) {
                            JSONObject c = mededelingen.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String title = c.getString(TAG_TITLE);
                            CharSequence text = Html.fromHtml(c.getString(TAG_TEXT));

                            HashMap<String, CharSequence> mededeling = new HashMap<String, CharSequence>();

                            mededeling.put(TAG_ID, id);
                            mededeling.put(TAG_TITLE, title);
                            mededeling.put(TAG_TEXT, text);

                            // Mededeling aan list toevoegen
                            mededelingList.add(mededeling);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Kon geen data van de Erasmusinfo API ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {

                        ListAdapter adapter = null;

                        if(getActivity() != null){
                            adapter = new SimpleAdapter(getActivity(), mededelingList, R.layout.list_item, new String[]{TAG_TITLE, TAG_TEXT}, new int[]{R.id.title, R.id.text});
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new ProFragment()).commit();
                        }

                        // Dismiss progress-spinner
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        // Adapter

                        listView.setAdapter(adapter);
                    }
                });
            }

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
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Context context = getActivity().getApplication().getApplicationContext();

            ((MainActivity) getActivity()).setActionBarTitle("Instellingen");

            addPreferencesFromResource(R.xml.preferences);

            Preference donatePreference = findPreference("info_donate");
            donatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=4V8MQJJN62BWA"));
                    startActivity(intent);
                    return false;
                }
            });

            final Preference ratePreference = findPreference("info_rate");
            ratePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
                    startActivity(intent);
                    return false;
                }
            });

            final Preference contributePreference = findPreference("info_contribute");
            contributePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/wesselperik/erasmusinfo"));
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

            getView().setBackgroundColor(Color.WHITE);
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