package com.wesselperik.erasmusinfo.interfaces;

import com.wesselperik.erasmusinfo.models.News;

import java.util.ArrayList;

/**
 * Created by wesselperik on 15/12/2017.
 */

public interface NewsCallback {
    void onNewsLoaded(ArrayList<News> items);
    void onNewsLoadingFailed();
}
