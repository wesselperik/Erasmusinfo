package com.wesselperik.erasmusinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wesselperik.erasmusinfo.fragments.HavoVwo;
import com.wesselperik.erasmusinfo.fragments.Pro;
import com.wesselperik.erasmusinfo.fragments.Vmbo;

//public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
public class MainActivity extends ActionBarActivity {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ActionBarDrawerToggle mDrawerToggle;
    //private DrawerLayout mDrawerLayout;
    //private Toolbar mToolbar;
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

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    static NavigationView mNavigationView;
    FrameLayout mContentFrame;

    private static final String PREFERENCES_FILE = "erasmusinfo_settings";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;

    public MainActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getIntent().hasExtra("bundle") && savedInstanceState==null){
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        }



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*
        String theme = prefs.getString("settings_theme", "indigo");

        switch(theme)
        {
            case "indigo":
                setTheme(R.style.IndigoTheme);
                break;
            case "darkblue":
                setTheme(R.style.DarkBlueTheme);
                break;
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
        }*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        if(!isNetworkAvailable(this)) {
            Toast.makeText(this,"Geen internetverbinding", Toast.LENGTH_LONG).show();
            finish();
        }

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(MainActivity.this, FirstStartupActivity.class));
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
        setUpToolbar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(this, PREF_USER_LEARNED_DRAWER, "false"));

        /*
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }*/

        setUpNavDrawer();

        mNavigationView = (NavigationView) findViewById(R.id.drawer_view);
        // mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);

        final DrawerLayout finalMDrawerLayout = mDrawerLayout;
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                FragmentManager fragmentManager = getSupportFragmentManager();

                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction()
                                .addToBackStack("HomeFragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, HomeFragment.newInstance())
                                .commit();
                        //mCurrentSelectedPosition = 0;
                        finalMDrawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.navigation_item_2:
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction()
                                .addToBackStack("HavovwoFragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, new HavoVwo.MainFragment())
                                .commit();
                        //mCurrentSelectedPosition = 1;

                        finalMDrawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.navigation_item_3:
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction()
                                .addToBackStack("VmboFragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, new Vmbo.MainFragment())
                                .commit();
                        //mCurrentSelectedPosition = 2;
                        finalMDrawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.navigation_item_4:
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction()
                                .addToBackStack("ProFragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, new Pro.MainFragment())
                                .commit();
                        //mCurrentSelectedPosition = 3;
                        finalMDrawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.navigation_item_5:
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction()
                                .addToBackStack("RoosterMainFragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, RoosterMainFragment.newInstance())
                                .commit();
                        //mCurrentSelectedPosition = 4;
                        finalMDrawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.navigation_item_6:
                        /*fragmentManager.popBackStack();
                        getFragmentManager().beginTransaction()
                                .addToBackStack("PrefsFragment")
                                .replace(R.id.container, new PrefsFragment())
                                .commit(); */
                        //mCurrentSelectedPosition = 5;

                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        finalMDrawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    default:
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction()
                                .addToBackStack("HomeFragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, HomeFragment.newInstance())
                                .commit();
                        //mCurrentSelectedPosition = 0;
                        return true;
                }
            }
        });

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
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new HavoVwo.MainFragment())
                            .commit();
                    break;
                case "vmbo":
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("VmboFragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new Vmbo.MainFragment())
                            .commit();
                    break;
                case "pro":
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("ProFragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new Pro.MainFragment())
                            .commit();
                    break;

                default:
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        //Menu menu = mNavigationView.getMenu();
        //menu.getItem(mCurrentSelectedPosition).setChecked(true);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .addToBackStack("HomeFragment")
                .replace(R.id.container, HomeFragment.newInstance())
                .commit();

    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
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

        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        DrawerLayout homeDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case android.R.id.home:
                if(homeDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    homeDrawerLayout.closeDrawer(Gravity.LEFT);
                }else{
                    homeDrawerLayout.openDrawer(Gravity.LEFT);
                }
                return true;

            case R.id.action_settings:
                /*fragmentManager.popBackStack();
                getFragmentManager().beginTransaction()
                        .addToBackStack("PrefsFragment")
                        .replace(R.id.container, new PrefsFragment()).commit();*/
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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

        Menu menu = mNavigationView.getMenu();

        switch(schoolName)
        {
            case "havovwo":
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .addToBackStack("HavovwoFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, new HavoVwo.MainFragment())
                        .commit();
                menu.getItem(1).setChecked(true);
                break;
            case "vmbo":
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .addToBackStack("VmboFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, new Vmbo.MainFragment())
                        .commit();
                menu.getItem(2).setChecked(true);
                break;
            case "pro":
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .addToBackStack("ProFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, new Pro.MainFragment())
                        .commit();
                menu.getItem(3).setChecked(true);
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

            ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
            ab.setTitle("Home");
            ab.setSubtitle("");

            ImageView imageView1 = (ImageView) rootView.findViewById(R.id.schoolImage1);
            ImageView imageView2 = (ImageView) rootView.findViewById(R.id.schoolImage2);
            ImageView imageView3 = (ImageView) rootView.findViewById(R.id.schoolImage3);

            Picasso.with(getActivity().getApplicationContext()).load(R.drawable.havovwo).into(imageView1);
            Picasso.with(getActivity().getApplicationContext()).load(R.drawable.vmbo).into(imageView2);
            Picasso.with(getActivity().getApplicationContext()).load(R.drawable.pro).into(imageView3);

            TextView textView1 = (TextView) rootView.findViewById(R.id.schoolNaam1);
            TextView textView2 = (TextView) rootView.findViewById(R.id.schoolNaam2);
            TextView textView3 = (TextView) rootView.findViewById(R.id.schoolNaam3);

            textView1.setText("HAVO/VWO");
            textView2.setText("VMBO");
            textView3.setText("PrO");

            CardView cardView1 = (CardView) rootView.findViewById(R.id.card_view1);
            CardView cardView2 = (CardView) rootView.findViewById(R.id.card_view2);
            CardView cardView3 = (CardView) rootView.findViewById(R.id.card_view3);

            final Menu menu = mNavigationView.getMenu();

            cardView1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("HavovwoFragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new HavoVwo.MainFragment())
                            .commit();
                    menu.getItem(1).setChecked(true);
                }
            });

            cardView2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("VmboFragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new Vmbo.MainFragment())
                            .commit();
                    menu.getItem(2).setChecked(true);
                }
            });

            cardView3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction()
                            .addToBackStack("ProFragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, new Pro.MainFragment())
                            .commit();
                    menu.getItem(3).setChecked(true);
                }
            });


            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //((MainActivity) activity).onSectionAttached(1);
        }
    }

    // ------------------------------------------------------------ //
    // ------------------ Main Rooster Fragment ------------------- //
    // ------------------------------------------------------------ //

    public static class RoosterMainFragment extends Fragment {

        public static RoosterMainFragment newInstance() {
            RoosterMainFragment fragment = new RoosterMainFragment();
            return fragment;

        }

        public RoosterMainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_roostermain, container,
                    false);

            FragmentActivity activity = (FragmentActivity)rootView.getContext();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM");

            String today = df.format(c.getTime());

            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String maandag = df.format(c.getTime());

            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
            String dinsdag = df.format(c.getTime());

            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            String woensdag = df.format(c.getTime());

            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
            String donderdag = df.format(c.getTime());

            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            String vrijdag = df.format(c.getTime());

            // Initialize the ViewPager and set an adapter
            ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
            pager.setAdapter(new PagerAdapter(activity.getSupportFragmentManager()));
            pager.setOffscreenPageLimit(1);

            /*
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            Boolean scroll = prefs.getBoolean("magister_scroll", true);

            int scrolltab = 0;
            if (scroll == true){
                if (today == maandag){
                    scrolltab = 0;
                }else if (today == dinsdag){
                    scrolltab = 1;
                }else if (today == woensdag){
                    scrolltab = 2;
                }else if (today == donderdag){
                    scrolltab = 3;
                }else if (today == vrijdag){
                    scrolltab = 4;
                }
                pager.setCurrentItem(scrolltab);
            } */

            // Bind the tabs to the ViewPager
            SlidingTabLayout tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });
            tabs.setViewPager(pager);

            ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
            ab.setTitle("Rooster");
            ab.setSubtitle(maandag + " - " + vrijdag);
            ab.setElevation(0);

            return rootView;
        }

        @Override
        public void onPause() {
            super.onPause();
            getActivity().getSupportFragmentManager().beginTransaction().remove(RoosterMainFragment.newInstance()).commit();
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

    // ------------------------------------------------------------ //
    // -------------------- Rooster Fragments --------------------- //
    // ------------------------------------------------------------ //

    // JSON Node names
    private static final String TAG_ITEMS = "Items";
    private static final String TAG_LESSON_ID = "Id";
    private static final String TAG_LESSON_DESC = "Omschrijving";
    private static final String TAG_LESSON_LOCATION = "Lokatie";
    private static final String TAG_LESSON_ROOM = "Lokaal";
    private static final String TAG_LESSON_HOUR = "LesuurVan";
    private static final String TAG_LESSON_STATUS = "Status";
    private static final String TAG_LESSON_HOMEWORK = "Inhoud";
    private static final String TAG_LESSON_TIME = "Tijd";
    private static final String TAG_PERSON = "Persoon";
    private static final String TAG_USERID = "Id";
    private static final String TAG_ARRAY_VAKKEN = "Vakken";
    private static final String TAG_VAK_NAAM = "Naam";
    private static final String TAG_ARRAY_DOCENTEN = "Docenten";
    private static final String TAG_DOCENT_NAAM = "Naam";
    private static final String TAG_DOCENT_CODE = "Docentcode";

    public static class RoosterMaandagFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray items = null;
        JSONArray vakarray = null;
        JSONArray docentarray = null;
        JSONObject person = null;
        Integer userid = null;
        String lessonname = null;
        String docentname = null;
        String docentcode = null;
        ArrayList<HashMap<String, CharSequence>> itemList = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> vakken = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> docenten = new ArrayList<HashMap<String, CharSequence>>();

        // ArrayList<ArrayList<String>> vakList = new ArrayList<ArrayList<String>>();

        public static RoosterMaandagFragment newInstance(int dayId) {
            RoosterMaandagFragment fragment = new RoosterMaandagFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();

            args.putInt("dayId", dayId);

            fragment.setArguments(args);

            return fragment;
        }

        public RoosterMaandagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if(!isNetworkAvailable(getActivity().getApplicationContext())) {
                Toast.makeText(getActivity().getApplicationContext(),"Geen internetverbinding", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

          //  if (view == null) {

                view = inflater.inflate(R.layout.fragment_roostermaandag, container, false);
                listView = (ListView) view.findViewById(R.id.listView);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(isNetworkAvailable(getActivity().getApplicationContext())) {
                                itemList.clear();
                                new GetRooster().execute();
                            }
                        }
                    }).start();

                view.getBackground().setAlpha(255);

                /*
                ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
                ab.setTitle("Rooster");
                ab.setSubtitle("Vandaag"); */

                mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                            Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }

                        //Refreshing data on server
                        new GetRooster().execute();
                        itemList.clear();
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});
                                ((SimpleAdapter) adapter).notifyDataSetChanged();
                                listView.requestLayout();
                            }
                        });
                        Log.i("SwipeRefresh", "Refreshing data...");
                    }
                });

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                });
           /* } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }*/
            return view;
        }
