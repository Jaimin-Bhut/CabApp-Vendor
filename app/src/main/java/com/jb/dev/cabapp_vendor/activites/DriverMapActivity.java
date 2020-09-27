package com.jb.dev.cabapp_vendor.activites;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jb.dev.cabapp_vendor.R;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    SupportMapFragment mapFragment;
    double start_lat, start_long, end_lat, end_long, current_lat, current_long;
    private Marker currentLocationMarker;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    MapsActivity mapsActivity;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        isSet();
        mapsActivity = new MapsActivity();
        view = findViewById(android.R.id.content);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        distanceBetweenPointsInKm(start_lat, start_long, end_lat, end_long);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    void isSet() {
        Bundle b = getIntent().getExtras();
        start_lat = b.getDouble("start_lat");
        start_long = b.getDouble("start_long");
        end_lat = b.getDouble("end_lat");
        end_long = b.getDouble("end_long");
    }

    @Override
    public void onLocationChanged(Location location) {
//            currentLocationMarker.remove();
        current_lat = location.getLatitude();
        current_long = location.getLongitude();
        LatLng latLng3 = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng3);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng3));
        if (googleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            MarkerOptions startMark = new MarkerOptions();
            LatLng latLng = new LatLng(start_lat, start_long);
            startMark.position(latLng);
            startMark.title("PickUp Location");
            startMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(startMark);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            MarkerOptions endMark = new MarkerOptions();
            LatLng latLng1 = new LatLng(end_lat, end_long);
            endMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            endMark.title("Drop Location");
            endMark.position(latLng1);
            mMap.addMarker(endMark);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }

    }


    private double distanceBetweenPointsInKm(double lat_a, double lng_a, double lat_b, double lng_b) {
        Log.e("first", lat_a + "," + lng_a);
        Log.e("second", lat_b + "," + lng_b);
        double pk = (float) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        double ans = 6366 * tt;
        Log.e("distance", ans + "");
        return ans;
    }

}