package io.forward.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import io.forward.MainActivity;
import io.forward.receivers.GPSReceiver;

public class GPSService extends IntentService implements LocationListener {
    private LocationManager lm;
    public static final String INTENT_EXTRA_LAT_DEGREES = "lat_degrees";
    public static final String INTENT_EXTRA_LON_DEGREES = "lon_degrees";
    public static final String INTENT_EXTRA_LAT_MINUTES = "lat_minutes";
    public static final String INTENT_EXTRA_LON_MINUTES = "lon_minutes";
    public static final String INTENT_EXTRA_SPEED = "speed";
    private double longitude;
    private double latitude;
    private float speedInMPS;

    public GPSService() {
        super("GPSService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
        }
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Not sure why this needs to be reversed but the default format is to get it as a negative number
        longitude = -longitude;
        speedInMPS = location.getSpeed();

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);

        String latDegrees = location.convert(latitude, Location.FORMAT_DEGREES);
        String lonDegrees = location.convert(longitude, Location.FORMAT_DEGREES);
        latDegrees = latDegrees.substring(0, latDegrees.length() - 2);
        lonDegrees = lonDegrees.substring(0, lonDegrees.length() - 2);

        i.putExtra(INTENT_EXTRA_LAT_DEGREES, latDegrees);
        i.putExtra(INTENT_EXTRA_LON_DEGREES, lonDegrees);
        i.putExtra(INTENT_EXTRA_LAT_MINUTES, location.convert(latitude, Location.FORMAT_MINUTES));
        i.putExtra(INTENT_EXTRA_LON_MINUTES, location.convert(longitude, Location.FORMAT_MINUTES));
        i.putExtra(INTENT_EXTRA_SPEED, speedInMPS);
        i.setClass(this, GPSReceiver.class);
        MainActivity.broadcastManager.sendBroadcast(i);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Called when the provider is disabled by the user.
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Called when the provider is enabled by the user.
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Called when the provider status changes.
    }

}
