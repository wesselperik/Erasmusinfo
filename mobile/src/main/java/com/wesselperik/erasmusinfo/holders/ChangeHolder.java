package com.wesselperik.erasmusinfo.holders;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wesselperik on 21/11/2017.
 */

public class ChangeHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title) TextView tvTitle;
    @BindView(R.id.recyclerView) RecyclerView rvChanges;
    private Context context;

    public ChangeHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void bindViews(String title, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        tvTitle.setText(title);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvChanges.setLayoutManager(linearLayoutManager);
        ViewCompat.setNestedScrollingEnabled(rvChanges, false);
        rvChanges.setAdapter(adapter);
    }
}