/*
        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "RoosterFragment.onPause() has been called.");
            }
        }*/

        private class GetRooster extends AsyncTask<Void, Void, Void> {

            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0){
                final DefaultHttpClient mHttpclient = new DefaultHttpClient();
                CookieStore mCookieStore = new BasicCookieStore();
                final HttpContext mLocalContext = new BasicHttpContext();
                mLocalContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                String username = prefs.getString("account_username", null);
                String password = prefs.getString("account_password", null);


                // ----- POST login ----- //
                // ---------------------- //


                try {
                final HttpPost p = new HttpPost("https://osgerasmus.magister.net/api/sessies");
                p.addHeader("Content-Type", "application/json;charset=UTF-8");
                String r = null;
                    r = new JSONStringer()
                            .object()
                            .key("Gebruikersnaam")
                            .value("13719")
                            .key("Wachtwoord")
                            .value("GH7IU3")
                            .key("IngelogdBlijven")
                            .value(true)
                            .endObject()
                            .toString();

                    Log.d("String r", r);
                ByteArrayEntity b = new ByteArrayEntity(r.getBytes("UTF-8"));
                b.setContentType("application/json;charset=UTF-8");
                p.setEntity(b);
                //mHttpclient.execute(p);

                HttpResponse resp = null;
                resp = mHttpclient.execute(p, mLocalContext);

                HttpEntity entity = resp.getEntity();

                if (entity == null)
                    throw new IOException("entity == null");

                InputStream content = null;
                content = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                StringBuilder builder = new StringBuilder();
                String line;

                    while ((line = reader.readLine()) != null)
                    {
                        builder.append(line);
                    }
                    content.close();

                    Log.d("builder.toString()", builder.toString());

                // ----- User ID ophalen ----- //
                // --------------------------- //

                HttpResponse resp2 = null;
                resp2 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/account"), mLocalContext);
                HttpEntity entity2 = resp2.getEntity();
                if (entity2 == null) throw new IOException("entity2 == null");

                InputStream content2 = null;
                content2 = entity2.getContent();

                BufferedReader reader2 = new BufferedReader(new InputStreamReader(content2));
                StringBuilder builder2 = new StringBuilder();
                String line2;
                    while ((line2 = reader2.readLine()) != null)
                    {
                        builder2.append(line2);
                    }
                    content2.close();


                String jsonStr = builder2.toString();

                if (jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        Log.d("jsonStr", jsonStr);

                        // JSON Object ophalen
                        person = jsonObj.getJSONObject(TAG_PERSON);

                        userid = person.getInt(TAG_USERID);
                        Log.i("Magister", "User ID: " + userid);

                } else {
                    Log.e("Magister", "Kon geen User ID ophalen van Magister!");
                }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // ----- Rooster ophalen ----- //
                // --------------------------- //

                // userid = 11489;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String maandag = df.format(c.getTime());

                HttpResponse resp3 = null;
                try {
                    resp3 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/personen/" + userid + "/afspraken?van=" + maandag + "&tot=" + maandag), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity3 = resp3.getEntity();
                if (entity3 == null) try {
                    throw new IOException("entity3 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content3 = null;
                try {
                    content3 = entity3.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(content3));
                StringBuilder builder3 = new StringBuilder();
                String line3;
                try {
                    while ((line3 = reader3.readLine()) != null)
                    {
                        builder3.append(line3);
                    }
                    content3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr2 = builder3.toString();

                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);

                        // JSON array ophalen
                        items = jsonObj2.getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject c2 = items.getJSONObject(i);

                            String id = c2.getString(TAG_LESSON_ID);
                            String hour = c2.getString(TAG_LESSON_HOUR);
                            String title = c2.getString(TAG_LESSON_DESC);
                            final int status = c2.getInt(TAG_LESSON_STATUS);
                            CharSequence text = Html.fromHtml(c2.getString(TAG_LESSON_LOCATION));

                            String time = "";
                            String starttime = "";

                            if (hour == "1") {
                                time = "8:20 - 9:10";
                                starttime = "8:20";
                            }else if (hour == "2") {
                                time = "9:10 - 10:00";
                                starttime = "9:10";
                            }else if (hour == "3") {
                                time = "10:20 - 11:10";
                                starttime = "10:20";
                            }else if (hour == "4") {
                                time = "11:10 - 12:00";
                                starttime = "11:10";
                            }else if (hour == "5") {
                                time = "12:25 - 13:15";
                                starttime = "12:25";
                            }else if (hour == "6") {
                                time = "13:15 - 14:05";
                                starttime = "13:15";
                            }else if (hour == "7") {
                                time = "14:20 - 15:10";
                                starttime = "14:20";
                            }else if (hour == "8") {
                                time = "15:10 - 16:00";
                                starttime = "15:10";
                            }else if (hour == "9") {
                                time = "16:00 - 16:50";
                                starttime = "16:00";
                            }
                            final CharSequence homework = c2.getString(TAG_LESSON_HOMEWORK);

                            vakarray = c2.getJSONArray(TAG_ARRAY_VAKKEN);

                            for (int i2 = 0; i2 < vakarray.length(); i2++) {
                                HashMap<String, CharSequence> vak = new HashMap<String, CharSequence>();

                                JSONObject c3 = vakarray.getJSONObject(i2);

                                lessonname = c3.getString(TAG_VAK_NAAM);
                                vak.put(TAG_VAK_NAAM, lessonname);

                                vakken.add(vak);
                            }

                            docentarray = c2.getJSONArray(TAG_ARRAY_DOCENTEN);

                            for (int i2 = 0; i2 < docentarray.length(); i2++) {
                                HashMap<String, CharSequence> docent = new HashMap<String, CharSequence>();

                                JSONObject c3 = docentarray.getJSONObject(i2);

                                docentname = c3.getString(TAG_DOCENT_NAAM);
                                docentcode = c3.getString(TAG_DOCENT_CODE);

                                docent.put(TAG_DOCENT_NAAM, docentname);
                                docent.put(TAG_DOCENT_CODE, docentcode);

                                docenten.add(docent);
                            }


                            HashMap<String, CharSequence> item = new HashMap<String, CharSequence>();

                            item.put(TAG_LESSON_ID, id);
                            item.put(TAG_LESSON_HOUR, hour);

                            if (prefs.getBoolean("magister_simplify", false) == true){
                                item.put(TAG_LESSON_DESC, lessonname);
                            }else{
                                item.put(TAG_LESSON_DESC, title);
                            }

                            item.put(TAG_LESSON_LOCATION, "Lokaal " + text + " - " + starttime);
                            item.put(TAG_LESSON_ROOM, text);
                            item.put(TAG_LESSON_HOMEWORK, homework);
                            item.put(TAG_LESSON_TIME, time);

                            // Item aan list toevoegen
                            itemList.add(item);


                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((SimpleAdapter) adapter).notifyDataSetChanged();
                                    listView.requestLayout();

//                                    if (status == 5) {
//                                        Shape circle = (Shape) view.findViewById(R.id.hour_circle);
//                                        GradientDrawable drawable = (GradientDrawable) circle.getDrawable();
//                                        drawable.setColor(Color.parseColor(String.valueOf(R.color.red)));
//
//                                        TextView hourtext = (TextView) view.findViewById(R.id.hour);
//                                        ShapeDrawable shapeDrawable = (ShapeDrawable)hourtext.getBackground();
//                                        shapeDrawable.getPaint().setColor(getResources().getColor(R.color.red));
//                                    }


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                long id) {
                                            HashMap<String, CharSequence> les = itemList.get(position);
                                            HashMap<String, CharSequence> vak = vakken.get(position);
                                            HashMap<String, CharSequence> docent = docenten.get(position);
                                            String homework2;
                                            if (les.get(TAG_LESSON_HOMEWORK) != "null"){
                                                homework2 = "\n\nOpmerking/huiswerk: " + Html.fromHtml(String.valueOf(les.get(TAG_LESSON_HOMEWORK)));
                                            }else{
                                                homework2 = "";
                                            }


                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle(les.get(TAG_LESSON_HOUR) + "e uur - " + vak.get(TAG_VAK_NAAM))
                                                    .setMessage("Lokaal: " + les.get(TAG_LESSON_ROOM) + "\n\nTijd: " + les.get(TAG_LESSON_TIME) + "\n\nDocent: " + docent.get(TAG_DOCENT_NAAM) + " (" + docent.get(TAG_DOCENT_CODE) + ")" + homework2)
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).create().show();

                                        }
                                    });
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Magister", "Kon geen data van Magister ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {
                            if (isNetworkAvailable(getActivity().getApplicationContext())) {

                                // ListAdapter adapter = null;
/*
                        if(getActivity() != null){
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new RoosterFragment()).commit();
                        }*/

                                // Dismiss progress-spinner
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                                // Adapter

                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                    }
                });
            }

        }


    }

    public static class RoosterDinsdagFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray items = null;
        JSONArray vakarray = null;
        JSONArray docentarray = null;
        JSONObject person = null;
        Integer userid = null;
        String lessonname = null;
        String docentname = null;
        String docentcode = null;
        ArrayList<HashMap<String, CharSequence>> itemList = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> vakken = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> docenten = new ArrayList<HashMap<String, CharSequence>>();

        public static RoosterDinsdagFragment newInstance() {
            RoosterDinsdagFragment fragment = new RoosterDinsdagFragment();
            return fragment;
        }

        public RoosterDinsdagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

            //  if (view == null) {

            view = inflater.inflate(R.layout.fragment_roosterdinsdag, container, false);
            listView = (ListView) view.findViewById(R.id.listView);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isNetworkAvailable(getActivity().getApplicationContext())) {
                        itemList.clear();
                        new GetRooster().execute();
                    }
                }
            }).start();

            view.getBackground().setAlpha(255);

                /*
                ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
                ab.setTitle("Rooster");
                ab.setSubtitle("Vandaag"); */

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                        Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                    //Refreshing data on server
                    new GetRooster().execute();
                    itemList.clear();
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});
                            ((SimpleAdapter) adapter).notifyDataSetChanged();
                            listView.requestLayout();
                        }
                    });
                    Log.i("SwipeRefresh", "Refreshing data...");
                }
            });

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
           /* } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }*/
            return view;
        }
