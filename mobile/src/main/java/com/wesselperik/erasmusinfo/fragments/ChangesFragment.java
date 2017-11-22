package com.wesselperik.erasmusinfo.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.adapters.ChangeAdapter;
import com.wesselperik.erasmusinfo.classes.Constants;
import com.wesselperik.erasmusinfo.models.Change;
import com.wesselperik.erasmusinfo.models.ChangeItem;
import com.wesselperik.erasmusinfo.models.Post;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wessel on 20-11-2017.
 */

public class ChangesFragment extends Fragment {

    private View view;
    private RequestQueue requestQueue;
    private Gson gson;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private ChangeAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private List<Change> mChangesList;

    public ChangesFragment() {
    }

    public static ChangesFragment newInstance() {
        ChangesFragment fragment = new ChangesFragment();
        return fragment;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mRecyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Constants.CHANGES, (ArrayList) mChangesList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        view = inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mDividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(), mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.IndigoThemeAccent, R.color.IndigoThemeAccent2, R.color.IndigoThemeAccent3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchChanges();
            }
        });

        mChangesList = new ArrayList<>();
        mAdapter = new ChangeAdapter(getActivity().getApplicationContext(), mChangesList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mProgressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.CHANGES)) {
            // Get changes from saved instance state
            mChangesList = savedInstanceState.getParcelableArrayList(Constants.CHANGES);
        } else {
            // Initialize changes list
            mChangesList = new ArrayList<>();
            fetchChanges();
        }

        return view;
    }

    private void fetchChanges() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String location = prefs.getString("settings_schoolname", "havovwo");
        StringRequest request = new StringRequest(Request.Method.GET, Constants.API_URL + "?" + Constants.API_PARAMETER_LOCATION + "=" + location, onChangesLoaded, onChangesError);
        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private final Response.Listener<String> onChangesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
        JsonParser parser = new JsonParser();
            JsonObject element = (JsonObject) parser.parse(response);

            JsonArray changesArray = element.getAsJsonArray(Constants.CHANGES);
            Type listType = new TypeToken<List<Change>>(){}.getType();
            List<Change> changes = (List<Change>) gson.fromJson(changesArray, listType);

            Log.i("ChangesFragment", changes.size() + " changes loaded.");
            mChangesList = new ArrayList<>();
            for (Change change : changes) {
                Log.i("ChangesFragment", change.ID + ": " + change.title);
                Log.d("ChangesFragment", change.getChanges().size() + " change items loaded.");
                for (ChangeItem item : change.getChanges()) {
                    Log.d("ChangesFragment", "Item: " + item.getItemClass() + " - " + item.getItemHour() + " - " + item.getItemTeacher() + " - " + item.getItemComment());
                }

//                JsonArray changeItemsArray = change.getAsJsonArray(Constants.CHANGE_ITEMS);
//                Type listType = new TypeToken<List<Change>>(){}.getType();
//                List<Change> changes = (List<Change>) gson.fromJson(changesArray, listType);


                mChangesList.add(change);
            }

            mAdapter = new ChangeAdapter(getActivity().getApplicationContext(), mChangesList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private final Response.ErrorListener onChangesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("ChangesFragment", error.toString());

            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
}
