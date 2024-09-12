package com.atsuite.subletapp;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class uploadProfilePicActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imageView;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private  Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_profile_pic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnChoosePic = findViewById(R.id.btn_choose_pic);
        Button btnUploadPic = findViewById(R.id.btn_upload_pic);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.img_profile_pic);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("UserPictures");

        Uri uri = firebaseUser.getPhotoUrl();

        int width = 360;
        int height = 300;

        if (width > 0 && height > 0){
            // Set users current Dp in imageview (if uploaded already)
            Picasso.get().load(uri).resize(width, height).centerInside().into(imageView);
        }else {
            Toast.makeText(uploadProfilePicActivity.this, "Image dimensions cannot be less than zero.", Toast.LENGTH_LONG).show();
        }


        // Choose pic
        btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        //Upload pic
        btnUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                uploadPicture();
            }
        });

    }

    private void uploadPicture() {
        if(uriImage != null){
            StorageReference fileReference = storageReference.child(auth.getCurrentUser().getUid() + "." + getFileExtention(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                        }
                    });

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(uploadProfilePicActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(uploadProfilePicActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(uploadProfilePicActivity.this, "No picture was selected.", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            Picasso.get()
                    .load(uriImage)
                    .resize(imageView.getWidth(), imageView.getHeight())  // Resizes the image to fit the ImageView
                    .centerInside()  // Keeps the aspect ratio
                    .into(imageView);
        }
    }
}