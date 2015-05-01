package io.windward;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nutiteq.core.MapPos;
import com.nutiteq.core.MapRange;
import com.nutiteq.datasources.LocalVectorDataSource;
import com.nutiteq.graphics.Color;
import com.nutiteq.layers.VectorLayer;
import com.nutiteq.projections.Projection;
import com.nutiteq.styles.LineStyleBuilder;
import com.nutiteq.styles.MarkerStyle;
import com.nutiteq.styles.MarkerStyleBuilder;
import com.nutiteq.styles.PolygonStyleBuilder;
import com.nutiteq.ui.MapView;
import com.nutiteq.utils.BitmapUtils;
import com.nutiteq.vectorelements.Marker;

public class Plotter {
    private static final int ZOOM_LEVEL = 10;
//    private static final float BOAT_SIZE = ZOOM_LEVEL * (float)(100 * (float)(1/ZOOM_LEVEL));
    private static final float BOAT_SIZE = ZOOM_LEVEL * (75 / ZOOM_LEVEL);
    private Activity activity;
    private MapView mapView;
    private Projection proj;
    private LocalVectorDataSource vectorDataSource;
    private VectorLayer vectorLayer;
    private MapRange range;
    private PolygonStyleBuilder polygonStyleBuilder;
    private LineStyleBuilder lineStyleBuilder;
    private Bitmap androidMarker;
    private com.nutiteq.graphics.Bitmap marker;
    private MarkerStyleBuilder markerStyleBuilder;
    private MarkerStyle sharedMarkerStyle;
    private float heading;

    public Plotter(Activity activity) {
        this.activity = activity;
        mapView = (MapView) activity.findViewById(R.id.map_view);
        proj = mapView.getOptions().getBaseProjection();
        vectorDataSource = new LocalVectorDataSource(proj);
        vectorLayer = new VectorLayer(vectorDataSource);
        mapView.getLayers().add(vectorLayer);

        range = new MapRange(10, 24);
        vectorLayer.setVisibleZoomRange(range);

        androidMarker = BitmapFactory.decodeResource(activity.getResources(), R.drawable.marker);
        marker = BitmapUtils.CreateBitmapFromAndroidBitmap(androidMarker);
        markerStyleBuilder = new MarkerStyleBuilder();
        markerStyleBuilder.setBitmap(marker);
        markerStyleBuilder.setSize(BOAT_SIZE);
        sharedMarkerStyle = markerStyleBuilder.buildStyle();

        this.polygonStyleBuilder = new PolygonStyleBuilder();
        this.polygonStyleBuilder.setColor(new Color(0xFFFF0000));

        this.lineStyleBuilder = new LineStyleBuilder();
        this.lineStyleBuilder.setColor(new Color(0xFF000000));
        lineStyleBuilder.setWidth(1.0f);
        polygonStyleBuilder.setLineStyle(lineStyleBuilder.buildStyle());
    }

    private void clearOldPlotPoints() {
        vectorDataSource.removeAll();
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public void plot(float lat, float lon) {
        clearOldPlotPoints();
        MapPos pos = new MapPos(lon, lat);
        MapPos wgs84 = proj.fromWgs84(pos);
        Marker m = new Marker(wgs84, sharedMarkerStyle);

        // Rotation is clockwise in Nutiteq
        m.setRotation(-heading);
        vectorDataSource.add(m);
        mapView.setFocusPos(wgs84, 0);
        mapView.setZoom(15, wgs84, 0);
    }
}
