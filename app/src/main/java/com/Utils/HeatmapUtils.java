package com.Utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class HeatmapUtils {

    /**
     * Colors for default gradient.
     * Array of colors, represented by ints.
     */
    public static final int[] PRIMARY_DEFAULT_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 200),
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 50, 127),
            Color.rgb(255, 50, 0)
    }, SECONDARY_DEFAULT_GRADIENT_COLORS = {
            Color.argb(0, 255, 255, 200),
            Color.argb(255 / 3 * 2, 150, 255, 255),
            Color.rgb(50, 91, 255),
            Color.rgb(50, 50, 227),
            Color.rgb(0, 0, 255)
    };

    /**
     * Starting fractions for default gradient.
     * This defines which percentages the above colors represent.
     * These should be a sorted array of floats in the interval [0, 1].
     */
    public static final float[] PRIMARY_DEFAULT_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    }, SECONDARY_DEFAULT_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    /**
     * Default gradient for heatmap.
     */
    public static final Gradient
            PRIMARY_DEFAULT_GRADIENT =
                    new Gradient(PRIMARY_DEFAULT_GRADIENT_COLORS, PRIMARY_DEFAULT_GRADIENT_START_POINTS),
            SECONDARY_DEFAULT_GRADIENT =
                    new Gradient(SECONDARY_DEFAULT_GRADIENT_COLORS, SECONDARY_DEFAULT_GRADIENT_START_POINTS);



    public static HeatmapTileProvider getProvider(
            HashMap<String, LatLngDataSet> mLists,
            String regionName) {

        ArrayList<LatLng> providerData;

        providerData = mLists.get(regionName).getData();

        // Create a heat map tile provider, passing it the latlngs found in mLists.
        return new HeatmapTileProvider.Builder()
                .data(providerData)
                .gradient(HeatmapUtils.PRIMARY_DEFAULT_GRADIENT)
                .radius(30)
                .opacity(0.9)
                .build();
    }


    public static ArrayList<LatLng> readItems(JSONArray array) throws JSONException {

        ArrayList<LatLng> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            JSONObject object = array.getJSONObject(i);

            list.add(new LatLng(
                    object.getDouble("lat"),
                    object.getDouble("lng"))
            );
        }

        Collections.sort(list, new Comparator<LatLng>() {
            @Override
            public int compare(LatLng o1, LatLng o2) {
                if (o1.latitude < o2.latitude) return 1;
                else if (o1.latitude > o2.latitude) return -1;
                else if (o1.longitude < o2.longitude) return 1;
                else if (o1.longitude > o2.longitude) return -1;
                else return 0;
            }
        });

        return list;
    }



    public static void addHeatMapList(
            Context ctx,
            HashMap<String, LatLngDataSet> mLists,
            JSONObject jsonEvents) {

        String region = jsonEvents.keys().next();

        try {
            // Retrieves the JSONArray witch contains all the events in the specified region;
            // the server response MUST contain the events in this format.
            JSONArray arrayEvents = jsonEvents.getJSONArray(region);

            // Insert the items read from the arrayEvents inside the HashMap mLists.
            // This HashMap will be used to pick the right position for the heatmap.
            mLists.put(region, new LatLngDataSet(
                    HeatmapUtils.readItems(arrayEvents),
                    region));

        } catch (JSONException e) {
            Toast.makeText(ctx, "Problem reading list of location.", Toast.LENGTH_LONG).show();
        }
    }
}
