package com.atsuite.subletapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditSubletBottomSheet extends BottomSheetDialogFragment {

    private EditText editTextTitle, editTextDescription, editTextPrice, editTextStartDate, editTextEndDate, editTextAddress;
    private Spinner spinnerSubletType;
    private Button buttonUpdateSublet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_edit_sublet, container, false);

        // Initialize views
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextStartDate = view.findViewById(R.id.editTextStartDate);
        editTextEndDate = view.findViewById(R.id.editTextEndDate);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        spinnerSubletType = view.findViewById(R.id.spinnerSubletType);
        buttonUpdateSublet = view.findViewById(R.id.buttonSaveSublet);

        // Set up click listener for save button
        buttonUpdateSublet.setOnClickListener(v -> {
            updateSublet();
        });

        return view;
    }

    private void updateSublet() {
        // Gather input data
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String type = spinnerSubletType.getSelectedItem().toString();

        // Here you can add the logic to save the sublet to your database
        // For example, using Firebase to save the data

        // Dismiss the bottom sheet after saving
        dismiss();
    }
}

