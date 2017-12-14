package com.wesselperik.erasmusinfo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

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
import com.wesselperik.erasmusinfo.classes.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Wessel on 20-3-2016.
 */
public class WearService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    GoogleApiClient googleClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy() {
        Log.w("Erasmusinfo", "Wearservice gestopt.");
    }

    @Override
    public void onCreate() {
        Log.w("Erasmusinfo", "WearService gestart.");
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();
    }


    @Override
    public void onStart(Intent intent, int startid)
    {

    }

    @Override
    public void onConnected(Bundle bundle) {

        Wearable.DataApi.addListener(googleClient, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Log.i("Erasmusinfo Wear Host", String.valueOf(status));
            }
        });
    }

    private class GetInfokanaalData extends AsyncTask<Void, Void, Void> {

        public Handler UIHandler = new Handler(Looper.getMainLooper());

        ArrayList<String> titleArray = new ArrayList<String>();
        ArrayList<String> textArray = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // ServiceHandler
            ServiceHandler sh = new ServiceHandler();

            // Request maken naar server
            if (getApplicationContext() != null) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                //ArrayList<HashMap<String, CharSequence>> mededelingList = new ArrayList<HashMap<String, CharSequence>>();

                String schoolName = prefs.getString("settings_schoolname", "havovwo");

                String jsonStr = sh.makeServiceCall("http://api.erasmusinfo.nl/v3?location=" + schoolName, ServiceHandler.GET);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // JSON array ophalen
                        JSONArray mededelingen = jsonObj.getJSONArray("mededelingen");

                        for (int i = 0; i < mededelingen.length(); i++) {
                            JSONObject c = mededelingen.getJSONObject(i);

                            String id = c.getString("id");
                            String title = c.getString("titel");
                            String text = String.valueOf(Html.fromHtml(c.getString("mededeling")));

                            //HashMap<String, CharSequence> mededeling = new HashMap<String, CharSequence>();

                            //mededeling.put("id", id);
                            //mededeling.put("titel", title);
                            //mededeling.put("mededeling", text);
                            titleArray.add(title);
                            textArray.add(text);

                            // Mededeling aan list toevoegen
                            //mededelingList.add(mededeling);
                        }
                    } catch (Exception e) {
                        cancel(true);
                        e.printStackTrace();
                    }
                } else {
                    cancel(true);
                    Log.e("ServiceHandler", "Kon geen data van de Erasmusinfo API ophalen!");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            UIHandler.post(new Runnable() {
                public void run() {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR);
                    int minute = c.get(Calendar.MINUTE);
                    int seconds = c.get(Calendar.SECOND);

                    PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/data").setUrgent();
                    putDataMapReq.getDataMap().putString("time", hour + ":" + minute + ":" + seconds);
                    putDataMapReq.getDataMap().putString("status", "success");
                    putDataMapReq.getDataMap().putStringArrayList("title", titleArray);
                    putDataMapReq.getDataMap().putStringArrayList("text", textArray);

                    PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
                    Wearable.DataApi.putDataItem(googleClient, putDataReq);
                    Log.d("Erasmusinfo Wear Host", "Data opgehaald en verstuurd!");
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.i("Erasmusinfo Wear Host", "in on Data Changed");
        for (DataEvent event : dataEventBuffer){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem item = event.getDataItem();

                if(item.getUri().getPath().compareTo("/action") == 0 ){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    Log.d("Erasmusinfo Wear Host", "Refresh request ontvangen van Client");
                    if(isNetworkAvailable(getApplicationContext())) {
                        new GetInfokanaalData().execute();
                        Log.d("Erasmusinfo Wear Host", "Data van infokanaal ophalen...");
                    }else{
                        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/data").setUrgent();
                        putDataMapReq.getDataMap().putString("status", "failedNoInternet");

                        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
                        Wearable.DataApi.putDataItem(googleClient, putDataReq);
                        Log.d("Erasmusinfo Wear Host", "Geen internet, error verstuurd naar Client");
                    }

                }
            }
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