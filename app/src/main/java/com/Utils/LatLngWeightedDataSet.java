package com.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Random;


public class LatLngWeightedDataSet {

    private ArrayList<WeightedLatLng> mDataset;

    private String mUrl;

    public LatLngWeightedDataSet(ArrayList<LatLng> dataSet, String url, int weight) {

        mDataset = new ArrayList<>();

        for (LatLng item: dataSet)
            mDataset.add(new WeightedLatLng(item, new Random(weight).nextFloat()));

        this.mUrl = url;
    }

    public ArrayList<WeightedLatLng> getData() {
        return mDataset;
    }

    public String getUrl() {
        return mUrl;
    }
}
