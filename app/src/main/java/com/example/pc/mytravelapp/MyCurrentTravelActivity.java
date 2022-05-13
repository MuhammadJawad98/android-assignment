package com.example.pc.mytravelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyCurrentTravelActivity extends AppCompatActivity {
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    boolean requestingLocationUpdates = false;
    double latitude;
    double longitude;
    String address;
    String name;
    private static final int REQUEST_LOCATION = 1;
    private TextView tvLat, tvLong;
    private EditText edtStreetAddress;
    private EditText edtNote;
    private Button btnAddUpdate, btnTravelOnMap, btnTravelList;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_current_travel);
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        getSupportActionBar().setTitle("My current travel: " + name);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLng);
        edtStreetAddress = findViewById(R.id.edtStreetAddress);
        edtNote = findViewById(R.id.edtNoteId);
        btnAddUpdate = findViewById(R.id.btnUpdateLocation);
        btnTravelOnMap = findViewById(R.id.btnViewTravel);
        btnTravelList = findViewById(R.id.btnTravelList);
        requestPermissions();
        createLocationRequestLocationCallback();

        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocationToDB();
            }
        });
       btnTravelOnMap.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
               startActivity(intent);
           }
       });
        btnTravelList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
//                startActivity(intent);
            }
        });
    }

    public boolean requestPermissions() {
        int REQUEST_PERMISSION = 3000;
        String permissions[] = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        boolean grantFinePermission =
                ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean grantCoarsePermission =
                ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED;
        if (!grantFinePermission && !grantCoarsePermission) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
        } else if (!grantFinePermission) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[0]}, REQUEST_PERMISSION);
        } else if (!grantCoarsePermission) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[1]}, REQUEST_PERMISSION);
        }
        return grantFinePermission && grantCoarsePermission;
    }

    private void createLocationRequestLocationCallback() {
        locationRequest = LocationRequest.create();
        long ms = 10000; // milliseconds
        // Sets the desired interval for active location updates.
        locationRequest.setInterval(ms);
        // Sets the fastest rate for active location updates.
        locationRequest.setFastestInterval(ms / 2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                System.out.println("location result::: " + locationResult);
            }
        };
    }

    public void getLocationAddress(View view) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Start location updates
                        startLocationUpdates();
                        if (location != null) {
                            // Logic to handle location object
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            tvLat.setText("Latitude: " + latitude);
                            tvLong.setText("Longitude: " + longitude);
                            address = getStreetAddress(latitude, longitude);
                            edtStreetAddress.setText(address);

                            SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                                    (SupportStreetViewPanoramaFragment)
                                            getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
                            streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                                    new OnStreetViewPanoramaReadyCallback() {
                                        @Override
                                        public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                                            LatLng latLng = new LatLng(latitude, longitude);
                                            panorama.setPosition(latLng);
                                        }
                                    });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("error:::::: " + e.getMessage());
            }
        });
    }

    private String getStreetAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String streetAddress = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address address = addresses.get(0);
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    streetAddress += address.getAddressLine(i) + "\n";
                }
            } else {
                streetAddress = "Unknown";
            }
        } catch (Exception e) {
            streetAddress = "Service not available";
            e.printStackTrace();
        }
        return streetAddress;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void addLocationToDB() {
        //String lat, String lng, String address, String note, String currentDate, String locationName
        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);
        String address = edtStreetAddress.getText().toString().trim();
        String note = edtNote.getText().toString().trim();
        if (note.equals("")) {
            note = " ";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        String locationName = name;
        LocationData data = new LocationData(lat, lng, address, note, currentDate, locationName);
        db.addLocation(data);
        System.out.println("successfully add data.....");
    }
}