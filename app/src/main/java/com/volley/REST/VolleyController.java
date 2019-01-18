package com.volley.REST;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class VolleyController    {
    private static VolleyController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyController(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyController getInstance(Context context) {
        // If instance is not available, create it. If available, reuse and return the object.
        if (mInstance == null) {
            mInstance = new VolleyController(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {

            // Setup 10 MB disk-based cache for Volley
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 1024 * 1024 * 10);

            // Use HttpURLConnection as the HTTP client
            Network network = new BasicNetwork(new HurlStack());

            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }

        return mRequestQueue;
    }

    public void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }

}