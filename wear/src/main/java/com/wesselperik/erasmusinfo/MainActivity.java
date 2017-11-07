package com.wesselperik.erasmusinfo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    private TextView mTextView;
    GoogleApiClient googleClient;
    ArrayList<String> titleArray;
    ArrayList<String> textArray;
    GridPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleArray = new ArrayList<String>();
        textArray = new ArrayList<String>();
        adapter = new GridPagerAdapter(this, getFragmentManager());

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/action").setUrgent();
        putDataMapReq.getDataMap().putString("refresh", "true");
        putDataMapReq.getDataMap().putString("time", hour + ":" + minute + ":" + seconds);

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleClient, putDataReq);
        Log.d("Erasmusinfo Wear Client", "Refresh request naar host device gestuurd");

        final GridViewPager mGridPager = (GridViewPager) findViewById(R.id.pager);
        mGridPager.setAdapter(adapter);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(googleClient, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Log.i("Erasmusinfo Wear", String.valueOf(status));
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.i("Erasmusinfo Wear", "in on Data Changed");
        for (DataEvent event : dataEventBuffer){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem item = event.getDataItem();

                if(item.getUri().getPath().compareTo("/data") == 0 ){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    String status = dataMap.getString("status");

                    Log.d("Erasmusinfo Wear Status", status);

                    switch (status) {
                        case "success":

                            titleArray = dataMap.getStringArrayList("title");
                            textArray = dataMap.getStringArrayList("text");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GridPagerAdapter.removePages();
                                }
                            });

                            for (int i = 0; i < titleArray.size(); i++) {
                                Log.d("Erasmusinfo Wear Items", titleArray.get(i));
                                final int finalI = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GridPagerAdapter.addPage(titleArray.get(finalI), textArray.get(finalI));
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            break;
                        case "failedNoInternet":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter != null) {
                                        GridPagerAdapter.removePages();
                                        GridPagerAdapter.addPage("Error", "Kon data niet ophalen, je telefoon heeft mogelijk geen internetconnectie. Probeer het later opnieuw!");
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            break;
                        default:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter != null) {
                                        GridPagerAdapter.removePages();
                                        GridPagerAdapter.addPage("Error", "Een onbekende fout is opgetreden. Probeer het later opnieuw!");
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            break;
                    }

                }
            }
        }
    }
}


