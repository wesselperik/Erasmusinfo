package com.wesselperik.erasmusinfo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.classes.Tools;
import com.wesselperik.erasmusinfo.views.TextViewBold;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wessel on 16-3-2017.
 */

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingtoolbar) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.toolbar_content_title) TextViewBold toolbarContentTitle;
    @BindView(R.id.editText) EditText editText;
    @BindView(R.id.btnSendFeedback) Button btnSendFeedback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
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
        toolbarContentTitle.setText("feedback");

        btnSendFeedback.setOnClickListener(this);
    }

    public void sendEmail(String body) {
        StringBuilder finalBody = new StringBuilder(body);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Erasmusinfo feedback");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@wesselperik.com"});
        emailIntent.putExtra(Intent.EXTRA_TEXT, finalBody.toString());

        startActivity(Tools.createEmailChooserIntent(this, emailIntent, getString(R.string.intent_feedback_header)));
    }


    @Override
    public void onClick(View view) {
        String feedback = editText.getText().toString();
        if (feedback.trim().length() > 0) {
            sendEmail(feedback);
        } else {
            editText.setError(getString(R.string.error_no_feedback_provided));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
