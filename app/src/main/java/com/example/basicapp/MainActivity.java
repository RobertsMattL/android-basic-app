package com.example.basicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import android.widget.FrameLayout;
import com.example.basicapp.data.local.LocationDatabase;
import com.example.basicapp.data.local.entity.LocationEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final long TRACKING_MIN_TIME_MS = 3000;
    private static final float TRACKING_MIN_DISTANCE_M = 5f;

    private MapView mapView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Marker myLocationMarker;
    private FrameLayout fragmentContainer;
    private ConstraintLayout mainContent;
    private int currentTileSourceIndex = 0;
    private final ITileSource[] tileSources = {
            TileSourceFactory.USGS_SAT,
            TileSourceFactory.MAPNIK,
            TileSourceFactory.OpenTopo
    };
    private final String[] tileSourceNames = {"Satellite", "Street", "Topo"};


    private MaterialButton btnTrackGps;
    private boolean isTracking = false;
    private LocationManager locationManager;
    private Polyline trackingPolyline;
    private List<GeoPoint> trackingPoints = new ArrayList<>();
    private LocationListener trackingLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved dark mode preference before setting content view
        boolean darkMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        Log.d(TAG, "Test log: MainActivity onCreate called");

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        // Set up the custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fragmentContainer = findViewById(R.id.fragmentContainer);
        mainContent = findViewById(R.id.mainContent);

        // Set up the Map
        mapView = findViewById(R.id.mapFragment);
        mapView.setTileSource(TileSourceFactory.USGS_SAT);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(10.0);

        GeoPoint sydney = new GeoPoint(-34.0, 151.0);
        mapView.getController().setCenter(sydney);

        Marker marker = new Marker(mapView);
        marker.setPosition(sydney);
        marker.setTitle("Marker in Sydney");
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        // Set up the current location FAB
        FloatingActionButton fabMyLocation = findViewById(R.id.fabMyLocation);
        fabMyLocation.setOnClickListener(v -> goToMyLocation());

        // Set up GPS tracking button
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        btnTrackGps = findViewById(R.id.btnTrackGps);
        btnTrackGps.setOnClickListener(v -> toggleGpsTracking());

        // Set up the layers FAB
        FloatingActionButton fabLayers = findViewById(R.id.fabLayers);
        fabLayers.setOnClickListener(v -> {
            currentTileSourceIndex = (currentTileSourceIndex + 1) % tileSources.length;
            mapView.setTileSource(tileSources[currentTileSourceIndex]);
            Toast.makeText(this, tileSourceNames[currentTileSourceIndex], Toast.LENGTH_SHORT).show();
        });

        // Set up the ListView
        ListView listView = findViewById(R.id.listView);

        // Create sample data
        String[] items = {
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5",
            "Item 6",
            "Item 7",
            "Item 8",
            "Item 9",
            "Item 10"
        };

        // Create an ArrayAdapter to populate the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            items
        );

        // Set the adapter to the ListView
        listView.setAdapter(adapter);

        // Set up the bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.bottom_map) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            } else if (id == R.id.bottom_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (id == R.id.bottom_about) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            return false;
        });
    }

    private void toggleGpsTracking() {
        if (isTracking) {
            stopTracking();
        } else {
            startTracking();
        }
    }

    private void startTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        isTracking = true;
        btnTrackGps.setText(R.string.stop_tracking);
        btnTrackGps.setIconResource(android.R.drawable.ic_media_pause);

        trackingPoints.clear();
        trackingPolyline = new Polyline();
        trackingPolyline.getOutlinePaint().setColor(Color.RED);
        trackingPolyline.getOutlinePaint().setStrokeWidth(8f);
        mapView.getOverlays().add(trackingPolyline);

        trackingLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                trackingPoints.add(point);
                trackingPolyline.setPoints(trackingPoints);
                mapView.getController().animateTo(point);
                mapView.invalidate();

                // Save to database in background
                new Thread(() -> {
                    LocationEntity entity = new LocationEntity(
                            "Track Point",
                            location.getLatitude(),
                            location.getLongitude(),
                            "GPS tracking session",
                            System.currentTimeMillis()
                    );
                    LocationDatabase.getInstance(MainActivity.this)
                            .locationDao().insert(entity);
                }).start();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(MainActivity.this,
                        "GPS disabled — tracking stopped", Toast.LENGTH_SHORT).show();
                stopTracking();
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                TRACKING_MIN_TIME_MS, TRACKING_MIN_DISTANCE_M, trackingLocationListener);

        Toast.makeText(this, "GPS tracking started", Toast.LENGTH_SHORT).show();
    }

    private void stopTracking() {
        isTracking = false;
        btnTrackGps.setText(R.string.start_tracking);
        btnTrackGps.setIconResource(android.R.drawable.ic_menu_mylocation);

        if (trackingLocationListener != null) {
            locationManager.removeUpdates(trackingLocationListener);
            trackingLocationListener = null;
        }

        int pointCount = trackingPoints.size();
        Toast.makeText(this,
                "Tracking stopped — " + pointCount + " points recorded",
                Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refresh clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_search) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_test_crash) {
            throw new RuntimeException("Test crash triggered from menu");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showMainContent();
        } else if (id == R.id.nav_map) {
            showMainContent();
        } else if (id == R.id.nav_contacts) {
            showFragment(new ContactsFragment());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Contacts");
            }
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void showFragment(androidx.fragment.app.Fragment fragment) {
        mainContent.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void showMainContent() {
        fragmentContainer.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
        // Remove any fragment
        androidx.fragment.app.Fragment current =
                getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (current != null) {
            getSupportFragmentManager().beginTransaction().remove(current).commit();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTracking) {
            stopTracking();
        }
        mapView.onPause();
    }
}
