package com.atsuite.subletapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SubletDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    List<SubletModel> subletModelList;

    private TextView textViewTitle, textViewDescription, textViewPrice, textViewAvailability, textViewSubletterName;
    private ImageView imgRoomPicture;
    String subletterEmail, subletterPhone;
    private Button buttonContactByEmail, buttonContactByCall, buttonContactBySMS;
    private MapView mapView;
    private GoogleMap googleMap;

    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sublet_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sublet Details");

        // Initialize views
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewAvailability = findViewById(R.id.textViewAvailability);
        textViewSubletterName = findViewById(R.id.textViewSubletterName);

        imgRoomPicture = findViewById(R.id.room_picture);

        buttonContactByEmail = findViewById(R.id.buttonContactByEmail);
        buttonContactByCall = findViewById(R.id.buttonContactByCall);
        buttonContactBySMS = findViewById(R.id.buttonContactBySMS);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(getApplicationContext());

        // Retrieve data from intent
        Intent intent = getIntent();
        String userUid = intent.getStringExtra("userUid");
        String type = intent.getStringExtra("type");
        String price = intent.getStringExtra("price");
        String duration = intent.getStringExtra("duration");
        String description = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        String roomUrl = intent.getStringExtra("roomImage");

        String[] latLng = location.replace("lat/lng: (", "").replace(")", "").split(",");

        lat = Double.parseDouble(latLng[0].trim());
        lng = Double.parseDouble(latLng[1].trim());
        //String imageUrl = intent.getStringExtra("imageUrl");

        // Set data to views
        textViewTitle.setText(type);
        textViewDescription.setText(description);
        textViewPrice.setText(price);
        textViewAvailability.setText(duration);

        if (roomUrl != null && !roomUrl.isEmpty()) {
            Picasso.get()
                    .load(roomUrl)
                    .placeholder(R.drawable.sublet_logo)
                    .error(R.drawable.sublet_logo)
                    .into(imgRoomPicture);
        }else {
            imgRoomPicture.setImageResource(R.drawable.sublet_logo);
        }
        //textViewLocation.setText(location);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users").child(userUid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String subletterName = snapshot.child("fullName").getValue(String.class);
                    subletterEmail = snapshot.child("email").getValue(String.class);
                    subletterPhone = snapshot.child("phone").getValue(String.class);

                    // Set subletter name
                    textViewSubletterName.setText("Subletter Details: " + subletterName + " / " + subletterEmail + " / " + "+" + subletterPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubletDetailsActivity.this, "Something went wrong. Subletter details not found.", Toast.LENGTH_LONG).show();
            }
        });

        buttonContactByEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(subletterEmail);
            }
        });

        //buttonContactByEmail.setOnClickListener(v -> sendEmail(subletterEmail));
        buttonContactByCall.setOnClickListener(v -> makePhoneCall(subletterPhone));
        buttonContactBySMS.setOnClickListener(v -> sendSMS(subletterPhone));
    }

    private void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        //intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiries About Your Sublet");
        startActivity(Intent.createChooser(intent, "Choose an email client"));

    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);

    }

    private void sendSMS(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        startActivity(intent);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng subletLocation = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(subletLocation).title("Sublet Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(subletLocation, 15));
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

    //@Override
    //protected void onLowMemory() {
    // super.onLowMemory();
    // mapView.onLowMemory();
    //}
}