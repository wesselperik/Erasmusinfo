package com.wesselperik.erasmusinfo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.wesselperik.erasmusinfo.R;

import com.wesselperik.erasmusinfo.classes.Analytics;
import com.wesselperik.erasmusinfo.fragments.ChangesFragment;
import com.wesselperik.erasmusinfo.fragments.NewsFragment;
import com.wesselperik.erasmusinfo.fragments.PostsFragment;
import com.wesselperik.erasmusinfo.services.FCMInitializationService;
import com.wesselperik.erasmusinfo.services.WearService;
import com.wesselperik.erasmusinfo.views.TextViewBold;
import com.wesselperik.erasmusinfo.views.TextViewMedium;
//import com.wesselperik.erasmusinfo.fragments.Nieuws;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingtoolbar) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.toolbar_title) TextViewMedium toolbarTitle;
    @BindView(R.id.toolbar_content_title) TextViewBold toolbarContentTitle;
    @BindView(R.id.navigation) BottomNavigationView bottomNavigation;

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
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, NewsFragment.newInstance(), "NewsFragment").commit();
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean wearServiceEnabled = prefs.getBoolean("settings_wear_service", false);
        if (wearServiceEnabled) startService(new Intent(this, WearService.class));

        startService(new Intent(this, FCMInitializationService.class));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

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

        toolbarLayout.setTitle(" ");
        toolbarTitle.setText(" ");
        toolbarContentTitle.setText("mededelingen");

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Analytics analytics = new Analytics(getApplicationContext());
        analytics.init();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, PostsFragment.newInstance(), "PostsFragment").commit();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

}

