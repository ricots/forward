package io.windward.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import io.windward.MainActivity;
import io.windward.receivers.MagnetoReceiver;

public class MagnetoService extends IntentService implements SensorEventListener {
    public static final String INTENT_EXTRA_HEADING = "azimuth";
    private static final int rateToUpdate = 15;  // Skip ever N updates

    private ContentResolver cResolver;
    private SensorManager mSensorManager;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private float[] mGeomagnetic;
    private float[] mGravity;
    private int count;

    public MagnetoService() {
        super("MagnetoService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        cResolver = getContentResolver();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (magnetometer == null || accelerometer == null) {
            Log.i("NO_ACC", "Sorry, your device does not have a magnetometer or accelerometer, you cannot use this app");
        }

        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) { }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (count % rateToUpdate == 0) {
            int type = event.sensor.getType();

            if (type == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values;
            } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic = event.values;
            }

            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    int heading = (int) ((Math.toDegrees(orientation[0])+360) % 360); // orientation contains: azimuth, pitch and roll

//                Log.d("ORIENTATION: ", "Azimuth: " + azimuth);

                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_SEND);
                    i.putExtra(INTENT_EXTRA_HEADING, String.valueOf(heading));
                    i.setClass(this, MagnetoReceiver.class);
                    MainActivity.broadcastManager.sendBroadcast(i);
                }
            }
        }
        count++;
    }
}