package io.forward.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        String heading = intent.getStringExtra(MagnetoService.INTENT_EXTRA_HEADING);
        if (heading != null && heading != null) {
            plotter.setHeading(Float.parseFloat(heading));
//            Log.d("HEADING: ", heading);
            headingText.setText(heading + "°");
        }
    }
}