package io.forward;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nutiteq.datasources.NutiteqOnlineTileDataSource;
import com.nutiteq.datasources.TileDataSource;
import com.nutiteq.layers.VectorTileLayer;
import com.nutiteq.ui.MapView;
import com.nutiteq.utils.AssetUtils;
import com.nutiteq.vectortiles.MBVectorTileDecoder;
import com.nutiteq.vectortiles.MBVectorTileStyleSet;
import com.nutiteq.wrappedcommons.UnsignedCharVector;

import io.forward.receivers.GPSReceiver;
import io.forward.receivers.MagnetoReceiver;
import io.forward.services.GPSService;
import io.forward.services.MagnetoService;


public class MainActivity extends Activity {
    public static final String INTENT_EXTRA_HEADING = "heading";
    private BroadcastReceiver gpsReceiver;
    private BroadcastReceiver magnetoReceiver;
    private static final String LICENSE_KEY = "XTUMwQ0ZDREU5bUkvSm5SRjRNYnh5NUFDc3NsbW41U2NBaFVBaC92YzYrYXV3WWh3eDNCTElEaFNnTkpPM2I4PQoKcHJvZHVjdHM9c2RrLWFuZHJvaWQtMy4qCnBhY2thZ2VOYW1lPWlvLmZvcndhcmQKd2F0ZXJtYXJrPW51dGl0ZXEKdXNlcktleT1hOTEwMDM1NjQ2NjJhZjM4MWZjMjZmNGE1NDAzYTNjYQo=";
    public static LocalBroadcastManager broadcastManager;
    private MapView mapView;
    private Plotter plotter;

    private void startOfflineMaps() {
        // 1. The initial step: register your license. This must be done before using MapView!
        MapView.RegisterLicense(LICENSE_KEY, getApplicationContext());

        // Create map view
        mapView = (MapView) this.findViewById(R.id.map_view);

        // Create layer with vector styling
        UnsignedCharVector styleBytes = AssetUtils.loadBytes("osmbright.zip");
        MBVectorTileDecoder vectorTileDecoder = null;
        if (styleBytes != null){
            // Create style set
            MBVectorTileStyleSet vectorTileStyleSet = new MBVectorTileStyleSet(styleBytes);
            vectorTileDecoder = new MBVectorTileDecoder(vectorTileStyleSet);
        }
        TileDataSource vectorTileDataSource = new NutiteqOnlineTileDataSource("nutiteq.mbstreets");
        VectorTileLayer baseLayer = new VectorTileLayer(vectorTileDataSource, vectorTileDecoder);

        mapView.getLayers().add(baseLayer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.forward.R.layout.activity_main);

        if (savedInstanceState != null) {
//            plotter.setHeading(savedInstanceState.getFloat(INTENT_EXTRA_HEADING));
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                findViewById(R.id.LatWrapper).setVisibility(View.GONE);
                findViewById(R.id.LonWrapper).setVisibility(View.GONE);
                findViewById(R.id.headingWrapper).setVisibility(View.GONE);
                findViewById(R.id.speedInKnotsWrapper).setVisibility(View.GONE);
            } else {
                findViewById(R.id.LatWrapper).setVisibility(View.VISIBLE);
                findViewById(R.id.LonWrapper).setVisibility(View.VISIBLE);
                findViewById(R.id.headingWrapper).setVisibility(View.VISIBLE);
                findViewById(R.id.speedInKnotsWrapper).setVisibility(View.VISIBLE);
                findViewById(R.id.mapWrapper).setVisibility(View.VISIBLE);
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SEND);
        plotter = new Plotter(this);
        gpsReceiver = new GPSReceiver(this, plotter);
        magnetoReceiver = new MagnetoReceiver(this, plotter);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(gpsReceiver, filter);
        broadcastManager.registerReceiver(magnetoReceiver, filter);

        // Start the dim service
        startService(new Intent(this, GPSService.class));
        startService(new Intent(this, MagnetoService.class));
        startOfflineMaps();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putFloat(INTENT_EXTRA_HEADING, plotter.getHeading());
//        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(io.forward.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == io.forward.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(gpsReceiver);
        broadcastManager.unregisterReceiver(magnetoReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(GPSService.INTENT_EXTRA_LAT_MINUTES);
        intentFilter.addAction(GPSService.INTENT_EXTRA_LON_MINUTES);
        LocalBroadcastManager.getInstance(this).registerReceiver((gpsReceiver), intentFilter);

        IntentFilter i = new IntentFilter(MagnetoService.INTENT_EXTRA_HEADING);
        LocalBroadcastManager.getInstance(this).registerReceiver((magnetoReceiver), i);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
