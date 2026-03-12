package com.example.basicapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private FloatingActionButton fabZoomIn, fabZoomOut, fabLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Map");
        }

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.USGS_SAT);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.0);

        GeoPoint defaultLocation = new GeoPoint(37.7749, -122.4194);
        mapView.getController().setCenter(defaultLocation);

        Marker marker = new Marker(mapView);
        marker.setPosition(defaultLocation);
        marker.setTitle("Marker in San Francisco");
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        // Initialize FABs
        fabZoomIn = findViewById(R.id.fab_zoom_in);
        fabZoomOut = findViewById(R.id.fab_zoom_out);
        fabLocation = findViewById(R.id.fab_location);

        fabZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getController().zoomIn();
            }
        });

        fabZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getController().zoomOut();
            }
        });

        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoint defaultLoc = new GeoPoint(37.7749, -122.4194);
                mapView.getController().animateTo(defaultLoc, 12.0, null);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
