package com.wesselperik.erasmusinfo.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wesselperik on 14/12/2017.
 */

public class PostHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.text) TextView text;

    public PostHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindViews(String title, String text) {
        this.title.setText(title);
        this.text.setText(text);
    }
}
