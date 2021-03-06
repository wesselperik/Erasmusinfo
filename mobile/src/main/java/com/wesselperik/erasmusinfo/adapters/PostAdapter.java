package com.wesselperik.erasmusinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.holders.PostHolder;
import com.wesselperik.erasmusinfo.models.Post;

import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 20-11-2017.
 */

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    List<Post> data = Collections.emptyList();

    // create constructor to initialize context and data sent from fragment
    public PostAdapter(Context context, List<Post> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_post, parent,false);
        return new PostHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        PostHolder postHolder = (PostHolder) holder;
        Post current = data.get(position);
        postHolder.bindViews(current.getTitle(), Html.fromHtml(current.getContent()).toString());
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }
}
