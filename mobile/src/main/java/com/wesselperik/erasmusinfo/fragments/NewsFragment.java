package com.wesselperik.erasmusinfo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.adapters.NewsAdapter;
import com.wesselperik.erasmusinfo.classes.Constants;
import com.wesselperik.erasmusinfo.models.News;
import com.wesselperik.erasmusinfo.tasks.NewsTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wessel on 20-11-2017.
 */

public class NewsFragment extends Fragment implements NewsAdapter.OnNewsLoaded {

    private View view;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private NewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<News> mNewsList;

    public NewsFragment() {
        // empty constructor
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //registerForContextMenu(mRecyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Constants.NEWS, (ArrayList) mNewsList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mNewsList = new ArrayList<>();
        mAdapter = new NewsAdapter(getActivity().getApplicationContext(), mNewsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mProgressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.NEWS)) {
            // Get news from saved instance state
            mNewsList = savedInstanceState.getParcelableArrayList(Constants.NEWS);
        } else {
            // Initialize news list
            mNewsList = new ArrayList<>();
            new NewsTask(this).execute();
        }

        return view;
    }

    @Override
    public void onNewsLoaded(ArrayList<News> items) {
        Log.d("NewsFragment", "onNewsLoaded: " + items.size() + " items loaded.");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new NewsAdapter(getActivity().getApplicationContext(), mNewsList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
