package com.atsuite.subletapp;

//import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.Table.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddSubletActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    
    private EditText edtTitle, edtDesc, edtAddress, edtPrice, edtStartDate, edtEndDate;
    private Spinner spinnerSubletType;
    private Button btnPickLocation, btnChoosePicture, btnSaveSublet;
    private TextView txtLatLng;

    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_sublet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Sublet");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        
        edtTitle = findViewById(R.id.editTextTitle);
        spinnerSubletType = findViewById(R.id.spinnerSubletType);
        edtDesc = findViewById(R.id.editTextDescription);
        edtPrice = findViewById(R.id.editTextPrice);
        edtStartDate = findViewById(R.id.editTextStartDate);
        edtEndDate = findViewById(R.id.editTextEndDate);
        edtAddress = findViewById(R.id.editTextAddress);

        txtLatLng = findViewById(R.id.textLatLng);
        
        btnPickLocation = findViewById(R.id.buttonPickLocation);
        btnChoosePicture = findViewById(R.id.buttonUploadImages);
        btnSaveSublet = findViewById(R.id.buttonSaveSublet);

        //onMapReady(googleMap);

        //Select sublet/room location
        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation != null){
                    txtLatLng.setText(selectedLocation.toString());
                }else {
                    Toast.makeText(AddSubletActivity.this, "Please select location on map.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Save Sublet Details to database
        btnSaveSublet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtTitle.getText().toString();
                String desc = edtDesc.getText().toString();
                String price = edtPrice.getText().toString();
                String startDate = edtStartDate.getText().toString();
                String endDate = edtEndDate.getText().toString();
                String address = edtAddress.getText().toString();
                String subletType = spinnerSubletType.getSelectedItem().toString();
                String selectedlocation = selectedLocation.toString();
                
                saveSublet(title, subletType, desc, startDate, endDate, price, address, selectedlocation);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Set a default location (e.g., Sydney) and zoom level
        LatLng defaultLocation = new LatLng(50.7234, 10.4546);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Clear any existing markers and add a new one
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                selectedLocation = latLng; // Save the selected location
            }
        });
    }

    private void saveSublet(String title, String subletType, String desc, String startDate, String endDate, String price, String address, String selectedlocation) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();

        SubletModel subletModel = new SubletModel(title, subletType, desc, startDate, endDate, price, address, selectedlocation);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sublet Details");
        databaseReference.child(firebaseUser.getUid()).setValue(subletModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Send email verification
                    firebaseUser.sendEmailVerification();

                    Toast.makeText(AddSubletActivity.this, "Sublet Added successful.", Toast.LENGTH_LONG).show();

                    /*// Open user profile after successful registration
                    Intent intent = new Intent(AddSubletActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    finish(); // close register activity

                }else {
                    Toast.makeText(AddSubletActivity.this, "Failed to save sublet.", Toast.LENGTH_LONG).show();
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}