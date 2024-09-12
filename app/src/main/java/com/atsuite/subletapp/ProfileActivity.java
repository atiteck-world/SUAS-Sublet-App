package com.atsuite.subletapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    String UserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sublet App");
        getSupportActionBar().setSubtitle("Explore");

        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setSelectedItemId(R.id.explore);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        Fragment selectedFragment = new ExploreFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();

        auth = FirebaseAuth.getInstance();

        UserUid = auth.getCurrentUser().getUid();

/*
        if (user != null) {
            UserUid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users").child(UserUid);
            storageReference = FirebaseStorage.getInstance().getReference("UserPictures").child(UserUid);
        }
*/

    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();
        Fragment selectedFragment = null;
        if(itemId==R.id.explore){
            selectedFragment = new ExploreFragment();
            getSupportActionBar().setSubtitle("Explore");
        } else if (itemId == R.id.listing) {
            selectedFragment = new ListingsFragment();
            getSupportActionBar().setSubtitle("My Listings");
        } else if (itemId == R.id.profile) {
            selectedFragment = new ProfileFragment();
            getSupportActionBar().setSubtitle("My Profile");
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();

        return true;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout){
            logoutUser();
        }
        if(id == R.id.delete){
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Confirm Delete", (dialog, which) ->{
                        deleteAccount();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAccount() {
        if (UserUid != null) {
            // Delete all sublets associated with the user
            DatabaseReference subletDetailsRef = FirebaseDatabase.getInstance().getReference("Sublet Details").child(UserUid);
            subletDetailsRef.removeValue().addOnSuccessListener(aVoid -> {
                //Toast.makeText(ProfileActivity.this, "User Delete successful", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Unable to Delete user sublets: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

            // Delete user profile picture from Firebase Storage
            //DatabaseReference subletUid = FirebaseDatabase.getInstance().getReference("Sublet Details").child(UserUid).getKey();
            StorageReference RoomPicRef = FirebaseStorage.getInstance().getReference("RoomPictures").child(UserUid);
            RoomPicRef.delete().addOnSuccessListener(aVoid -> {
                // Profile picture deleted
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Unable to Delete user pics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

            // Delete user profile data from Registered Users
            DatabaseReference registeredUsersRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(UserUid);
            registeredUsersRef.removeValue().addOnSuccessListener(aVoid -> {
                // User profile data deleted
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Unable to user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

            // Delete user profile picture from Firebase Storage
            StorageReference profilePicRef = FirebaseStorage.getInstance().getReference("UserPictures/" + UserUid + ".jpg");
            profilePicRef.delete().addOnSuccessListener(aVoid -> {
                // Profile picture deleted
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Unable to Delete user pics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

            // Delete user authentication data from Firebase Authentication
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                user.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Account deleted successfully
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Unable to Delete user authentications", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else {
            Toast.makeText(ProfileActivity.this, "Something went wrong. Account not deleted", Toast.LENGTH_SHORT).show();
        }
    }


    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}