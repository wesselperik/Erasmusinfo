package com.wesselperik.erasmusinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.holders.ChangeHolder;
import com.wesselperik.erasmusinfo.models.Change;

import java.util.Collections;
import java.util.List;

/**
 * Created by wesselperik on 21/11/2017.
 */

public class ChangeAdapter extends RecyclerView.Adapter<ChangeHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Change> data = Collections.emptyList();

    public ChangeAdapter(Context context, List<Change> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ChangeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_changes, parent, false);
        return new ChangeHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ChangeHolder holder, int position) {
        Change current = data.get(position);
        ChangeItemAdapter adapter = new ChangeItemAdapter(context, current.getChanges());

        holder.bindViews(current.getTitle(), adapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}