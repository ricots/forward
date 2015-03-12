package io.windward;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSService extends IntentService implements LocationListener {
    private LocationManager lm;
    public static final String INTENT_EXTRA_LAT = "lat";
    public static final String INTENT_EXTRA_LON = "lon";
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
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
        }

    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speedInMPS = location.getSpeed();

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(INTENT_EXTRA_LAT, location.convert(latitude, Location.FORMAT_MINUTES));
        i.putExtra(INTENT_EXTRA_LON, location.convert(longitude, Location.FORMAT_MINUTES));
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
