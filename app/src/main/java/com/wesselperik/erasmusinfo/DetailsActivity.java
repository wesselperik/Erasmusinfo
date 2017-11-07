package com.wesselperik.erasmusinfo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.HashMap;

/**
 * Created by Wessel on 14-9-2015.
 */
public class DetailsActivity extends ActionBarActivity {
    public final static HashMap<String, CharSequence> LES = null;
    public final static HashMap<String, CharSequence> VAK = null;
    public final static HashMap<String, CharSequence> DOCENT = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
