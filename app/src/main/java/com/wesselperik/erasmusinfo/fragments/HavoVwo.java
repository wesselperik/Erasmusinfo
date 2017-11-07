package com.wesselperik.erasmusinfo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.wesselperik.erasmusinfo.MainActivity;
import com.wesselperik.erasmusinfo.R;
import com.wesselperik.erasmusinfo.ServiceHandler;
import com.wesselperik.erasmusinfo.SlidingTabLayout;

/**
 * Created by Wessel on 18-9-2015.
 */
public class HavoVwo extends Fragment {

    private static String havovwo_url = "http://api.erasmusinfo.nl/roosterwijzigingen/havo-vwo/";

    private static final String TAG_MEDEDELINGEN = "mededelingen";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "titel";
    private static final String TAG_TEXT = "mededeling";

    public static Handler UIHandler = new Handler(Looper.getMainLooper());

    // ------------------------------------------------------------ //
    // ------------------- Main HAVO/VWO Fragment ----------------- //
    // ------------------------------------------------------------ //

    public static class MainFragment extends Fragment {

        public static MainFragment newInstance() {
            MainFragment fragment = new MainFragment();
            return fragment;

        }

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_havovwomain, container,
                    false);

            FragmentActivity activity = (FragmentActivity)rootView.getContext();

            // Initialize the ViewPager and set an adapter
            ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
            pager.setAdapter(new PagerAdapter(activity.getSupportFragmentManager()));