/*
        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "RoosterFragment.onPause() has been called.");
            }
        }*/

        private class GetRooster extends AsyncTask<Void, Void, Void> {

            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                final DefaultHttpClient mHttpclient = new DefaultHttpClient();
                CookieStore mCookieStore = new BasicCookieStore();
                final HttpContext mLocalContext = new BasicHttpContext();
                mLocalContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                String username = prefs.getString("account_username", null);
                String password = prefs.getString("account_password", null);


                // ----- POST login ----- //
                // ---------------------- //

                final HttpPost p = new HttpPost("https://osgerasmus.magister.net/api/sessies");
                p.addHeader("Content-Type", "application/json;charset=UTF-8");
                String r = null;
                try {
                    r = new JSONStringer()
                            .object()
                            .key("Gebruikersnaam")
                            .value(username)
                            .key("Wachtwoord")
                            .value(password)
                            .key("IngelogdBlijven")
                            .value(true)
                            .endObject()
                            .toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteArrayEntity b = null;
                try {
                    b = new ByteArrayEntity(r.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                b.setContentType("application/json;charset=UTF-8");
                p.setEntity(b);
                try {
                    mHttpclient.execute(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                HttpResponse resp = null;
                try {
                    resp = mHttpclient.execute(p, mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity = resp.getEntity();
                if (entity == null) try {
                    throw new IOException("entity == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content = null;
                try {
                    content = entity.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                StringBuilder builder = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    content.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ----- User ID ophalen ----- //
                // --------------------------- //

                HttpResponse resp2 = null;
                try {
                    resp2 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/account"), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity2 = resp2.getEntity();
                if (entity2 == null) try {
                    throw new IOException("entity2 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content2 = null;
                try {
                    content2 = entity2.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(content2));
                StringBuilder builder2 = new StringBuilder();
                String line2;
                try {
                    while ((line2 = reader2.readLine()) != null) {
                        builder2.append(line2);
                    }
                    content2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr = builder2.toString();

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON Object ophalen
                        person = jsonObj.getJSONObject(TAG_PERSON);

                        userid = person.getInt(TAG_USERID);
                        Log.i("Magister", "User ID: " + userid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage("Kon geen verbinding maken met Magister. Controleer je inloggegevens en probeer het later opnieuw.")
                                        .setCancelable(true)
                                        .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                            }
                        });
                    }
                } else {
                    Log.e("Magister", "Kon geen User ID ophalen van Magister!");
                }


                // ----- Rooster ophalen ----- //
                // --------------------------- //

                // userid = 11489;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String dinsdag = df.format(c.getTime());

                HttpResponse resp3 = null;
                try {
                    resp3 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/personen/" + userid + "/afspraken?van=" + dinsdag + "&tot=" + dinsdag), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity3 = resp3.getEntity();
                if (entity3 == null) try {
                    throw new IOException("entity3 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content3 = null;
                try {
                    content3 = entity3.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(content3));
                StringBuilder builder3 = new StringBuilder();
                String line3;
                try {
                    while ((line3 = reader3.readLine()) != null) {
                        builder3.append(line3);
                    }
                    content3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr2 = builder3.toString();

                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);

                        // JSON array ophalen
                        items = jsonObj2.getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject c2 = items.getJSONObject(i);

                            String id = c2.getString(TAG_LESSON_ID);
                            String hour = c2.getString(TAG_LESSON_HOUR);
                            String title = c2.getString(TAG_LESSON_DESC);
                            CharSequence text = Html.fromHtml(c2.getString(TAG_LESSON_LOCATION));
                            String time = "";
                            String starttime = "";

                            if (hour == "1") {
                                time = "8:20 - 9:10";
                                starttime = "8:20";
                            }else if (hour == "2") {
                                time = "9:10 - 10:00";
                                starttime = "9:10";
                            }else if (hour == "3") {
                                time = "10:20 - 11:10";
                                starttime = "10:20";
                            }else if (hour == "4") {
                                time = "11:10 - 12:00";
                                starttime = "11:10";
                            }else if (hour == "5") {
                                time = "12:25 - 13:15";
                                starttime = "12:25";
                            }else if (hour == "6") {
                                time = "13:15 - 14:05";
                                starttime = "13:15";
                            }else if (hour == "7") {
                                time = "14:20 - 15:10";
                                starttime = "14:20";
                            }else if (hour == "8") {
                                time = "15:10 - 16:00";
                                starttime = "15:10";
                            }else if (hour == "9") {
                                time = "16:00 - 16:50";
                                starttime = "16:00";
                            }
                            final CharSequence homework = c2.getString(TAG_LESSON_HOMEWORK);

                            vakarray = c2.getJSONArray(TAG_ARRAY_VAKKEN);

                            for (int i2 = 0; i2 < vakarray.length(); i2++) {
                                HashMap<String, CharSequence> vak = new HashMap<String, CharSequence>();

                                JSONObject c3 = vakarray.getJSONObject(i2);

                                lessonname = c3.getString(TAG_VAK_NAAM);
                                vak.put(TAG_VAK_NAAM, lessonname);

                                vakken.add(vak);
                            }

                            docentarray = c2.getJSONArray(TAG_ARRAY_DOCENTEN);

                            for (int i2 = 0; i2 < docentarray.length(); i2++) {
                                HashMap<String, CharSequence> docent = new HashMap<String, CharSequence>();

                                JSONObject c3 = docentarray.getJSONObject(i2);

                                docentname = c3.getString(TAG_DOCENT_NAAM);
                                docentcode = c3.getString(TAG_DOCENT_CODE);

                                docent.put(TAG_DOCENT_NAAM, docentname);
                                docent.put(TAG_DOCENT_CODE, docentcode);

                                docenten.add(docent);
                            }


                            HashMap<String, CharSequence> item = new HashMap<String, CharSequence>();

                            item.put(TAG_LESSON_ID, id);
                            item.put(TAG_LESSON_HOUR, hour);

                            if (prefs.getBoolean("magister_simplify", false) == true){
                                item.put(TAG_LESSON_DESC, lessonname);
                            }else{
                                item.put(TAG_LESSON_DESC, title);
                            }

                            item.put(TAG_LESSON_LOCATION, "Lokaal " + text + " - " + starttime);
                            item.put(TAG_LESSON_ROOM, text);
                            item.put(TAG_LESSON_HOMEWORK, homework);
                            item.put(TAG_LESSON_TIME, time);

                            // Item aan list toevoegen
                            itemList.add(item);


                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((SimpleAdapter) adapter).notifyDataSetChanged();
                                    listView.requestLayout();


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                long id) {
                                            HashMap<String, CharSequence> les = itemList.get(position);
                                            HashMap<String, CharSequence> vak = vakken.get(position);
                                            HashMap<String, CharSequence> docent = docenten.get(position);
                                            String homework2;
                                            if (les.get(TAG_LESSON_HOMEWORK) != "null"){
                                                homework2 = "\n\nOpmerking/huiswerk: " + Html.fromHtml(String.valueOf(les.get(TAG_LESSON_HOMEWORK)));
                                            }else{
                                                homework2 = "";
                                            }
                                            /*

                                            Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                            intent.putExtra(String.valueOf(DetailsActivity.LES), les);
                                            intent.putExtra(String.valueOf(DetailsActivity.VAK), vak);
                                            intent.putExtra(String.valueOf(DetailsActivity.DOCENT), docent);
                                            startActivity(intent);*/


                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle(les.get(TAG_LESSON_HOUR) + "e uur - " + vak.get(TAG_VAK_NAAM))
                                                    .setMessage("Lokaal: " + les.get(TAG_LESSON_ROOM) + "\n\nTijd: " + les.get(TAG_LESSON_TIME) + "\n\nDocent: " + docent.get(TAG_DOCENT_NAAM) + " (" + docent.get(TAG_DOCENT_CODE) + ")" + homework2)
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).create().show();

                                        }
                                    });
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Magister", "Kon geen data van Magister ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {
                            if (isNetworkAvailable(getActivity().getApplicationContext())) {

                                // ListAdapter adapter = null;
/*
                        if(getActivity() != null){
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new RoosterFragment()).commit();
                        }*/

                                // Dismiss progress-spinner
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                                // Adapter

                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                        }
                });
            }

        }

    }

    public static class RoosterWoensdagFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray items = null;
        JSONArray vakarray = null;
        JSONArray docentarray = null;
        JSONObject person = null;
        Integer userid = null;
        String lessonname = null;
        String docentname = null;
        String docentcode = null;
        ArrayList<HashMap<String, CharSequence>> itemList = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> vakken = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> docenten = new ArrayList<HashMap<String, CharSequence>>();

        public static RoosterWoensdagFragment newInstance() {
            RoosterWoensdagFragment fragment = new RoosterWoensdagFragment();
            return fragment;
        }

        public RoosterWoensdagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

            //  if (view == null) {

            view = inflater.inflate(R.layout.fragment_roosterwoensdag, container, false);
            listView = (ListView) view.findViewById(R.id.listView);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isNetworkAvailable(getActivity().getApplicationContext())) {
                        itemList.clear();
                        new GetRooster().execute();
                    }
                }
            }).start();

            view.getBackground().setAlpha(255);

                /*
                ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
                ab.setTitle("Rooster");
                ab.setSubtitle("Vandaag"); */

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                        Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                    //Refreshing data on server
                    new GetRooster().execute();
                    itemList.clear();
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});
                            ((SimpleAdapter) adapter).notifyDataSetChanged();
                            listView.requestLayout();
                        }
                    });
                    Log.i("SwipeRefresh", "Refreshing data...");
                }
            });

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
           /* } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }*/
            return view;
        }
