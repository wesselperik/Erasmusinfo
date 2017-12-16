package com.wesselperik.erasmusinfo.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Parcelable;

import com.wesselperik.erasmusinfo.R;

import java.util.List;
import java.util.Stack;

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

    public static Intent createEmailChooserIntent(Context context, Intent source, CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "info@wesselperik.com", null));
        List<ResolveInfo> activities = context.getPackageManager()
                .queryIntentActivities(i, 0);

        for(ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }

        if(!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));

            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }
}
