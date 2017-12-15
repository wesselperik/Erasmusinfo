package com.wesselperik.erasmusinfo.activities;

/**
 * Created by Wessel on 5-11-2015.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.interfaces.NewsCallback;
import com.wesselperik.erasmusinfo.interfaces.NewsDetailCallback;
import com.wesselperik.erasmusinfo.models.News;
import com.wesselperik.erasmusinfo.tasks.NewsTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity implements NewsDetailCallback {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingtoolbar) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.toolbar_image) ImageView toolbarImage;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarLayout.setTitle(" ");

        getFragmentManager().beginTransaction().replace(R.id.container, new DetailFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.nieuws_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                News newsItem = getIntent().getExtras().getParcelable("news_item");

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, newsItem.getTitle() + "\n" + "https://het-erasmus.nl/" + newsItem.getUrl());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Artikel delen via..."));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNewsImageLoaded(String image) {
        Picasso.with(this).load(image).into(toolbarImage);
    }

    @Override
    public void onNewsImageLoadingFailed() {
        imageProgressBar.setVisibility(View.GONE);
    }

    public static class DetailFragment extends Fragment implements NewsCallback {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.category) TextView category;
        @BindView(R.id.header) TextView header;
        @BindView(R.id.text) TextView text;
        @BindView(R.id.progressBarLayout) LinearLayout progressBarLayout;

        NewsDetailCallback callback;

        public DetailFragment() {
            // empty constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ButterKnife.bind(this, rootView);

            News newsItem = getActivity().getIntent().getExtras().getParcelable("news_item");

            title.setText(newsItem.getTitle());
            date.setText(newsItem.getDate());
            category.setText(newsItem.getCategory());
            header.setText(newsItem.getShortText());
            text.setText(newsItem.getText());

            new NewsTask(this).execute(newsItem.getUrl());

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            callback = (NewsDetailCallback) context;
        }

        @Override
        public void onNewsLoaded(final ArrayList<News> items) {
            Log.d("DetailFragment", "onNewsLoaded: " + items.size() + " items loaded.");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    News newsItem = items.get(0);
                    title.setText(newsItem.getTitle());
                    date.setText(newsItem.getDate());
                    category.setText(newsItem.getCategory());
                    header.setText(newsItem.getShortText());
                    text.setText(newsItem.getText());
                    progressBarLayout.setVisibility(View.GONE);
                    callback.onNewsImageLoaded(newsItem.getImage());
                }
            });
        }

        @Override
        public void onNewsLoadingFailed() {
            Log.d("DetailFragment", "onNewsLoadingFailed");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarLayout.setVisibility(View.GONE);
                    callback.onNewsImageLoadingFailed();
                }
            });
        }
    }
}
