package com.wesselperik.erasmusinfo.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.models.ChangeItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wesselperik on 21/11/2017.
 */

public class ChangeItemHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvClass) TextView tvClass;
    @BindView(R.id.tvHour) TextView tvHour;
    @BindView(R.id.tvTeacher) TextView tvTeacher;
    @BindView(R.id.tvComment) TextView tvComment;
    private Context context;

    public ChangeItemHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void bindViews(ChangeItem item) {
        tvClass.setText(item.getItemClass());
        tvHour.setText(item.getItemHour());
        tvTeacher.setText(item.getItemTeacher());
        tvComment.setText(item.getItemComment());
    }
}