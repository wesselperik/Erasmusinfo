package com.wesselperik.erasmusinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.holders.ChangeItemHolder;
import com.wesselperik.erasmusinfo.models.ChangeItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by wesselperik on 21/11/2017.
 */

public class ChangeItemAdapter extends RecyclerView.Adapter<ChangeItemHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<ChangeItem> data = Collections.emptyList();

    public ChangeItemAdapter(Context context, List<ChangeItem> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ChangeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_change, parent, false);
        return new ChangeItemHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ChangeItemHolder holder, int position) {
        ChangeItem current = data.get(position);

        holder.bindViews(current);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}