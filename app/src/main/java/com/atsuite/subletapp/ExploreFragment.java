package com.atsuite.subletapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SubletAdapter adapter;
    private List<SubletModel> subletList;
    private EditText editTextSearch;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        progressBar = v.findViewById(R.id.progressBar);
        recyclerView = v.findViewById(R.id.rcv_sublet);
        editTextSearch = v.findViewById(R.id.editTextSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        subletList = new ArrayList<>();
        adapter = new SubletAdapter(getContext(), subletList);
        recyclerView.setAdapter(adapter);

        loadSublets();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return v;
    }

    private void loadSublets() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sublet Details");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subletList.clear(); // Clear the list to avoid duplicates

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userUid = userSnapshot.getKey();

                    // Each userSnapshot contains sublets for one user
                    for (DataSnapshot subletSnapshot : userSnapshot.getChildren()) {
                        SubletModel sublet = subletSnapshot.getValue(SubletModel.class);
                        sublet.setSubletUid(userUid);
                        subletList.add(sublet);
                    }
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                // Log the error or show a message to the user
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}