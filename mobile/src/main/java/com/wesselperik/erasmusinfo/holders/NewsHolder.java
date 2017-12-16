package com.wesselperik.erasmusinfo.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wesselperik.erasmusinfo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wesselperik on 14/12/2017.
 */

public class NewsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.image) ImageView image;

    private Context context;

    public NewsHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.context = context;
    }

    public void bindViews(String title, String image) {
        this.title.setText(title);
        Picasso.with(context).load(image).into(this.image);
    }
}
