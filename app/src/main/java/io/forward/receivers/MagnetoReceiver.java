package io.forward.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.Surface;
import android.widget.TextView;

import io.forward.Plotter;
import io.forward.R;
import io.forward.services.MagnetoService;

public class MagnetoReceiver extends BroadcastReceiver {
    private Activity activity;
    private TextView headingText;
    private Plotter plotter;

    public MagnetoReceiver() { }

    public MagnetoReceiver(Activity activity, Plotter plotter) {
        this.activity = activity;
        this.headingText = (TextView) activity.findViewById(R.id.heading);
        this.plotter = plotter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String headingAsStr = intent.getStringExtra(MagnetoService.INTENT_EXTRA_HEADING);
        if (headingAsStr != null && headingAsStr != null) {
            float heading = Float.parseFloat(headingAsStr);
            // Rotation is clockwise in Nutiteq
            Display d = activity.getWindowManager().getDefaultDisplay();
            int rotation = d.getRotation();

            if (rotation == Surface.ROTATION_90) {
                heading += 90;
            } else if (rotation == Surface.ROTATION_180) {
                heading += 180;
            } else if (rotation == Surface.ROTATION_270) {
                heading -= 90;
            }

            plotter.updateHeading(heading);
//            Log.d("HEADING: ", heading);
            headingText.setText(heading + "°");
        }
    }
}