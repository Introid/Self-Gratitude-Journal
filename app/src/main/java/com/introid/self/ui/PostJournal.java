package com.introid.self.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.introid.self.R;
import com.introid.self.util.JournalApi;

import java.util.Collection;

public class PostJournal extends AppCompatActivity {
    private static final int GALERY_CODE = 1;
    private TextView postUserName;
    private ImageView postImage;
    private ImageView cameraButton;
    private EditText postTitle;
    private EditText postDescription;
    private Button post;
    ProgressBar postProgressBar;

    private String currentUserId;
    private String currentUserName;

    private  FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);

        firebaseAuth= FirebaseAuth.getInstance();

        postUserName= findViewById(R.id.post_user_name);
        postImage= findViewById(R.id.back_img);
        cameraButton= findViewById(R.id.postCameraButton);
        postTitle=findViewById(R.id.title_post);
        postDescription=findViewById(R.id.description_post);
        post=findViewById(R.id.post);
        postDescription=findViewById(R.id.progressbar_post);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),GALERY_CODE);


//                Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent,GALERY_CODE);

            }
        });


     if (JournalApi.getInstance() != null){
         currentUserId= JournalApi.getInstance().getUserId();
         currentUserName= JournalApi.getInstance().getUsername();

         postUserName.setText(currentUserName);
     }
     authStateListener = new FirebaseAuth.AuthStateListener() {
         @Override
         public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                  user= firebaseAuth.getCurrentUser();
                  if (user != null){

                  }else{

                  }
         }
     };

    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.post:
//
//                break;
//            case R.id.postCameraButton:
//                Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent,GALERY_CODE);
//
//                break;
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERY_CODE){
            assert data != null;
            imageUri= data.getData();
            postImage.setImageURI(imageUri);
        }


//        if (requestCode == GALERY_CODE && resultCode == RESULT_OK) {
//            if (data != null) {
//                imageUri=data.getData();
//                postImage.setImageURI(imageUri); // show image
//            }
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        user= firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (firebaseAuth != null){
//            firebaseAuth.removeAuthStateListener(authStateListener);
//        }
//    }
}
