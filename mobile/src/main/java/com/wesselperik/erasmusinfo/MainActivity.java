package com.wesselperik.erasmusinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.activities.AboutActivity;
import com.wesselperik.erasmusinfo.activities.SettingsActivity;

import com.wesselperik.erasmusinfo.classes.Analytics;
import com.wesselperik.erasmusinfo.fragments.ChangesFragment;
import com.wesselperik.erasmusinfo.fragments.Infokanaal;
import com.wesselperik.erasmusinfo.fragments.Nieuws;
import com.wesselperik.erasmusinfo.fragments.PostsFragment;
import com.wesselperik.erasmusinfo.services.WearService;
import com.wesselperik.erasmusinfo.views.TextViewBold;
import com.wesselperik.erasmusinfo.views.TextViewMedium;
//import com.wesselperik.erasmusinfo.fragments.Nieuws;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private AppBarLayout appBar;
    private TextViewBold toolbarTitle;
    private TextViewBold toolbarContentTitle;
    private BottomNavigationView bottomNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_posts:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, PostsFragment.newInstance(), "PostsFragment").commit();
                    toolbarContentTitle.setText("mededelingen");
                    return true;
                case R.id.nav_changes:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, ChangesFragment.newInstance(), "ChangesFragment").commit();
                    toolbarContentTitle.setText("roosterwijzigingen");
                    return true;
                case R.id.nav_news:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, Nieuws.newInstance(), "Nieuws").commit();
                    toolbarContentTitle.setText("nieuws");
                    return true;
            }
            return false;
        }
    };

    private boolean isTitleShown = false;

    public MainActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getIntent().hasExtra("bundle") && savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        }

        startService(new Intent(this, WearService.class));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_new);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                toolbarContentTitle.setAlpha(1.0f - Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbarTitle.setText("erasmusinfo");
                    isTitleShown = true;
                } else if(isTitleShown) {
                    toolbarTitle.setText(" ");
                    isTitleShown = false;
                }
            }
        });

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbar);
        toolbarLayout.setTitle(" ");

        toolbarTitle = (TextViewBold) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(" ");

        toolbarContentTitle = (TextViewBold) findViewById(R.id.toolbar_content_title);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Analytics analytics = new Analytics(getApplicationContext());
        analytics.init();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, PostsFragment.newInstance(), "PostsFragment").commit();
        toolbarContentTitle.setText("mededelingen");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String school = prefs.getString("settings_schoolname", "havovwo");
        if (school.equals("havovwo")) {
            menu.findItem(R.id.school_havovwo).setChecked(true);
        }else if (school.equals("vmbo")) {
            menu.findItem(R.id.school_vmbo).setChecked(true);
        }else if (school.equals("pro")) {
            menu.findItem(R.id.school_pro).setChecked(true);
        }else{
            menu.findItem(R.id.school_havovwo).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        PostsFragment postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 0);
        ChangesFragment changesFragment = (ChangesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 1);

        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            case R.id.school_havovwo:
                if (!item.isChecked()){
                    item.setChecked(true);
                }
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("settings_schoolname", "havovwo");
                edit.apply();

                if (postsFragment != null) postsFragment.refresh();
                if (changesFragment != null) changesFragment.refresh();
                return true;

            case R.id.school_vmbo:
                if (!item.isChecked()){
                    item.setChecked(true);
                }
                SharedPreferences.Editor edit2 = prefs.edit();
                edit2.putString("settings_schoolname", "vmbo");
                edit2.apply();

                if (postsFragment != null) postsFragment.refresh();
                if (changesFragment != null) changesFragment.refresh();
                return true;

            case R.id.school_pro:
                if (!item.isChecked()){
                    item.setChecked(true);
                }
                SharedPreferences.Editor edit3 = prefs.edit();
                edit3.putString("settings_schoolname", "pro");
                edit3.apply();

                if (postsFragment != null) postsFragment.refresh();
                if (changesFragment != null) changesFragment.refresh();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;

            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