/*
        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "RoosterFragment.onPause() has been called.");
            }
        }*/

        private class GetRooster extends AsyncTask<Void, Void, Void> {

            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                final DefaultHttpClient mHttpclient = new DefaultHttpClient();
                CookieStore mCookieStore = new BasicCookieStore();
                final HttpContext mLocalContext = new BasicHttpContext();
                mLocalContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                String username = prefs.getString("account_username", null);
                String password = prefs.getString("account_password", null);


                // ----- POST login ----- //
                // ---------------------- //

                final HttpPost p = new HttpPost("https://osgerasmus.magister.net/api/sessies");
                p.addHeader("Content-Type", "application/json;charset=UTF-8");
                String r = null;
                try {
                    r = new JSONStringer()
                            .object()
                            .key("Gebruikersnaam")
                            .value(username)
                            .key("Wachtwoord")
                            .value(password)
                            .key("IngelogdBlijven")
                            .value(true)
                            .endObject()
                            .toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteArrayEntity b = null;
                try {
                    b = new ByteArrayEntity(r.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                b.setContentType("application/json;charset=UTF-8");
                p.setEntity(b);
                try {
                    mHttpclient.execute(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                HttpResponse resp = null;
                try {
                    resp = mHttpclient.execute(p, mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity = resp.getEntity();
                if (entity == null) try {
                    throw new IOException("entity == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content = null;
                try {
                    content = entity.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                StringBuilder builder = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    content.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ----- User ID ophalen ----- //
                // --------------------------- //

                HttpResponse resp2 = null;
                try {
                    resp2 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/account"), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity2 = resp2.getEntity();
                if (entity2 == null) try {
                    throw new IOException("entity2 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content2 = null;
                try {
                    content2 = entity2.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(content2));
                StringBuilder builder2 = new StringBuilder();
                String line2;
                try {
                    while ((line2 = reader2.readLine()) != null) {
                        builder2.append(line2);
                    }
                    content2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr = builder2.toString();

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON Object ophalen
                        person = jsonObj.getJSONObject(TAG_PERSON);

                        userid = person.getInt(TAG_USERID);
                        Log.i("Magister", "User ID: " + userid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage("Kon geen verbinding maken met Magister. Controleer je inloggegevens en probeer het later opnieuw.")
                                        .setCancelable(true)
                                        .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                            }
                        });
                    }
                } else {
                    Log.e("Magister", "Kon geen User ID ophalen van Magister!");
                }


                // ----- Rooster ophalen ----- //
                // --------------------------- //

                // userid = 11489;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String woensdag = df.format(c.getTime());

                HttpResponse resp3 = null;
                try {
                    resp3 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/personen/" + userid + "/afspraken?van=" + woensdag + "&tot=" + woensdag), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity3 = resp3.getEntity();
                if (entity3 == null) try {
                    throw new IOException("entity3 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content3 = null;
                try {
                    content3 = entity3.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(content3));
                StringBuilder builder3 = new StringBuilder();
                String line3;
                try {
                    while ((line3 = reader3.readLine()) != null) {
                        builder3.append(line3);
                    }
                    content3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr2 = builder3.toString();

                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);

                        // JSON array ophalen
                        items = jsonObj2.getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject c2 = items.getJSONObject(i);

                            String id = c2.getString(TAG_LESSON_ID);
                            String hour = c2.getString(TAG_LESSON_HOUR);
                            String title = c2.getString(TAG_LESSON_DESC);
                            CharSequence text = Html.fromHtml(c2.getString(TAG_LESSON_LOCATION));
                            String time = "";
                            String starttime = "";

                            if (hour == "1") {
                                time = "8:20 - 9:10";
                                starttime = "8:20";
                            }else if (hour == "2") {
                                time = "9:10 - 10:00";
                                starttime = "9:10";
                            }else if (hour == "3") {
                                time = "10:20 - 11:10";
                                starttime = "10:20";
                            }else if (hour == "4") {
                                time = "11:10 - 12:00";
                                starttime = "11:10";
                            }else if (hour == "5") {
                                time = "12:25 - 13:15";
                                starttime = "12:25";
                            }else if (hour == "6") {
                                time = "13:15 - 14:05";
                                starttime = "13:15";
                            }else if (hour == "7") {
                                time = "14:20 - 15:10";
                                starttime = "14:20";
                            }else if (hour == "8") {
                                time = "15:10 - 16:00";
                                starttime = "15:10";
                            }else if (hour == "9") {
                                time = "16:00 - 16:50";
                                starttime = "16:00";
                            }
                            final CharSequence homework = c2.getString(TAG_LESSON_HOMEWORK);

                            vakarray = c2.getJSONArray(TAG_ARRAY_VAKKEN);

                            for (int i2 = 0; i2 < vakarray.length(); i2++) {
                                HashMap<String, CharSequence> vak = new HashMap<String, CharSequence>();

                                JSONObject c3 = vakarray.getJSONObject(i2);

                                lessonname = c3.getString(TAG_VAK_NAAM);
                                vak.put(TAG_VAK_NAAM, lessonname);

                                vakken.add(vak);
                            }

                            docentarray = c2.getJSONArray(TAG_ARRAY_DOCENTEN);

                            for (int i2 = 0; i2 < docentarray.length(); i2++) {
                                HashMap<String, CharSequence> docent = new HashMap<String, CharSequence>();

                                JSONObject c3 = docentarray.getJSONObject(i2);

                                docentname = c3.getString(TAG_DOCENT_NAAM);
                                docentcode = c3.getString(TAG_DOCENT_CODE);

                                docent.put(TAG_DOCENT_NAAM, docentname);
                                docent.put(TAG_DOCENT_CODE, docentcode);

                                docenten.add(docent);
                            }


                            HashMap<String, CharSequence> item = new HashMap<String, CharSequence>();

                            item.put(TAG_LESSON_ID, id);
                            item.put(TAG_LESSON_HOUR, hour);

                            if (prefs.getBoolean("magister_simplify", false) == true){
                                item.put(TAG_LESSON_DESC, lessonname);
                            }else{
                                item.put(TAG_LESSON_DESC, title);
                            }

                            item.put(TAG_LESSON_LOCATION, "Lokaal " + text + " - " + starttime);
                            item.put(TAG_LESSON_ROOM, text);
                            item.put(TAG_LESSON_HOMEWORK, homework);
                            item.put(TAG_LESSON_TIME, time);

                            // Item aan list toevoegen
                            itemList.add(item);


                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((SimpleAdapter) adapter).notifyDataSetChanged();
                                    listView.requestLayout();


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                long id) {
                                            HashMap<String, CharSequence> les = itemList.get(position);
                                            HashMap<String, CharSequence> vak = vakken.get(position);
                                            HashMap<String, CharSequence> docent = docenten.get(position);
                                            String homework2;
                                            if (les.get(TAG_LESSON_HOMEWORK) != "null"){
                                                homework2 = "\n\nOpmerking/huiswerk: " + Html.fromHtml(String.valueOf(les.get(TAG_LESSON_HOMEWORK)));
                                            }else{
                                                homework2 = "";
                                            }


                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle(les.get(TAG_LESSON_HOUR) + "e uur - " + vak.get(TAG_VAK_NAAM))
                                                    .setMessage("Lokaal: " + les.get(TAG_LESSON_ROOM) + "\n\nTijd: " + les.get(TAG_LESSON_TIME) + "\n\nDocent: " + docent.get(TAG_DOCENT_NAAM) + " (" + docent.get(TAG_DOCENT_CODE) + ")" + homework2)
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).create().show();

                                        }
                                    });
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Magister", "Kon geen data van Magister ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {
                            if (isNetworkAvailable(getActivity().getApplicationContext())) {

                                // ListAdapter adapter = null;
/*
                        if(getActivity() != null){
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new RoosterFragment()).commit();
                        }*/

                                // Dismiss progress-spinner
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                                // Adapter

                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                    }
                });
            }

        }

    }

    public static class RoosterDonderdagFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray items = null;
        JSONArray vakarray = null;
        JSONArray docentarray = null;
        JSONObject person = null;
        Integer userid = null;
        String lessonname = null;
        String docentname = null;
        String docentcode = null;
        ArrayList<HashMap<String, CharSequence>> itemList = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> vakken = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> docenten = new ArrayList<HashMap<String, CharSequence>>();

        public static RoosterDonderdagFragment newInstance() {
            RoosterDonderdagFragment fragment = new RoosterDonderdagFragment();
            return fragment;
        }

        public RoosterDonderdagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

            //  if (view == null) {

            view = inflater.inflate(R.layout.fragment_roosterdonderdag, container, false);
            listView = (ListView) view.findViewById(R.id.listView);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isNetworkAvailable(getActivity().getApplicationContext())) {
                        itemList.clear();
                        new GetRooster().execute();
                    }
                }
            }).start();

            view.getBackground().setAlpha(255);

                /*
                ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
                ab.setTitle("Rooster");
                ab.setSubtitle("Vandaag"); */

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                        Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                    //Refreshing data on server
                    new GetRooster().execute();
                    itemList.clear();
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});
                            ((SimpleAdapter) adapter).notifyDataSetChanged();
                            listView.requestLayout();
                        }
                    });
                    Log.i("SwipeRefresh", "Refreshing data...");
                }
            });

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
           /* } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }*/
            return view;
        }
