package com.example.blogging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private EditText titlepost;
    private EditText despost;
    private Uri imageuri = null;
    private Button subpost;
    private ImageView selectimage;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        selectimage = (ImageView) findViewById(R.id.image);
        titlepost = (EditText) findViewById(R.id.title);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        despost = (EditText) findViewById(R.id.desc);
        subpost = (Button) findViewById(R.id.submit);
        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        subpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageuri = data.getData();
            selectimage.setImageURI(imageuri);
        }
    }

    private void startPosting() {
        final String titleval = titlepost.getText().toString();
        final String descval = despost.getText().toString();
        if (!TextUtils.isEmpty(titleval) && !TextUtils.isEmpty(descval) && imageuri != null) {

            mProgressDialog = new ProgressDialog(PostActivity.this);
            mProgressDialog.setTitle("Uploading Image...");
            mProgressDialog.setMessage("Please wait while image is uploading");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();
            final StorageReference filepath = storageReference.child("profile_images").child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           String url = uri.toString();
                           Toast.makeText(PostActivity.this, "Done", Toast.LENGTH_LONG).show();
                           DatabaseReference newPost = databaseReference.push();
                           newPost.child("title").setValue(titleval);
                           newPost.child("description").setValue(descval);
                           newPost.child("images").setValue(url);
                           Toast.makeText(PostActivity.this, "Done", Toast.LENGTH_LONG).show();
                           mProgressDialog.dismiss();
                       }
                   });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.setTitle("Uploading error.");
                    mProgressDialog.setMessage("Try Again");
                    mProgressDialog.show();
                }
            });
        }
    }
}
