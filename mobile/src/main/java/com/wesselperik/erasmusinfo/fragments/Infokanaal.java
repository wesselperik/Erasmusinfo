package com.wesselperik.erasmusinfo.fragments;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.adapters.MededelingAdapter;
import com.wesselperik.erasmusinfo.classes.Tools;
import com.wesselperik.erasmusinfo.models.Mededeling;

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

public class Infokanaal extends Fragment {

    private final String TAG_MEDEDELINGEN = "posts";
    private final String TAG_ID = "id";
    private final String TAG_TITLE = "title";
    private final String TAG_TEXT = "content";

    public static final int CONNECTION_TIMEOUT = 60000;
    public static final int READ_TIMEOUT = 90000;
    private static View view;
    private RecyclerView mRecyclerView;
    private MededelingAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private List<Mededeling> mMededelingenList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public Infokanaal() {
    }

    public static Infokanaal newInstance() {
        Infokanaal fragment = new Infokanaal();
        return fragment;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mRecyclerView);
    }

    public void refresh() {
        new GetMededelingen().execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(TAG_MEDEDELINGEN, (ArrayList) mMededelingenList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_infokanaal, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mDividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.IndigoThemeAccent, R.color.IndigoThemeAccent2, R.color.IndigoThemeAccent3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!Tools.isConnected(getActivity().getApplicationContext())) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Snackbar snackbar = Snackbar
                            .make(container, getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Log.e("Infokanaal", "No internet connection!");
                } else {
                    //Refreshing data on server
                    new GetMededelingen().execute();
                }
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(TAG_MEDEDELINGEN)) {
            // Get mededelingen from saved instance state
            mMededelingenList = savedInstanceState.getParcelableArrayList(TAG_MEDEDELINGEN);
        } else {
            // Initialize mededelingen list
            mMededelingenList = new ArrayList<>();

            if (Tools.isConnected(getActivity().getApplicationContext())) {
                // User has an internet connection, load the data
                new GetMededelingen().execute();
            } else {
                // User has no internet connection, show error dialog
                mSwipeRefreshLayout.setRefreshing(false);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Snackbar snackbar = Snackbar
                                .make(container, getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Log.e("Infokanaal", "No internet connection!");
                    }
                });
            }
        }

        return view;
    }

    private class GetMededelingen extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            String schoolName = prefs.getString("settings_schoolname", "havovwo");

            try {
                url = new URL("https://api.erasmusinfo.nl/v3/?location=" + schoolName);
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
                        Log.e("Infokanaal", "No internet connection!");
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
                    Log.e("Infokanaal", "Invalid response code: " + response_code);
                    return ("unsuccessful");
                }

            } catch (IOException ioe) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Snackbar snackbar = Snackbar
                                .make((ViewGroup) view.getParent(), getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Log.e("Infokanaal", "No internet connection!");
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
            mMededelingenList = new ArrayList<>();

            Log.d("Result", result);

            try {
                JSONObject jsonObj = new JSONObject(result);

                // Get JSON array with mededelingen
                JSONArray mededelingen = jsonObj.getJSONArray(TAG_MEDEDELINGEN);

                Log.d("Mededelingen", "Length: " + mededelingen.length());

                for (int i = 0; i < mededelingen.length(); i++) {
                    JSONObject c = mededelingen.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String title = c.getString(TAG_TITLE);
                    String text = Html.fromHtml(c.getString(TAG_TEXT)).toString();

                    Mededeling mededeling = new Mededeling(Integer.parseInt(id), title, text);

                    // Add mededeling to list
                    mMededelingenList.add(mededeling);
                }

                // Setup and hand over data to recyclerview
                mAdapter = new MededelingAdapter(getActivity().getApplicationContext(), mMededelingenList);
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
                Log.e("Infokanaal", "JSON exception: " + je.getMessage());
            }
        }

    }
}
