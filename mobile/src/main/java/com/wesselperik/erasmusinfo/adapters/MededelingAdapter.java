package com.wesselperik.erasmusinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.models.Mededeling;

import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 25-1-2017.
 */

public class MededelingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Mededeling> data = Collections.emptyList();
    Mededeling current;
    int currentPos=0;

    // create constructor to initialize context and data sent from fragment
    public MededelingAdapter(Context context, List<Mededeling> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent,false);
        MededelingHolder holder = new MededelingHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MededelingHolder mededelingHolder = (MededelingHolder) holder;
        Mededeling current = data.get(position);
        mededelingHolder.title.setText(current.getTitle());
        mededelingHolder.text.setText(current.getText());
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MededelingHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView text;

        // create constructor to get widget reference
        public MededelingHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
