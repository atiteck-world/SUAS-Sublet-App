package com.atsuite.subletapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MySubletAdapter adapter;
    private List<SubletModel> subletList;
    public ListingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listings, container, false);

        recyclerView = view.findViewById(R.id.rcv_myListings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        subletList = new ArrayList<>();
        adapter = new MySubletAdapter(getContext(), subletList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab_add_listing = view.findViewById(R.id.fab_add_listing);
        fab_add_listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddSubletActivity.class);
                startActivity(intent);
            }
        });

        loadMySubletListings();
        return view;
    }

    private void loadMySubletListings() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's UID
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sublet Details").child(currentUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subletList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Log the data snapshot before conversion: For debugging
                    System.out.println("DataSnapshot: " + snapshot.getValue());
                    try {
                        SubletModel sublet = snapshot.getValue(SubletModel.class);
                        String subletUid = snapshot.getKey();
                        sublet.setSubletUid(subletUid);
                        if (sublet != null) {
                            subletList.add(sublet); // Add the sublet to the list
                        } else {
                            Toast.makeText(getContext(), "Unexpected data format.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        //System.out.println("Error deserializing data: " + e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}