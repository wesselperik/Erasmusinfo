package com.wesselperik.erasmusinfo;

import android.app.Application;
import android.content.Context;

/**
 * Created by Wessel on 22-4-2016.
 */
public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
}
