package com.Utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class LatLngDataSet {

    private ArrayList<LatLng> mDataset;

    private String mUrl;

    public LatLngDataSet(ArrayList<LatLng> dataSet, String url) {

        this.mDataset = dataSet;

        this.mUrl = url;
    }

    public ArrayList<LatLng> getData() {
        return mDataset;
    }

    public String getUrl() {
        return mUrl;
    }
}