            // Bind the tabs to the ViewPager
            SlidingTabLayout tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);

            ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
            ab.setTitle("HAVO/VWO");
            ab.setSubtitle("");
            ab.setElevation(0);

            return rootView;
        }

        @Override
        public void onPause() {
            super.onPause();
            getActivity().getSupportFragmentManager().beginTransaction().remove(MainFragment.newInstance()).commit();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setBackgroundColor(Color.WHITE);
            getView().setClickable(true);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //((MainActivity) activity).onSectionAttached(1);
        }
    }


    public static class MededelingenFragment extends Fragment {

        private ListView listView;
        private View view;
        private SwipeRefreshLayout mSwipeRefreshLayout = null;
        JSONArray mededelingen = null;
        ArrayList<HashMap<String, CharSequence>> mededelingList = new ArrayList<HashMap<String, CharSequence>>();

        public static MededelingenFragment newInstance() {
            MededelingenFragment fragment = new MededelingenFragment();
            return fragment;
        }

        public MededelingenFragment() {
        }

        public void onActivityCreated(Bundle savedState) {
            super.onActivityCreated(savedState);
            registerForContextMenu(listView);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.mededeling, menu);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.share:
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    int index = info.position;
                    HashMap<String, CharSequence> mededeling = mededelingList.get(index);
                    CharSequence title = mededeling.get(TAG_TITLE);
                    CharSequence text = mededeling.get(TAG_TEXT);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + text);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Verzenden via..."));
                    return true;
            }
            return super.onContextItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if(!isNetworkAvailable(getActivity().getApplicationContext())) {
                Toast.makeText(getActivity().getApplicationContext(), "Geen internetverbinding", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

            if (view == null) {
                view = inflater.inflate(R.layout.fragment_havovwo, container, false);
                listView = (ListView) view.findViewById(R.id.listView);
                if(isNetworkAvailable(getActivity().getApplicationContext())) {
                    new GetMededelingen().execute();
                }
                view.getBackground().setAlpha(255);

                mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(!isNetworkAvailable(getActivity().getApplicationContext())) {
                            Toast.makeText(getActivity().getApplicationContext(),"Geen internetverbinding", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }else {
                            //Refreshing data on server
                            new GetMededelingen().execute();
                            mededelingList.clear();
                            Log.i("SwipeRefresh", "Refreshing data...");
                        }
                    }
                });

                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        listView.setScrollContainer(false);
                    }
                });
            } else {
//                ViewGroup parent = (ViewGroup) view.getParent();
//                parent.removeView(view);
            }
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mSwipeRefreshLayout.isRefreshing() && this.getView() != null) {
                ((ViewGroup) this.getView()).removeAllViews();
                this.getView().getBackground().setAlpha(0);
            }
        }

        private class GetMededelingen extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // ServiceHandler
                ServiceHandler sh = new ServiceHandler();

                // Request maken naar server
                String jsonStr = sh.makeServiceCall(havovwo_url, ServiceHandler.GET);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON array ophalen
                        mededelingen = jsonObj.getJSONArray(TAG_MEDEDELINGEN);

                        for (int i = 0; i < mededelingen.length(); i++) {
                            JSONObject c = mededelingen.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String title = c.getString(TAG_TITLE);
                            CharSequence text = Html.fromHtml(c.getString(TAG_TEXT));

                            HashMap<String, CharSequence> mededeling = new HashMap<String, CharSequence>();

                            mededeling.put(TAG_ID, id);
                            mededeling.put(TAG_TITLE, title);
                            mededeling.put(TAG_TEXT, text);

                            // Mededeling aan list toevoegen
                            mededelingList.add(mededeling);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Kon geen data van de Erasmusinfo API ophalen!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UIHandler.post(new Runnable() {
                    public void run() {
                        if(isNetworkAvailable(getActivity().getApplicationContext())) {

                            ListAdapter adapter = null;

                            if (getActivity() != null) {
                                adapter = new SimpleAdapter(getActivity(), mededelingList, R.layout.list_item, new String[]{TAG_TITLE, TAG_TEXT}, new int[]{R.id.title, R.id.text});
                            }

                            // Dismiss progress-spinner
                            if (mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                listView.setScrollContainer(true);
                            }

                            // Adapter

                            listView.setAdapter(adapter);
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"Geen internetverbinding", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    }
                });
            }

        }


    }

    public static class ContactFragment extends Fragment {

        private ListView listView;
        ArrayList<HashMap<String, CharSequence>> contactList = new ArrayList<HashMap<String, CharSequence>>();

        private final String[] NAMEN = {"mw. M. Brokelman", "mw. S. ten Heggeler", "mw. M. Eshuis", "dhr. S. van Binsbergen", "dhr. P. Vaanhold", "mw. J. In 't Veld", "dhr. J. Boerkamp", "dhr. W. Veenvliet"};
        private final String[] FUNCTIES = {"Coördinatie brugklassen", "Coördinatie VWO 2", "Coördinatie VWO 3 t/m 4", "Coördinatie HAVO 2 t/m 3", "Coördinatie HAVO 4", "Teamleider VWO 3 t/m 6", "Teamleider HAVO 4 t/m 5", "Vestigingsdirecteur HAVO/VWO"};
        private final String[] EMAILS = {"m.brokelman@osg-erasmus.nl", "s.t.heggeler@osg-erasmus.nl", "m.eshuis@osg-erasmus.nl", "s.v.binsbergen@osg-erasmus.nl", "p.vaanhold@osg-erasmus.nl", "j.veld@osg-erasmus.nl", "j.boerkamp@osg-erasmus.nl", "w.veenvliet@osg-erasmus.nl"};

        public static ContactFragment newInstance() {
            ContactFragment fragment = new ContactFragment();
            return fragment;
        }

        public ContactFragment() {
        }

        public String getNaam(int position) {
            return NAMEN[position];
        }

        public String getFunctie(int position) {
            return FUNCTIES[position];
        }

        public String getEmail(int position) {
            return EMAILS[position];
        }

        public int getCount() {
            return NAMEN.length;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contact, container,
                    false);

            listView = (ListView) rootView.findViewById(R.id.listView);

            TextView Titel = (TextView) rootView.findViewById(R.id.title);
            Titel.setText("Contactgegevens");

            TextView Postadres = (TextView) rootView.findViewById(R.id.postadres);
            Postadres.setText("Postadres:\n" +
                    "Postbus 341\n" +
                    "7600 AH Almelo");

            TextView Bezoekadres = (TextView) rootView.findViewById(R.id.bezoekadres);
            Bezoekadres.setText("Bezoekadres:\n" +
                    "Sluiskade Noordzijde 68\n" +
                    "7602 HT Almelo");

            TextView Telefoon = (TextView) rootView.findViewById(R.id.telefoonmail);
            Telefoon.setText("Telefoon: 0546 480 800\n" +
                    "Fax: 0546 480 839\n" +
                    "Email: info68@osg-erasmus.nl");

            for (int i = 0; i < getCount(); i++) {

                String naam = getNaam(i);
                String functie = getFunctie(i);
                String email = getEmail(i);
                HashMap<String, CharSequence> contact = new HashMap<String, CharSequence>();

                contact.put("Naam", naam);
                contact.put("Functie", functie);
                contact.put("Email", email);

                // Contact aan list toevoegen
                contactList.add(contact);
            }


            ListAdapter adapter;

            adapter = new SimpleAdapter(getActivity(), contactList, R.layout.list_item_contact, new String[]{"Naam", "Functie", "Email"}, new int[]{R.id.naam, R.id.functie, R.id.email});

            // Adapter
            listView.setAdapter(adapter);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setBackgroundColor(Color.WHITE);
            getView().setClickable(true);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
    }

    public static class LesurenFragment extends Fragment {

        public static LesurenFragment newInstance() {
            LesurenFragment fragment = new LesurenFragment();
            return fragment;
        }

        public LesurenFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_text, container,
                    false);
            TextView Text = (TextView) rootView.findViewById(R.id.hometext);
            Text.setText("Lesuren");

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setBackgroundColor(Color.WHITE);
            getView().setClickable(true);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
    }

    public static class VakantieFragment extends Fragment {

        public static VakantieFragment newInstance() {
            VakantieFragment fragment = new VakantieFragment();
            return fragment;
        }

        public VakantieFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_text, container,
                    false);
            TextView Text = (TextView) rootView.findViewById(R.id.hometext);
            Text.setText("Vakanties");

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setBackgroundColor(Color.WHITE);
            getView().setClickable(true);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
    }



    static class PagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = {"Infokanaal", "Contact", "Lesuren", "Vakanties"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MededelingenFragment();
                case 1:
                    return new ContactFragment();
                case 2:
                    return new LesurenFragment();
                case 3:
                    return new VakantieFragment();
            }

            return null;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }
}
