package com.wesselperik.erasmusinfo.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wesselperik.erasmusinfo.services.WearService;

/**
 * Created by Wessel on 2-12-2015.
 */
public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Intent intent = new Intent(arg0,WearService.class);
        arg0.startService(intent);
    }
}