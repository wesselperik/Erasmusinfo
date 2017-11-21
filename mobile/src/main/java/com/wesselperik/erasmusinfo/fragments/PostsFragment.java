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
import com.wesselperik.erasmusinfo.adapters.PostsAdapter;
import com.wesselperik.erasmusinfo.classes.Constants;
import com.wesselperik.erasmusinfo.models.Post;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wessel on 20-11-2017.
 */

public class PostsFragment extends Fragment {

    private View view;
    private RequestQueue requestQueue;
    private Gson gson;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private PostsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private List<Post> mPostsList;

    public PostsFragment() {
    }

    public static PostsFragment newInstance() {
        PostsFragment fragment = new PostsFragment();
        return fragment;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mRecyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Constants.POSTS, (ArrayList) mPostsList);
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
                fetchPosts();
            }
        });

        mPostsList = new ArrayList<>();
        mAdapter = new PostsAdapter(getActivity().getApplicationContext(), mPostsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mProgressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.POSTS)) {
            // Get posts from saved instance state
            mPostsList = savedInstanceState.getParcelableArrayList(Constants.POSTS);
        } else {
            // Initialize posts list
            mPostsList = new ArrayList<>();
            fetchPosts();
        }

        return view;
    }

    private void fetchPosts() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String location = prefs.getString("settings_schoolname", "havovwo");
        StringRequest request = new StringRequest(Request.Method.GET, Constants.API_URL + "?" + Constants.API_PARAMETER_LOCATION + "=" + location, onPostsLoaded, onPostsError);
        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
        JsonParser parser = new JsonParser();
            JsonObject element = (JsonObject) parser.parse(response);

            JsonArray postsArray = element.getAsJsonArray(Constants.POSTS);
            Type listType = new TypeToken<List<Post>>(){}.getType();
            List<Post> posts = (List<Post>) gson.fromJson(postsArray, listType);

            Log.i("PostsFragment", posts.size() + " posts loaded.");
            mPostsList = new ArrayList<>();
            for (Post post : posts) {
                Log.i("PostsFragment", post.ID + ": " + post.title);
                mPostsList.add(post);
            }

            mAdapter = new PostsAdapter(getActivity().getApplicationContext(), mPostsList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostsFragment", error.toString());

            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
}
