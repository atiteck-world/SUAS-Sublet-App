package com.atsuite.subletapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SubletAdapter extends RecyclerView.Adapter<SubletAdapter.holder>{

    Context context;

    private List<SubletModel> subletModelList;
    private List<SubletModel> subletModelListFull; // Full copy of the sublets list for filtering

    public SubletAdapter(Context context, List<SubletModel> subletModelList) {
        this.context = context;
        this.subletModelList = subletModelList;
        this.subletModelListFull = new ArrayList<>(subletModelList);
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_item, parent, false);
        return new holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        SubletModel subletModel = subletModelList.get(position);
        holder.txt_type.setText("Type: " + subletModel.type + "/" + subletModel.title);
        holder.txt_amount.setText("Amount: " + "€" + subletModel.price + " per month");
        holder.txt_duration.setText("Duration: " + subletModel.startDate + " - " + subletModel.endDate);

        // Load image into imageView using picasso
        String imageUrl = subletModel.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.sublet_logo)
                    .error(R.drawable.sublet_logo)
                    .into(holder.img_room_picture);
        }else {
            holder.img_room_picture.setImageResource(R.drawable.sublet_logo);
        }

        // Handle click event on RecyclerView item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubletDetailsActivity.class);
            intent.putExtra("type", subletModel.getType() + "/" + subletModel.title);
            intent.putExtra("price", "€" + subletModel.getPrice() + " per month");
            intent.putExtra("duration", subletModel.getStartDate() + " - " + subletModel.getEndDate());
            intent.putExtra("description", subletModel.getDesc());
            intent.putExtra("location", subletModel.getSelectedLocation());
            intent.putExtra("userUid", subletModel.getSubletUid());
            intent.putExtra("roomImage", subletModel.getImageUrl());
            //intent.putExtra("lng", lng);
            //intent.putExtra("imageUrl", subletModel.getImageUrl());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return subletModelList.size();
    }

    // Filtering method
    public void filter(String text) {
        List<SubletModel> filteredList = new ArrayList<>();

        for (SubletModel item : subletModelList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    item.getType().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDesc().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        subletModelList.clear();
        subletModelList.addAll(filteredList);
        notifyDataSetChanged();
    }
    public static class holder extends RecyclerView.ViewHolder{

        TextView txt_type;
        TextView txt_amount;
        TextView txt_duration;
        ImageView img_room_picture;
        public holder(@NonNull View itemView) {
            super(itemView);

            txt_type = itemView.findViewById(R.id.txt_type);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_duration = itemView.findViewById(R.id.txt_duration);
            img_room_picture = itemView.findViewById(R.id.room_picture);
        }
    }
}
