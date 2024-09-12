package com.atsuite.subletapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView txtFullname, txtEmail, txtAddress, txtMobile, txtGender;
    private ImageView profileImage1;
    private CircleImageView profileImage;
    private Button btnEditProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        txtFullname = view.findViewById(R.id.txtFullname);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtAddress = view.findViewById(R.id.txtAddress);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtGender = view.findViewById(R.id.txtGender);

        profileImage = view.findViewById(R.id.profilePicture);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetEditProfile bottomSheet = new BottomSheetEditProfile();
                bottomSheet.show(getParentFragmentManager(), "BottomSheetEditProfile");
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), uploadProfilePicActivity.class);
                startActivity(intent);
            }
        });

        showProfileDetails(firebaseUser);

    }

    // Fetch data from database and display
    private void showProfileDetails(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        // Extract user reference from database for "Registered Users"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                if(readUserDetails != null){
                    String fullname = readUserDetails.getFullName().toUpperCase();
                    String address = readUserDetails.getAddress();
                    String email = readUserDetails.getEmail();
                    String mobile = readUserDetails.getPhone();
                    String gender = readUserDetails.getGender();

                    txtFullname.setText(fullname);
                    txtAddress.setText(address);
                    txtEmail.setText(email);
                    txtMobile.setText(mobile);
                    txtGender.setText(gender);

                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).resize(profileImage.getWidth(), profileImage.getHeight())  // Resizes the image to fit the ImageView
                            .centerInside().into(profileImage);
                }else {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}