package io.windward;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

public class MagnetoReceiver extends BroadcastReceiver {
    private Activity activity;
    private TextView headingText;

    public MagnetoReceiver() { }

    public MagnetoReceiver(Activity activity) {
        this.activity = activity;
        this.headingText = (TextView) activity.findViewById(R.id.heading);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String heading = intent.getStringExtra(MagnetoService.INTENT_EXTRA_HEADING);
        if (heading != null && heading != null) {
//            Log.d("HEADING: ", heading);
            headingText.setText(heading + "°");
        }
    }
}