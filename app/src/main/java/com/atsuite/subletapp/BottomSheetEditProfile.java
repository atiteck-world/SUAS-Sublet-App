package com.atsuite.subletapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BottomSheetEditProfile extends BottomSheetDialogFragment {

    private EditText editFullName, editEmail, editAddress, editMobile, editGender, editDOB;
    private Button btnSave;
    private DatabaseReference userRef;
    private String currentUserUid;

    public BottomSheetEditProfile() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false);

        editFullName = view.findViewById(R.id.editFullname);
        editEmail = view.findViewById(R.id.editEmail);
        editAddress = view.findViewById(R.id.editAddress);
        editMobile = view.findViewById(R.id.editMobile);
        editGender = view.findViewById(R.id.editGender);
        editDOB = view.findViewById(R.id.editDOB);
        btnSave = view.findViewById(R.id.btnUpdate);

        // Get the current user's UID
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the current user's data in Firebase
        userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        populateUserData();

        btnSave.setOnClickListener(v -> {

            saveUserData();

            // Dismiss the bottom sheet
            dismiss();
        });

        return view;
    }

    private void populateUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String mobile = snapshot.child("phone").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);

                    if (!TextUtils.isEmpty(fullName)) editFullName.setText(fullName);
                    if (!TextUtils.isEmpty(email)) editEmail.setText(email);
                    if (!TextUtils.isEmpty(address)) editAddress.setText(address);
                    if (!TextUtils.isEmpty(mobile)) editMobile.setText(mobile);
                    if (!TextUtils.isEmpty(gender)) editGender.setText(gender);
                    if (!TextUtils.isEmpty(dob)) editDOB.setText(dob);
                } else {
                    Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();
        String gender = editGender.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();

        if (validateInputs(fullName, email, address, mobile, gender, dob)) {
            userRef.child("fullName").setValue(fullName);
            userRef.child("email").setValue(email);
            userRef.child("address").setValue(address);
            userRef.child("phone").setValue(mobile);
            userRef.child("gender").setValue(gender);
            userRef.child("dob").setValue(dob).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if(getContext() != null){
                            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if(getContext() != null){
                            Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    dismiss();
                }
            });
        }
    }

    private boolean validateInputs(String fullName, String email, String address, String mobile, String gender, String dob) {
        if (TextUtils.isEmpty(fullName)) {
            editFullName.setError("Full name is required");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            editAddress.setError("Address is required");
            return false;
        }
        if (TextUtils.isEmpty(mobile)) {
            editMobile.setError("Mobile number is required");
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            editGender.setError("Gender is required");
            return false;
        }
        if (TextUtils.isEmpty(dob)) {
            editDOB.setError("Date of Birth is required");
            return false;
        }
        return true;
    }
}

