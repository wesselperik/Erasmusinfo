package com.wesselperik.erasmusinfo.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

import com.wesselperik.erasmusinfo.R;

/**
 * Created by Wessel on 25-1-2017.
 */

public abstract class Tools {

    public static boolean isConnected(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