/*
        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "RoosterFragment.onPause() has been called.");
            }
        }*/

        private class GetRooster extends AsyncTask<Void, Void, Void> {

            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                final DefaultHttpClient mHttpclient = new DefaultHttpClient();
                CookieStore mCookieStore = new BasicCookieStore();
                final HttpContext mLocalContext = new BasicHttpContext();
                mLocalContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                String username = prefs.getString("account_username", null);
                String password = prefs.getString("account_password", null);


                // ----- POST login ----- //
                // ---------------------- //

                final HttpPost p = new HttpPost("https://osgerasmus.magister.net/api/sessies");
                p.addHeader("Content-Type", "application/json;charset=UTF-8");
                String r = null;
                try {
                    r = new JSONStringer()
                            .object()
                            .key("Gebruikersnaam")
                            .value(username)
                            .key("Wachtwoord")
                            .value(password)
                            .key("IngelogdBlijven")
                            .value(true)
                            .endObject()
                            .toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteArrayEntity b = null;
                try {
                    b = new ByteArrayEntity(r.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                b.setContentType("application/json;charset=UTF-8");
                p.setEntity(b);
                try {
                    mHttpclient.execute(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                HttpResponse resp = null;
                try {
                    resp = mHttpclient.execute(p, mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity = resp.getEntity();
                if (entity == null) try {
                    throw new IOException("entity == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content = null;
                try {
                    content = entity.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                StringBuilder builder = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    content.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ----- User ID ophalen ----- //
                // --------------------------- //

                HttpResponse resp2 = null;
                try {
                    resp2 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/account"), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity2 = resp2.getEntity();
                if (entity2 == null) try {
                    throw new IOException("entity2 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content2 = null;
                try {
                    content2 = entity2.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(content2));
                StringBuilder builder2 = new StringBuilder();
                String line2;
                try {
                    while ((line2 = reader2.readLine()) != null) {
                        builder2.append(line2);
                    }
                    content2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr = builder2.toString();

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON Object ophalen
                        person = jsonObj.getJSONObject(TAG_PERSON);

                        userid = person.getInt(TAG_USERID);
                        Log.i("Magister", "User ID: " + userid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage("Kon geen verbinding maken met Magister. Controleer je inloggegevens en probeer het later opnieuw.")
                                        .setCancelable(true)
                                        .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                            }
                        });
                    }
                } else {
                    Log.e("Magister", "Kon geen User ID ophalen van Magister!");
                }


                // ----- Rooster ophalen ----- //
                // --------------------------- //

                // userid = 11489;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String donderdag = df.format(c.getTime());

                HttpResponse resp3 = null;
                try {
                    resp3 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/personen/" + userid + "/afspraken?van=" + donderdag + "&tot=" + donderdag), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity3 = resp3.getEntity();
                if (entity3 == null) try {
                    throw new IOException("entity3 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content3 = null;
                try {
                    content3 = entity3.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(content3));
                StringBuilder builder3 = new StringBuilder();
                String line3;
                try {
                    while ((line3 = reader3.readLine()) != null) {
                        builder3.append(line3);
                    }
                    content3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr2 = builder3.toString();

                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);

                        // JSON array ophalen
                        items = jsonObj2.getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject c2 = items.getJSONObject(i);

                            String id = c2.getString(TAG_LESSON_ID);
                            String hour = c2.getString(TAG_LESSON_HOUR);
                            String title = c2.getString(TAG_LESSON_DESC);
                            CharSequence text = Html.fromHtml(c2.getString(TAG_LESSON_LOCATION));
                            String time = "";
                            String starttime = "";

                            if (hour == "1") {
                                time = "8:20 - 9:10";
                                starttime = "8:20";
                            }else if (hour == "2") {
                                time = "9:10 - 10:00";
                                starttime = "9:10";
                            }else if (hour == "3") {
                                time = "10:20 - 11:10";
                                starttime = "10:20";
                            }else if (hour == "4") {
                                time = "11:10 - 12:00";
                                starttime = "11:10";
                            }else if (hour == "5") {
                                time = "12:25 - 13:15";
                                starttime = "12:25";
                            }else if (hour == "6") {
                                time = "13:15 - 14:05";
                                starttime = "13:15";
                            }else if (hour == "7") {
                                time = "14:20 - 15:10";
                                starttime = "14:20";
                            }else if (hour == "8") {
                                time = "15:10 - 16:00";
                                starttime = "15:10";
                            }else if (hour == "9") {
                                time = "16:00 - 16:50";
                                starttime = "16:00";
                            }
                            final CharSequence homework = c2.getString(TAG_LESSON_HOMEWORK);

                            vakarray = c2.getJSONArray(TAG_ARRAY_VAKKEN);

                            for (int i2 = 0; i2 < vakarray.length(); i2++) {
                                HashMap<String, CharSequence> vak = new HashMap<String, CharSequence>();

                                JSONObject c3 = vakarray.getJSONObject(i2);

                                lessonname = c3.getString(TAG_VAK_NAAM);
                                vak.put(TAG_VAK_NAAM, lessonname);

                                vakken.add(vak);
                            }

                            docentarray = c2.getJSONArray(TAG_ARRAY_DOCENTEN);

                            for (int i2 = 0; i2 < docentarray.length(); i2++) {
                                HashMap<String, CharSequence> docent = new HashMap<String, CharSequence>();

                                JSONObject c3 = docentarray.getJSONObject(i2);

                                docentname = c3.getString(TAG_DOCENT_NAAM);
                                docentcode = c3.getString(TAG_DOCENT_CODE);

                                docent.put(TAG_DOCENT_NAAM, docentname);
                                docent.put(TAG_DOCENT_CODE, docentcode);

                                docenten.add(docent);
                            }


                            HashMap<String, CharSequence> item = new HashMap<String, CharSequence>();

                            item.put(TAG_LESSON_ID, id);
                            item.put(TAG_LESSON_HOUR, hour);

                            if (prefs.getBoolean("magister_simplify", false) == true){
                                item.put(TAG_LESSON_DESC, lessonname);
                            }else{
                                item.put(TAG_LESSON_DESC, title);
                            }

                            item.put(TAG_LESSON_LOCATION, "Lokaal " + text + " - " + starttime);
                            item.put(TAG_LESSON_ROOM, text);
                            item.put(TAG_LESSON_HOMEWORK, homework);
                            item.put(TAG_LESSON_TIME, time);

                            // Item aan list toevoegen
                            itemList.add(item);


                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((SimpleAdapter) adapter).notifyDataSetChanged();
                                    listView.requestLayout();


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                long id) {
                                            HashMap<String, CharSequence> les = itemList.get(position);
                                            HashMap<String, CharSequence> vak = vakken.get(position);
                                            HashMap<String, CharSequence> docent = docenten.get(position);
                                            String homework2;
                                            if (les.get(TAG_LESSON_HOMEWORK) != "null"){
                                                homework2 = "\n\nOpmerking/huiswerk: " + Html.fromHtml(String.valueOf(les.get(TAG_LESSON_HOMEWORK)));
                                            }else{
                                                homework2 = "";
                                            }


                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle(les.get(TAG_LESSON_HOUR) + "e uur - " + vak.get(TAG_VAK_NAAM))
                                                    .setMessage("Lokaal: " + les.get(TAG_LESSON_ROOM) + "\n\nTijd: " + les.get(TAG_LESSON_TIME) + "\n\nDocent: " + docent.get(TAG_DOCENT_NAAM) + " (" + docent.get(TAG_DOCENT_CODE) + ")" + homework2)
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).create().show();

                                        }
                                    });
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Magister", "Kon geen data van Magister ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {
                            if (isNetworkAvailable(getActivity().getApplicationContext())) {

                                // ListAdapter adapter = null;
/*
                        if(getActivity() != null){
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new RoosterFragment()).commit();
                        }*/

                                // Dismiss progress-spinner
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                                // Adapter

                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                    }
                });
            }

        }

    }

    public static class RoosterVrijdagFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray items = null;
        JSONArray vakarray = null;
        JSONArray docentarray = null;
        JSONObject person = null;
        Integer userid = null;
        String lessonname = null;
        String docentname = null;
        String docentcode = null;
        ArrayList<HashMap<String, CharSequence>> itemList = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> vakken = new ArrayList<HashMap<String, CharSequence>>();
        ArrayList<HashMap<String, CharSequence>> docenten = new ArrayList<HashMap<String, CharSequence>>();

        public static RoosterVrijdagFragment newInstance() {
            RoosterVrijdagFragment fragment = new RoosterVrijdagFragment();
            return fragment;
        }

        public RoosterVrijdagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

            //  if (view == null) {

            view = inflater.inflate(R.layout.fragment_roostervrijdag, container, false);
            listView = (ListView) view.findViewById(R.id.listView);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isNetworkAvailable(getActivity().getApplicationContext())) {
                        itemList.clear();
                        new GetRooster().execute();
                    }
                }
            }).start();

            view.getBackground().setAlpha(255);

                /*
                ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
                ab.setTitle("Rooster");
                ab.setSubtitle("Vandaag"); */

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!isNetworkAvailable(getActivity().getApplicationContext())) {
                        Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                    //Refreshing data on server
                    new GetRooster().execute();
                    itemList.clear();
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});
                            ((SimpleAdapter) adapter).notifyDataSetChanged();
                            listView.requestLayout();
                        }
                    });
                    Log.i("SwipeRefresh", "Refreshing data...");
                }
            });

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
           /* } else {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }*/
            return view;
        }
