package com.wesselperik.erasmusinfo.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.wesselperik.erasmusinfo.adapters.PostAdapter;
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
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.error_card) CardView mErrorCard;
    @BindView(R.id.error_text) TextView mErrorText;
    @BindView(R.id.error_image) ImageView mErrorImage;

    private PostAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Post> mPostsList;

    public PostsFragment() {
        // empty constructor
    }

    public static PostsFragment newInstance() {
        return new PostsFragment();
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

        mPostsList = new ArrayList<>();
        mAdapter = new PostAdapter(getActivity().getApplicationContext(), mPostsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorCard.setVisibility(View.INVISIBLE);

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
        Log.d("fetchPosts", "Fetching posts from " + Constants.API_URL + "?" + Constants.API_PARAMETER_LOCATION + "=" + location);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.API_URL + "?" + Constants.API_PARAMETER_LOCATION + "=" + location, onPostsLoaded, onPostsError);
        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void setErrorView(String error, int drawable) {
        mErrorText.setText(error);
        mErrorImage.setImageDrawable(getResources().getDrawable(drawable));
        mErrorCard.setVisibility(View.VISIBLE);
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

            if (posts.size() > 0) {
                mPostsList = new ArrayList<>();
                for (Post post : posts) {
                    mPostsList.add(post);
                }

                mAdapter = new PostAdapter(getActivity().getApplicationContext(), mPostsList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
            } else {
                setErrorView(getString(R.string.error_no_posts), R.drawable.ic_error_posts);
            }
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            setErrorView(getString(R.string.error_no_internet), R.drawable.ic_error);
            Log.e("PostsFragment", error.toString());
        }
    };
}
