package com.wesselperik.erasmusinfo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.adapters.NieuwsAdapter;
import com.wesselperik.erasmusinfo.classes.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wessel on 25-1-2017.
 */

public class Nieuws extends Fragment {

    private final String TAG_NIEUWS = "nieuws";
    private final String TAG_ID = "id";
    private final String TAG_TITLE = "title";
    private final String TAG_DATE = "date";
    private final String TAG_CATEGORY = "category";
    private final String TAG_HEADER = "header";
    private final String TAG_TEXT = "text";
    private final String TAG_IMAGE = "image";

    public static final int CONNECTION_TIMEOUT = 60000;
    public static final int READ_TIMEOUT = 90000;

    private static View view;
    private RecyclerView mRecyclerView;
    private NieuwsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private List<com.wesselperik.erasmusinfo.models.Nieuws> mNieuwsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public Nieuws() {
    }

    public static Nieuws newInstance() {
        Nieuws fragment = new Nieuws();
        return fragment;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mRecyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(TAG_NIEUWS, (ArrayList) mNieuwsList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_nieuws, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mDividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!Tools.isConnected(getActivity().getApplicationContext())) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Snackbar snackbar = Snackbar
                            .make(container, getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    //Refreshing data on server
                    new GetNieuws().execute();
                }
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(TAG_NIEUWS)) {
            // Get mededelingen from saved instance state
            mNieuwsList = savedInstanceState.getParcelableArrayList(TAG_NIEUWS);
        } else {
            // Initialize mededelingen list
            mNieuwsList = new ArrayList<>();

            if (Tools.isConnected(getActivity().getApplicationContext())) {
                // User has an internet connection, load the data
                new GetNieuws().execute();
            } else {
                // User has no internet connection, show error dialog
                mSwipeRefreshLayout.setRefreshing(false);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Snackbar snackbar = Snackbar
                                .make(container, getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        }

        return view;
    }

    private class GetNieuws extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("https://api.erasmusinfo.nl/nieuws/");
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
                return mue.toString();
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
            } catch (IOException ioe) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Snackbar snackbar = Snackbar
                                .make((ViewGroup) view.getParent(), getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
                ioe.printStackTrace();
                cancel(true);
                return null;
            }

            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return ("unsuccessful");
                }

            } catch (IOException ioe) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Snackbar snackbar = Snackbar
                                .make((ViewGroup) view.getParent(), getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
                ioe.printStackTrace();
                cancel(true);
                return null;
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mNieuwsList = new ArrayList<>();

            try {
                // Get JSON array with nieuws
                JSONArray jsonArr = new JSONArray(result);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject c = jsonArr.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String title = c.getString(TAG_TITLE);
                    String date = c.getString(TAG_DATE);
                    String category = c.getString(TAG_CATEGORY);
                    String header = Html.fromHtml(c.getString(TAG_HEADER)).toString();
                    String text = Html.fromHtml(c.getString(TAG_TEXT).replaceAll("<img.+?>", "").replaceAll("(\r\n|\n)", "<br />")).toString();
                    String image = c.getString(TAG_IMAGE);

                    com.wesselperik.erasmusinfo.models.Nieuws nieuws = new com.wesselperik.erasmusinfo.models.Nieuws(Integer.parseInt(id), title, date, category, header, text, image);

                    // Add mededeling to list
                    mNieuwsList.add(nieuws);
                }

                // Setup and hand over data to recyclerview
                mAdapter = new NieuwsAdapter(getActivity().getApplicationContext(), mNieuwsList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);

            } catch (JSONException je) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Snackbar snackbar = Snackbar
                                .make((ViewGroup) view.getParent(), getResources().getString(R.string.error_unknown), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        }

    }
}

