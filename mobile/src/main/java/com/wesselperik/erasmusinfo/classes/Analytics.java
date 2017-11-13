package com.wesselperik.erasmusinfo.classes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Wessel on 24-1-2017.
 */

public class Analytics {

    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean isInitialized = false;
    private Context context;

    public Analytics(Context context) {
        this.context = context;
    }

    public void init() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        isInitialized = true;
    }

    public void logEvent(String eventName, Bundle bundle) {
        if (isInitialized) mFirebaseAnalytics.logEvent(eventName, bundle);
    }
}
