package com.wesselperik.erasmusinfo.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Wessel on 23-1-2017.
 */

public class FCMInitializationService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMInitService";

    @Override
    public void onTokenRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String fcmToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "FCM Device Token:" + fcmToken);

        //Save FCM registration token
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("device_token", fcmToken);
        edit.apply();
    }
}
