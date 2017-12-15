package com.wesselperik.erasmusinfo.interfaces;

/**
 * Created by wesselperik on 15/12/2017.
 */

public interface NewsDetailCallback {
    void onNewsImageLoaded(String image);
    void onNewsImageLoadingFailed();
}
