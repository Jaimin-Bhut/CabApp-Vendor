package com.jb.dev.cabapp_vendor.activites;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_vendor.R;
import com.jb.dev.cabapp_vendor.adapter.BookingListAdapter;
import com.jb.dev.cabapp_vendor.helper.Constants;
import com.jb.dev.cabapp_vendor.helper.Helper;
import com.jb.dev.cabapp_vendor.model.BookingModel;

import java.util.Objects;

import static android.os.Build.VERSION_CODES;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewBooking;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference bookingRef = db.collection(Constants.BOOKING_COLLECTION_REFERENCE_KEY);
    BookingListAdapter adapter;
    Query query;
    String driverEmail, area, mId, id;
    private long backPressTime;
    Toast backToast;
    MapsActivity mapsActivity;
    View view;
    CollectionReference mCabRef;
    private FusedLocationProviderClient client;
    private static final int REQUEST_LOCATION_CODE = 99;
    private TextView textViewNoData;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewNoData = findViewById(R.id.txt_no_data);
        progressBar = findViewById(R.id.progress_circular);
        view = findViewById(android.R.id.content);
        mCabRef = db.collection(Constants.CAB_COLLECTION_REFERENCE_KEY);
        mapsActivity = new MapsActivity();
        client = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
        if (!Helper.isNetworkAvailable(MainActivity.this)) {
            Helper.showSnackBar(view, R.string.please_turn_on_internet, this);
        }
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    area = mapsActivity.getAddress(location.getLatitude(), location.getLongitude(), getApplicationContext());
                    Log.e("Area", area);
                } else {
                    Helper.showSnackBar(view, R.string.please_turn_on_location, MainActivity.this);
                }
            }
        });
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    //Permission is denied
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }

    }

    void init() {
        progressBar.setVisibility(View.VISIBLE);
        driverEmail = getDriverEmail(this);
        Log.e("init::", driverEmail);
        recyclerViewBooking = findViewById(R.id.driver_dashboard_booking_recycler_view);

        query = bookingRef.whereEqualTo(Constants.BOOKING_CAB_DRIVER_KEY, driverEmail);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()) {
                    textViewNoData.setVisibility(View.VISIBLE);
                } else {
                    textViewNoData.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        FirestoreRecyclerOptions<BookingModel> options = new FirestoreRecyclerOptions.Builder<BookingModel>()
                .setQuery(query, BookingModel.class)
                .build();
        adapter = new BookingListAdapter(options);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewBooking.setLayoutManager(layoutManager);
        recyclerViewBooking.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBooking.setHasFixedSize(true);
        recyclerViewBooking.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new BookingListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String cabNumber) {
                mId = documentSnapshot.getId();
                final Query q = mCabRef.whereEqualTo(Constants.CAB_NUMBER_KEY, cabNumber);
                final DocumentReference mReference = db.collection(Constants.BOOKING_COLLECTION_REFERENCE_KEY).document(mId);
                mReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Object object = documentSnapshot.get(Constants.BOOKING_VERIFIED);
                        if (object.toString().equalsIgnoreCase("true")) {
                            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                            id = snapshot.getId();
                                        }
                                        DocumentReference documentReference = db.collection(Constants.CAB_COLLECTION_REFERENCE_KEY).document(id);
                                        documentReference.update(Constants.CAB_STATUS_KEY, Constants.AVAILABLE);
                                        documentReference.update(Constants.CAB_AREA_KEY, area)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mReference.update(Constants.BOOKING_COMPLETED, true);
                                                        Snackbar.make(view, "Success", BaseTransientBottomBar.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }
                            });
                        } else {
                            Snackbar.make(view, "First need to verify user", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        if (backPressTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            moveTaskToBack(true);
            super.onBackPressed();
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressTime = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @RequiresApi(api = VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
            clearAppPref(this);
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);
            }
        }
    }

    public static void clearAppPref(Context mainContext) {
        SharedPreferences sp = mainContext.getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(mainContext), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static String getDriverEmail(Context context) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(context), Context.MODE_PRIVATE);
        return sp.getString("setDriverEmail", "");
    }
}