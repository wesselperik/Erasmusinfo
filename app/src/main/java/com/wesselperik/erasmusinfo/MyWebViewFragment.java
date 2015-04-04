package com.wesselperik.erasmusinfo;

/**
 * Created by Wessel on 17-1-2015.
 */
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MyWebViewFragment extends Fragment {

    ProgressDialog mProgress;
    WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_fragment, container,
                false);

        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        mWebView = (WebView) rootView.findViewById(R.id.webview1);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        mProgress = ProgressDialog.show(getActivity(), "Laden",
                "Even geduld...");

        mWebView.loadUrl(url);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setAppCachePath("/data/data/nl.wesselperik.erasmusinfo/cache");
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheMaxSize(1024*1024*8);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        if (android.os.Build.VERSION.SDK_INT >= 16)
        {
            mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        }


        Activity activity = getActivity();

        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // do nothing
        } else {
            mWebView.getSettings().setCacheMode(1);
        }

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

                String html = "<html><body><h2>Pagina niet beschikbaar</h2><p>Het lijkt erop dat je geen internetconnectie hebt. Probeer het later opnieuw!<p><br><p><b>Tip:</b><br>Laad pagina's als je een connectie hebt, als je daarna offline gaat zal hij deze uit de cache laden.</p></body></html>";
                String mime = "text/html";
                String encoding = "utf-8";

                mWebView.loadDataWithBaseURL(null, html, mime, encoding, null);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }
            }

        });

        return rootView;
    }

}