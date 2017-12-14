package com.wesselperik.erasmusinfo.activities;

/**
 * Created by Wessel on 12-11-2015.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.views.TextViewBold;

import butterknife.BindView;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) private Toolbar toolbar;
    @BindView(R.id.collapsingtoolbar) private CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.appbar) private AppBarLayout appBar;
    @BindView(R.id.toolbar_content_title) private TextViewBold toolbarContentTitle;

    @BindView(R.id.text_section1) TextView section1;
    @BindView(R.id.text_section2) TextView section2;
    @BindView(R.id.text_section3) TextView section3;
    @BindView(R.id.text_section4) TextView section4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

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
        toolbarContentTitle.setText("over erasmusinfo");

        section1.setText("In 2014 ben ik begonnen met de ontwikkeling van deze app, om een alternatief te bieden voor het Infokanaal van Het Erasmus, zodat deze beter te lezen is op je telefoon! Let op: deze app is officieel niet van Het Erasmus en gemaakt door een oud-leerling.");
        section2.setText("Gebruik jij de Erasmusinfo app graag? Help door een beoordeling achter te laten in de Play Store. Alvast bedankt!");
        section3.setText("Ik wil deze geweldige mensen bedanken voor hun bijdrage aan deze app: \n\n" +
                "\u2022 Justin Noppers\n" +
                "\u2022 Florian Nouwt\n" +
                "\u2022 Rico Schwab\n" +
                "\u2022 Max van den Bosch\n" +
                "\u2022 Lennard Deurman\n" +
                "\u2022 Alle beta-testers");
        section4.setText("Heb je een vraag, een suggestie of een bug gevonden? Stuur mij een e-mail!");

    }
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_playstore:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.wesselperik.erasmusinfo"));
                startActivity(intent);
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

    public void goToPlayStore(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.wesselperik.erasmusinfo"));
        startActivity(intent);
    }

    public void mailDeveloper(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:info@wesselperik.com?Subject=Erasmusinfo Android App"));
        startActivity(intent);
    }
}