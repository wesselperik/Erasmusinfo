package com.wesselperik.erasmusinfo;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

/**
 * Created by Wessel on 6-3-2016.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            if("/SAMPLE".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                String stringExample = map.getString("string_example");
                Log.d("Erasmusinfo", stringExample);
            }
        }

//        DataMap dataMap;
//        for (DataEvent event : dataEvents) {
//
//            // Check the data type
//            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                // Check the data path
//                String path = event.getDataItem().getUri().getPath();
//                if (path.equals(WEARABLE_DATA_PATH)) {}
//                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                Log.d("Erasmusinfo", "DataMap received on watch: " + dataMap);
//                String message = dataMap.getString("Mededeling");
//                Intent messageIntent = new Intent();
//                messageIntent.setAction(Intent.ACTION_SEND);
//                messageIntent.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
//            }
//        }
    }

}
