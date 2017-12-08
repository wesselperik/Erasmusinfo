package com.wesselperik.erasmusinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.wesselperik.erasmusinfo.activities.AboutActivity;
import com.wesselperik.erasmusinfo.activities.SettingsActivity;

import com.wesselperik.erasmusinfo.classes.Analytics;
import com.wesselperik.erasmusinfo.fragments.ChangesFragment;
import com.wesselperik.erasmusinfo.fragments.Infokanaal;
import com.wesselperik.erasmusinfo.fragments.Nieuws;
import com.wesselperik.erasmusinfo.fragments.PostsFragment;
import com.wesselperik.erasmusinfo.services.WearService;
//import com.wesselperik.erasmusinfo.fragments.Nieuws;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private CollapsingToolbarLayout toolbarLayout;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_posts:
                    viewPager.setCurrentItem(0, true);
                    return true;
                case R.id.nav_changes:
                    viewPager.setCurrentItem(1, true);
                    return true;
                case R.id.nav_news:
                    viewPager.setCurrentItem(2, true);
                    return true;
            }
            return false;
        }

    };

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbar);
        toolbarLayout.setTitle(" ");

        Analytics analytics = new Analytics(getApplicationContext());
        analytics.init();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PostsFragment.newInstance(), "Mededelingen");
        adapter.addFragment(ChangesFragment.newInstance(), "Roosterwijzigingen");
        adapter.addFragment(Nieuws.newInstance(), "Nieuws");
        viewPager.setAdapter(adapter);
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

                postsFragment.refresh();
                changesFragment.refresh();
                return true;

            case R.id.school_vmbo:
                if (!item.isChecked()){
                    item.setChecked(true);
                }
                SharedPreferences.Editor edit2 = prefs.edit();
                edit2.putString("settings_schoolname", "vmbo");
                edit2.apply();

                postsFragment.refresh();
                changesFragment.refresh();
                return true;

            case R.id.school_pro:
                if (!item.isChecked()){
                    item.setChecked(true);
                }
                SharedPreferences.Editor edit3 = prefs.edit();
                edit3.putString("settings_schoolname", "pro");
                edit3.apply();

                postsFragment.refresh();
                changesFragment.refresh();
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

