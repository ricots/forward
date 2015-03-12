package io.windward;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

public class GPSReceiver extends BroadcastReceiver {
    private static final float METERS_TO_KNOTS = 1.94384f;
    private static final String DELIMS = ":.";

    private Activity activity;
    private TextView latTextField;
    private TextView lonTextField;
    private TextView speedMPS;
    private TextView speedKnots;
    private String lat;
    private String lon;
    private float mps;
    private float knots;

    public GPSReceiver() { }

    public GPSReceiver(Activity activity) {
        this.activity = activity;
        this.latTextField = (TextView) activity.findViewById(R.id.latitude);
        this.lonTextField = (TextView) activity.findViewById(R.id.longitude);
        this.speedMPS = (TextView) activity.findViewById(R.id.speed_in_mps);
        this.speedKnots = (TextView) activity.findViewById(R.id.speed_in_knots);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        lat = intent.getStringExtra(GPSService.INTENT_EXTRA_LAT);
        lon = intent.getStringExtra(GPSService.INTENT_EXTRA_LON);
        mps = intent.getFloatExtra(GPSService.INTENT_EXTRA_SPEED, 0.0f);

        Log.d("GPS: ", lat + " " + lon);


        if (this.latTextField != null && lat != null) {
            this.latTextField.setText(formatCoordinate(lat));
        }

        if (this.lonTextField != null && lon != null) {
            this.lonTextField.setText(formatCoordinate(lon));
        }

        if (this.speedMPS != null) {
            knots = mps * METERS_TO_KNOTS;
            this.speedMPS.setText(String.valueOf(mps));
            this.speedKnots.setText(String.valueOf(knots));
        }
    }

    private String formatCoordinate(String coordinate) {
        String[] parts = coordinate.split(":");
        String minutesSecondsStr = parts[1];
        String[] minutesSeconds = minutesSecondsStr.split("\\.");
        return parts[0] + "°" + minutesSeconds[0] + "'" + minutesSeconds[1] + "\"";
    }
}
