package com.wesselperik.erasmusinfo.activities;

/**
 * Created by Wessel on 5-11-2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wesselperik.erasmusinfo.MainActivity2;
import com.wesselperik.erasmusinfo.R;

public class NieuwsDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nieuwsdetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("nieuws_titel"));

        getFragmentManager().beginTransaction().replace(R.id.container, new DetailFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.nieuws_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_share:
                String title = getIntent().getExtras().getString("nieuws_titel");
                String text = getIntent().getExtras().getString("nieuws_tekst");

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + text);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Artikel delen via..."));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DetailFragment extends Fragment {

        public static DetailFragment newInstance() {
            DetailFragment fragment = new DetailFragment();
            return fragment;
        }

        public DetailFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Context context = getActivity().getApplication().getApplicationContext();

            /*android.app.ActionBar ab = getActivity().getActionBar();
            ab.setTitle("Instellingen");
            ab.setSubtitle("");*/


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_nieuwsdetail, container,
                    false);

            //FragmentActivity activity = (FragmentActivity) rootView.getContext();

            String title = getActivity().getIntent().getExtras().getString("nieuws_titel");
            String date = getActivity().getIntent().getExtras().getString("nieuws_datum");
            String school = getActivity().getIntent().getExtras().getString("nieuws_categorie");
            String text = getActivity().getIntent().getExtras().getString("nieuws_tekst");
            String headertext = getActivity().getIntent().getExtras().getString("nieuws_header");

            TextView titel = (TextView) rootView.findViewById(R.id.title);
            TextView datum = (TextView) rootView.findViewById(R.id.date);
            TextView categorie = (TextView) rootView.findViewById(R.id.category);
            TextView header = (TextView) rootView.findViewById(R.id.header);
            TextView tekst = (TextView) rootView.findViewById(R.id.text);

            titel.setText(title);
            datum.setText(date + " - ");
            switch(school)
            {
                case "HAVO/VWO":
                    categorie.setText("HAVO/VWO");
                    break;
                case "VMBO":
                    categorie.setText("VMBO");
                    break;
                case "PRO":
                    categorie.setText("PrO");
                    break;
                case "HET ERASMUS":
                    categorie.setText("Het Erasmus");
                    break;
                default:
                    categorie.setText(school);
                    break;
            }
            header.setText(Html.fromHtml(headertext));
            header.setMovementMethod(LinkMovementMethod.getInstance());
            tekst.setText(Html.fromHtml(text));
            tekst.setMovementMethod(LinkMovementMethod.getInstance());

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setClickable(true);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

    }

    static void restartMain(Activity activity){
        activity.startActivity(new Intent(activity, MainActivity2.class));
    }
}
