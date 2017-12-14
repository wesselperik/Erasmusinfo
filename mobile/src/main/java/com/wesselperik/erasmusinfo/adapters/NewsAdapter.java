package com.wesselperik.erasmusinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.models.News;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 20-11-2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<News> data = Collections.emptyList();

    // create constructor to initialize context and data sent from fragment
    public NewsAdapter(Context context, List<News> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_news, parent,false);
        return new NewsHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        NewsHolder postHolder = (NewsHolder) holder;
        News current = data.get(position);
        postHolder.title.setText(current.getTitle());
        postHolder.shortText.setText(current.getShortText());
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class NewsHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView shortText;

        // create constructor to get widget reference
        public NewsHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            shortText = (TextView) itemView.findViewById(R.id.text);
        }
    }

    public interface NewsCallback {
        void onNewsLoaded(ArrayList<News> items);
        void onNewsLoadingFailed();
    }
}
