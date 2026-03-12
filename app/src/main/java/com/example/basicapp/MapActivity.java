package com.example.basicapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private MapView mapView;
    private FloatingActionButton fabZoomIn, fabZoomOut, fabLocation;
    private LocationManager locationManager;
    private Marker myLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
                goToMyLocation();
            }
        });
    }

    private void goToMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnown == null) {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (lastKnown != null) {
            moveToLocation(lastKnown);
        } else {
            Toast.makeText(this, "Getting location...", Toast.LENGTH_SHORT).show();
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            moveToLocation(location);
                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {
                            Toast.makeText(MapActivity.this,
                                    "Location provider disabled", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        }
    }

    private void moveToLocation(Location location) {
        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapView.getController().animateTo(point);
        mapView.getController().setZoom(15.0);

        if (myLocationMarker == null) {
            myLocationMarker = new Marker(mapView);
            myLocationMarker.setTitle("My Location");
            myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(myLocationMarker);
        }
        myLocationMarker.setPosition(point);
        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            goToMyLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
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
