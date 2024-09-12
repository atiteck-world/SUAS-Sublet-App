package com.atsuite.subletapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MySubletAdapter extends RecyclerView.Adapter<MySubletAdapter.holder> {
    private Context context;
    private List<SubletModel> subletModelList;

    public MySubletAdapter(Context context, List<SubletModel> subletModelList) {
        this.context = context;
        this.subletModelList = subletModelList;
    }
    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mylistings, parent, false);
        return new holder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MySubletAdapter.holder holder, @SuppressLint("RecyclerView") int position) {

        SubletModel subletModel = subletModelList.get(position);
        String subletUid = subletModel.getSubletUid();

        holder.txt_type.setText("Type: " + subletModel.getType() + " / " + subletModel.getTitle());
        holder.txt_amount.setText("Amount: €" + subletModel.getPrice() + " per month");
        holder.txt_duration.setText("Duration: " + subletModel.getStartDate() + " - " + subletModel.getEndDate());

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

        // Delete sublet
        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subletUid != null) {
                    deleteSublet(subletUid, position);
                } else {
                    Toast.makeText(context, "Cannot delete sublet: ID is null.", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
        Edit sublet
        holder.edtButton.setOnClickListener(v -> {
            Context context = v.getContext();

            // Safely unwrap the context
            while (context instanceof ContextThemeWrapper) {
                context = ((ContextThemeWrapper) context).getBaseContext();
            }

            // Ensure that the context is indeed a FragmentActivity
            if (context instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) context;
                EditSubletBottomSheet bottomSheet = new EditSubletBottomSheet();
                bottomSheet.show(activity.getSupportFragmentManager(), bottomSheet.getTag());
            } else {
                // Handle the error or fail gracefully
                Log.e("MySubletAdapter", "Context is not a FragmentActivity.");
            }
        });
*/


    }

    private void deleteSublet(String subletUid, int pos) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Sublet Details")
                .child(currentUserUid)
                .child(subletUid);

        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Sublet deleted successfully.", Toast.LENGTH_LONG).show();

                    // Delete user room picture from Firebase Storage
                    StorageReference roomPicRef = FirebaseStorage.getInstance().getReference("RoomPictures").child(currentUserUid);
                    roomPicRef.delete().addOnSuccessListener(aVoid -> {
                        // Room picture deleted
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Unable to Delete room/sublet picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                } else {
                    Toast.makeText(context, "Failed to delete sublet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subletModelList == null ? 0 : subletModelList.size();
    }

    public static class holder extends RecyclerView.ViewHolder {
        TextView txt_type;
        TextView txt_amount;
        TextView txt_duration;
        ImageView img_room_picture;
        Button delButton;

        public holder(@NonNull View itemView) {
            super(itemView);

            txt_type = itemView.findViewById(R.id.txt_type);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_duration = itemView.findViewById(R.id.txt_duration);
            img_room_picture = itemView.findViewById(R.id.room_picture);

            //edtButton = itemView.findViewById(R.id.buttonEdit);
            delButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
