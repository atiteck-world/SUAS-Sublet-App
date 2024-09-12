package com.atsuite.subletapp;

//import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.Table.map;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddSubletActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private EditText edtTitle, edtDesc, edtAddress, edtPrice, edtStartDate, edtEndDate;
    private Spinner spinnerSubletType;
    private Button btnPickLocation, btnChoosePicture, btnSaveSublet;
    private ImageView imageViewRoom;
    private TextView txtLatLng;

    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    private LatLng currentLatLng;
    private static final int PICK_IMAGE_REQUEST = 1;

    private DatePickerDialog picker;
    private Uri uriImage;

    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

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
        imageViewRoom = findViewById(R.id.imageViewRoom);

        btnPickLocation = findViewById(R.id.buttonPickLocation);
        btnChoosePicture = findViewById(R.id.buttonUploadImage);
        btnSaveSublet = findViewById(R.id.buttonSaveSublet);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("RoomPictures");

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setting up datepicker on startDate editText
        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Date picker dialog
                picker = new DatePickerDialog(AddSubletActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtStartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        // Setting up datepicker on endDate editText
        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Date picker dialog
                picker = new DatePickerDialog(AddSubletActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtEndDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        //Select sublet/room location
        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation != null) {
                    String address = getAddressFromLatLng(selectedLocation);
                    txtLatLng.setText(address != null ? address : selectedLocation.toString());
                } else if (selectedLocation == null) {
                    selectedLocation = currentLatLng;
                    String address = getAddressFromLatLng(currentLatLng);
                    txtLatLng.setText(address != null ? address : selectedLocation.toString());
                    Toast.makeText(AddSubletActivity.this, "Your current location has been set as your sublet location.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddSubletActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Choose room picture
        btnChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
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

                String selectedLocationString = null;
                if (selectedLocation != null) {
                    selectedLocationString = selectedLocation.toString();
                } else {
                    Toast.makeText(AddSubletActivity.this, "Please select a location", Toast.LENGTH_LONG).show();
                    return;  // Exit the method early if no location is selected
                }

                if (validateInputs(title, subletType, desc, startDate, endDate, price, address, selectedLocationString)) {
                    saveSublet(title, subletType, desc, startDate, endDate, price, address, selectedLocationString);
                }
                //saveSublet(title, subletType, desc, startDate, endDate, price, address, selectedlocation);
            }
        });
    }

    private boolean validateInputs(String title, String subletType, String desc, String startDate, String endDate, String price, String address, String selectedlocation) {
        if (title.isEmpty()) {
            edtTitle.setError("Title is required");
            edtTitle.requestFocus();
            return false;
        }

        if (subletType.isEmpty() || subletType.equals("Select Type")) {
            Toast.makeText(this, "Please select a sublet type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (price.isEmpty()) {
            edtPrice.setError("Price is required");
            edtPrice.requestFocus();
            return false;
        }

        // Ensure price is a valid number
        try {
            double priceValue = Double.parseDouble(price);
            if (priceValue <= 0) {
                edtPrice.setError("Price must be a positive number");
                edtPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Invalid price format");
            edtPrice.requestFocus();
            return false;
        }

        if (startDate.isEmpty()) {
            edtStartDate.setError("Start date is required");
            edtStartDate.requestFocus();
            return false;
        }

        if (endDate.isEmpty()) {
            edtEndDate.setError("End date is required");
            edtEndDate.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            edtAddress.setError("Address is required");
            edtAddress.requestFocus();
            return false;
        }
        if(txtLatLng == null){
            txtLatLng.setError("Please pick a location on map");
            return false;
        }

        if (selectedlocation == null) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateDates(String startDate, String endDate) {
        // Assuming the dates are in a standard format, e.g., yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);

        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            if (start == null || end == null) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (start.after(end)) {
                Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            // Request the location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

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

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                } else {
                    Toast.makeText(AddSubletActivity.this, "Unable to get current location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                enableMyLocation();
            } else {
                // Permission was denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveSublet(String title, String subletType, String desc, String startDate, String endDate, String price, String address, String selectedlocation) {
        auth = FirebaseAuth.getInstance();

        firebaseUser = auth.getCurrentUser();

        SubletModel subletModel = new SubletModel(title, subletType, desc, startDate, endDate, price, address, selectedlocation);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sublet Details");
        // Generate a unique ID for the new sublet
        String subletId = databaseReference.child(firebaseUser.getUid()).push().getKey();
        databaseReference.child(firebaseUser.getUid()).child(subletId).setValue(subletModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    uploadPicture(subletId);

                    Toast.makeText(AddSubletActivity.this, "Sublet Added successful.", Toast.LENGTH_LONG).show();

                    finish(); // close AddSubletActivity

                }else {
                    Toast.makeText(AddSubletActivity.this, "Failed to save sublet.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // get address from latlng
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // You can get more detailed information from the Address object
                String addressLine = address.getAddressLine(0); // Get the complete address
                return addressLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadPicture(String subletId) {
        if (uriImage != null) {
            // Create a storage reference with a unique name for the picture using the sublet ID
            String fileName = subletId + "." + getFileExtention(uriImage);
            StorageReference fileReference = storageReference.child(auth.getCurrentUser().getUid()).child(subletId);

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL after the upload is successful
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Uri of the uploaded image
                            Uri downloadUri = uri;

                            // Save the download URL to the database under the appropriate sublet record
                            DatabaseReference roomReference = FirebaseDatabase.getInstance().getReference("Sublet Details")
                                    .child(auth.getCurrentUser().getUid())
                                    .child(subletId);

                            // Save the download URL to the database
                            roomReference.child("imageUrl").setValue(downloadUri.toString());

                            Toast.makeText(AddSubletActivity.this, "Room picture uploaded successfully.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failures
                    Toast.makeText(AddSubletActivity.this, "Failed to upload picture: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Handle the case where no picture was selected
            Toast.makeText(AddSubletActivity.this, "No picture was selected.", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            Picasso.get()
                    .load(uriImage)
                    .resize(imageViewRoom.getWidth(), imageViewRoom.getHeight())  // Resizes the image to fit the ImageView
                    .centerInside()  // Keeps the aspect ratio
                    .into(imageViewRoom);
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