/*
        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
                Log.d("DEBUG", "RoosterFragment.onPause() has been called.");
            }
        }*/

        private class GetRooster extends AsyncTask<Void, Void, Void> {

            ListAdapter adapter = new SimpleAdapter(getActivity(), itemList, R.layout.rooster_list_item, new String[]{TAG_LESSON_HOUR, TAG_LESSON_DESC, TAG_LESSON_LOCATION}, new int[]{R.id.hour, R.id.title, R.id.text});

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                final DefaultHttpClient mHttpclient = new DefaultHttpClient();
                CookieStore mCookieStore = new BasicCookieStore();
                final HttpContext mLocalContext = new BasicHttpContext();
                mLocalContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                String username = prefs.getString("account_username", null);
                String password = prefs.getString("account_password", null);


                // ----- POST login ----- //
                // ---------------------- //

                final HttpPost p = new HttpPost("https://osgerasmus.magister.net/api/sessies");
                p.addHeader("Content-Type", "application/json;charset=UTF-8");
                String r = null;
                try {
                    r = new JSONStringer()
                            .object()
                            .key("Gebruikersnaam")
                            .value(username)
                            .key("Wachtwoord")
                            .value(password)
                            .key("IngelogdBlijven")
                            .value(true)
                            .endObject()
                            .toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteArrayEntity b = null;
                try {
                    b = new ByteArrayEntity(r.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                b.setContentType("application/json;charset=UTF-8");
                p.setEntity(b);
                try {
                    mHttpclient.execute(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                HttpResponse resp = null;
                try {
                    resp = mHttpclient.execute(p, mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity = resp.getEntity();
                if (entity == null) try {
                    throw new IOException("entity == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content = null;
                try {
                    content = entity.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                StringBuilder builder = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    content.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ----- User ID ophalen ----- //
                // --------------------------- //

                HttpResponse resp2 = null;
                try {
                    resp2 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/account"), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity2 = resp2.getEntity();
                if (entity2 == null) try {
                    throw new IOException("entity2 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content2 = null;
                try {
                    content2 = entity2.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(content2));
                StringBuilder builder2 = new StringBuilder();
                String line2;
                try {
                    while ((line2 = reader2.readLine()) != null) {
                        builder2.append(line2);
                    }
                    content2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr = builder2.toString();

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON Object ophalen
                        person = jsonObj.getJSONObject(TAG_PERSON);

                        userid = person.getInt(TAG_USERID);
                        Log.i("Magister", "User ID: " + userid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage("Kon geen verbinding maken met Magister. Controleer je inloggegevens en probeer het later opnieuw.")
                                        .setCancelable(true)
                                        .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                            }
                        });
                    }
                } else {
                    Log.e("Magister", "Kon geen User ID ophalen van Magister!");
                }


                // ----- Rooster ophalen ----- //
                // --------------------------- //

                // userid = 11489;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String vrijdag = df.format(c.getTime());

                HttpResponse resp3 = null;
                try {
                    resp3 = mHttpclient.execute(new HttpGet("https://osgerasmus.magister.net/api/personen/" + userid + "/afspraken?van=" + vrijdag + "&tot=" + vrijdag), mLocalContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity3 = resp3.getEntity();
                if (entity3 == null) try {
                    throw new IOException("entity3 == null");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream content3 = null;
                try {
                    content3 = entity3.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(content3));
                StringBuilder builder3 = new StringBuilder();
                String line3;
                try {
                    while ((line3 = reader3.readLine()) != null) {
                        builder3.append(line3);
                    }
                    content3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonStr2 = builder3.toString();

                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);

                        // JSON array ophalen
                        items = jsonObj2.getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject c2 = items.getJSONObject(i);

                            String id = c2.getString(TAG_LESSON_ID);
                            String hour = c2.getString(TAG_LESSON_HOUR);
                            String title = c2.getString(TAG_LESSON_DESC);
                            CharSequence text = Html.fromHtml(c2.getString(TAG_LESSON_LOCATION));
                            String time = "";
                            String starttime = "";

                            if (hour == "1") {
                                time = "8:20 - 9:10";
                                starttime = "8:20";
                            }else if (hour == "2") {
                                time = "9:10 - 10:00";
                                starttime = "9:10";
                            }else if (hour == "3") {
                                time = "10:20 - 11:10";
                                starttime = "10:20";
                            }else if (hour == "4") {
                                time = "11:10 - 12:00";
                                starttime = "11:10";
                            }else if (hour == "5") {
                                time = "12:25 - 13:15";
                                starttime = "12:25";
                            }else if (hour == "6") {
                                time = "13:15 - 14:05";
                                starttime = "13:15";
                            }else if (hour == "7") {
                                time = "14:20 - 15:10";
                                starttime = "14:20";
                            }else if (hour == "8") {
                                time = "15:10 - 16:00";
                                starttime = "15:10";
                            }else if (hour == "9") {
                                time = "16:00 - 16:50";
                                starttime = "16:00";
                            }
                            final CharSequence homework = c2.getString(TAG_LESSON_HOMEWORK);

                            vakarray = c2.getJSONArray(TAG_ARRAY_VAKKEN);

                            for (int i2 = 0; i2 < vakarray.length(); i2++) {
                                HashMap<String, CharSequence> vak = new HashMap<String, CharSequence>();

                                JSONObject c3 = vakarray.getJSONObject(i2);

                                lessonname = c3.getString(TAG_VAK_NAAM);
                                vak.put(TAG_VAK_NAAM, lessonname);

                                vakken.add(vak);
                            }

                            docentarray = c2.getJSONArray(TAG_ARRAY_DOCENTEN);

                            for (int i2 = 0; i2 < docentarray.length(); i2++) {
                                HashMap<String, CharSequence> docent = new HashMap<String, CharSequence>();

                                JSONObject c3 = docentarray.getJSONObject(i2);

                                docentname = c3.getString(TAG_DOCENT_NAAM);
                                docentcode = c3.getString(TAG_DOCENT_CODE);

                                docent.put(TAG_DOCENT_NAAM, docentname);
                                docent.put(TAG_DOCENT_CODE, docentcode);

                                docenten.add(docent);
                            }


                            HashMap<String, CharSequence> item = new HashMap<String, CharSequence>();

                            item.put(TAG_LESSON_ID, id);
                            item.put(TAG_LESSON_HOUR, hour);

                            if (prefs.getBoolean("magister_simplify", false) == true){
                                item.put(TAG_LESSON_DESC, lessonname);
                            }else{
                                item.put(TAG_LESSON_DESC, title);
                            }

                            item.put(TAG_LESSON_LOCATION, "Lokaal " + text + " - " + starttime);
                            item.put(TAG_LESSON_ROOM, text);
                            item.put(TAG_LESSON_HOMEWORK, homework);
                            item.put(TAG_LESSON_TIME, time);

                            // Item aan list toevoegen
                            itemList.add(item);


                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((SimpleAdapter) adapter).notifyDataSetChanged();
                                    listView.requestLayout();


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                long id) {
                                            HashMap<String, CharSequence> les = itemList.get(position);
                                            HashMap<String, CharSequence> vak = vakken.get(position);
                                            HashMap<String, CharSequence> docent = docenten.get(position);
                                            String homework2;
                                            if (les.get(TAG_LESSON_HOMEWORK) != "null"){
                                                homework2 = "\n\nOpmerking/huiswerk: " + Html.fromHtml(String.valueOf(les.get(TAG_LESSON_HOMEWORK)));
                                            }else{
                                                homework2 = "";
                                            }


                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle(les.get(TAG_LESSON_HOUR) + "e uur - " + vak.get(TAG_VAK_NAAM))
                                                    .setMessage("Lokaal: " + les.get(TAG_LESSON_ROOM) + "\n\nTijd: " + les.get(TAG_LESSON_TIME) + "\n\nDocent: " + docent.get(TAG_DOCENT_NAAM) + " (" + docent.get(TAG_DOCENT_CODE) + ")" + homework2)
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sluiten", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).create().show();

                                        }
                                    });
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Magister", "Kon geen data van Magister ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {
                            if (isNetworkAvailable(getActivity().getApplicationContext())) {

                                // ListAdapter adapter = null;
/*
                        if(getActivity() != null){
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new RoosterFragment()).commit();
                        }*/

                                // Dismiss progress-spinner
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                                // Adapter

                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                    }
                });
            }

        }

    }

    static void restartMain(Activity activity){
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    static class PagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = {"Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RoosterMaandagFragment.newInstance(1);
                case 1:
                    return new RoosterDinsdagFragment();
                case 2:
                    return new RoosterWoensdagFragment();
                case 3:
                    return new RoosterDonderdagFragment();
                case 4:
                    return new RoosterVrijdagFragment();
            }

            return null;
        }
    }

}

