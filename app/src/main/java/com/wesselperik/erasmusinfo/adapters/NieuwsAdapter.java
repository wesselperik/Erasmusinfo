package com.wesselperik.erasmusinfo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.activities.NieuwsDetailActivity;
import com.wesselperik.erasmusinfo.models.Mededeling;
import com.wesselperik.erasmusinfo.models.Nieuws;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Wessel on 25-1-2017.
 */

public class NieuwsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Nieuws> data = Collections.emptyList();
    Nieuws current;
    int currentPos=0;

    // create constructor to initialize context and data sent from fragment
    public NieuwsAdapter(Context context, List<Nieuws> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_nieuws, parent,false);
        NieuwsHolder holder = new NieuwsHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        NieuwsHolder nieuwsHolder = (NieuwsHolder) holder;
        final Nieuws current = data.get(position);
        nieuwsHolder.title.setText(current.getTitle());
        nieuwsHolder.date.setText(current.getDate());
        nieuwsHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NieuwsDetailActivity.class);
                i.putExtra("nieuws_titel", current.getTitle());
                i.putExtra("nieuws_datum", current.getDate());
                i.putExtra("nieuws_categorie", current.getCategory());
                i.putExtra("nieuws_header", current.getHeader());
                i.putExtra("nieuws_tekst", current.getText());
                i.putExtra("nieuws_afbeelding", current.getImage());
                context.startActivity(i);
            }
        });
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class NieuwsHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView date;

        // create constructor to get widget reference
        public NieuwsHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
