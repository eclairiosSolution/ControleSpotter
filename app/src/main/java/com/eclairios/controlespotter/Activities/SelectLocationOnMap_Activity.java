package com.eclairios.controlespotter.Activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.eclairios.controlespotter.Fragments.MyPlaces;
import com.eclairios.controlespotter.Others.CurrentLocation;
import com.eclairios.controlespotter.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.eclairios.controlespotter.Activities.MainActivity.prefs;

public class SelectLocationOnMap_Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CurrentLocation current_location_class;
    private double longitude, latitude;
    private LatLng latLng;
    private double lat, lng;
    private FloatingActionButton buttonnavigation, buttonzoommap, buttonzoomoutmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location_on_map_);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        goback_method();
        getcurrentlocation();
        flationlisten();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        float zoomLevel = 15.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        boolean darkmode_switch = prefs.getBoolean("darkmode_switch", false);
        if (darkmode_switch) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(SelectLocationOnMap_Activity.this, R.raw.style_json));
        }

        // Add a marker in Sydney and move the camera
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                 lat = (float) latLng.latitude;
                 lng = (float) latLng.longitude;
                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
            }
        });
    }

    private void goback_method(){
        final Button map_ok_btn = findViewById(R.id.map_ok_btn);
        map_ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("message_subject_intent");
                intent.putExtra("lat", (lat));
                intent.putExtra("lng", (lng));
                LocalBroadcastManager.getInstance(SelectLocationOnMap_Activity.this).sendBroadcast(intent);
                onBackPressed();

            }
        });

    }

    private void getcurrentlocation() {

        current_location_class = new CurrentLocation(SelectLocationOnMap_Activity.this);
        if (current_location_class.canGetLocation()) {

            longitude = current_location_class.getLongitude();
            latitude = current_location_class.getLatitude();
            latLng = new LatLng(latitude, longitude);
            Log.e("latit", "onCreate: " + longitude);
        } else {

            current_location_class.showSettingsAlert();
        }
    }

    private void flationlisten(){

        buttonnavigation = findViewById(R.id.buttonnavigation);
        buttonzoommap = findViewById(R.id.buttonzoommap);
        buttonzoomoutmap = findViewById(R.id.buttonzoomoutmap);

        buttonnavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng latLng = new LatLng(latitude,longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);

            }
        });

        buttonzoommap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        buttonzoomoutmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }
}