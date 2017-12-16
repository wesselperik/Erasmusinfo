package com.wesselperik.erasmusinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.holders.NewsHolder;
import com.wesselperik.erasmusinfo.models.News;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 20-11-2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private List<News> data = Collections.emptyList();
    private Context context;

    // create constructor to initialize context and data sent from fragment
    public NewsAdapter(Context context, List<News> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_news, parent,false);
        return new NewsHolder(view, context);
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        NewsHolder newsHolder = (NewsHolder) holder;
        News current = data.get(position);
        newsHolder.bindViews(current.getTitle(), current.getImage());
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }
}
