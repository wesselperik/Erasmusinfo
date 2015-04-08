package com.wesselperik.erasmusinfo;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "Erasmusinfo";
    String[] menutitles;
    TypedArray menuIcons;
    String[] pageUrl;
    RelativeLayout mDrawerPane;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;

    private List<RowItem> rowItems;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        menutitles = getResources().getStringArray(R.array.titles);
        menuIcons = getResources().obtainTypedArray(R.array.icons);
        pageUrl = getResources().getStringArray(R.array.pageurl);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerPane = (RelativeLayout) findViewById(R.id.drawer_pane);
        mDrawerList = (ListView) findViewById(R.id.slider_list);
        rowItems = new ArrayList<RowItem>();

        for (int i = 0; i < menutitles.length; i++) {
            RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
                    i, -1), pageUrl[i]);
            rowItems.add(items);
        }

        menuIcons.recycle();

        adapter = new CustomAdapter(getApplicationContext(), rowItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideitemListener());

       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        giveVoteOption();

        if (savedInstanceState == null) {
            updateDisplay(0);
        }
    }



    class SlideitemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            updateDisplay(position);
        }
    }

    private void updateDisplay(int position) {
        String url = rowItems.get(position).getPageUrl();
        Fragment fragment = new WebViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).commit();
        setTitle(menutitles[position]);
        mDrawerLayout.closeDrawer(mDrawerPane);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Actionbar items
        switch (item.getItemId()) {
            case R.id.action_about:
                final AlertDialog d = new AlertDialog.Builder(this)
                        .setTitle("Over deze app")
                        .setMessage(Html.fromHtml("Erasmusinfo v1.4<br /><br /><a href=\"http://erasmusinfo.nl\">Website</a><br /><br /><a href=\"http://github.com/wesselperik/Erasmusinfo\">Sourcecode op GitHub</a><br /><br />© 2015 Wessel Perik"))
                        .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_icon)
                        .create();
                        d.show();
                        ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                return true;

            case R.id.action_changelog:
                ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(this);
                _ChangelogDialog.show();
               return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    // Doneren action
    public void donateAction(View v) {
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Doneren")
                .setMessage(Html.fromHtml("Wil je de ontwikkeling van deze app steunen? Je kunt een (klein) bedrag doneren via PayPal, om dit te doen klik simpelweg op de knop 'Doneren' hieronder om doorverwezen te worden.<br /><br />Bij de donatie kun je je naam vermelden. Dit is niet verplicht, maar als je dit wel doet zal je naam vermeld worden in de app in het 'Over deze app'-scherm, als dank voor je bijdrage."))
                .setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=4V8MQJJN62BWA");
                        Intent goToPaypal = new Intent(Intent.ACTION_VIEW, uri);
                        goToPaypal.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        try {
                            startActivity(goToPaypal);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=4V8MQJJN62BWA")));
                        }
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_icon)
                .create();
                d.show();
                ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    // Waardeer app-action
    public void rateAppAction(View v) {
        Context context = this;
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    // AppRater (automatich waardeer-app scherm)
    private void giveVoteOption() {
        Log.i(TAG, "giveVoteOption");
        AppRater appRater = new AppRater(this);
        appRater.setDaysBeforePrompt(2);
        appRater.setLaunchesBeforePrompt(5);

        appRater.setPhrases("Beoordeel deze app", "Vindt je deze app nuttig/handig/cool/geweldig? Geef dan je mening op Google Play. Bedankt voor je support!", "Beoordeel", "Later", "Nee bedankt");
        appRater.setTargetUri("https://play.google.com/store/apps/details?id=com.wesselperik.erasmusinfo");
        appRater.setPreferenceKeys("app_rater", "flag_dont_show", "launch_count", "first_launch_time");

        appRater.show();
    }

}
