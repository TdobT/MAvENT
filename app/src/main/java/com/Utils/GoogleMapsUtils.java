package com.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GoogleMapsUtils {


    public static void moveMapInLocation(GoogleMap map, Location location, float zoom, int speed) {

        // Moves the map in the current location
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(
                                location.getLatitude(),
                                location.getLongitude())
                        , zoom)
                , speed
                , null
        );
    }

    public static void moveMapInLocation(GoogleMap map, LatLng point, int speed) {

        // Moves the map in the current location
        map.animateCamera(
                CameraUpdateFactory.newLatLng(point)
                , speed
                , null
        );
    }

    public static void moveMapInLocation(GoogleMap map, LatLng point, float zoom, int speed) {

        // Moves the map in the current location
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(point, zoom)
                , speed
                , null
        );
    }


    public static void showGooglePositionButton(Context context, GoogleMap mMap) {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // map is a GoogleMap object
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        }
    }



    public static boolean isPlayServicesAvailable(Context context) {

        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {

            GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog((Activity) context, resultCode, 2)
                    .show();

            return false;
        }
        return true;
    }

    public static String getCountryName(Context context, double latitude, double longitude) {

        return "italy";
//        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//        List<Address> addresses = null;
//
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            Address result;
//
//            if (addresses != null && !addresses.isEmpty()) {
//                return addresses.get(0).getCountryName();
//            }
//            return null;
//        } catch (IOException ignored) {
//            //do some
//            return null;
//        }
    }
}
