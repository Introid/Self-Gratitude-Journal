package com.introid.self.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.introid.self.R;
import com.introid.self.ui.PostJournal;
import com.introid.self.util.JournalApi;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity {
    ProgressBar progressBarCreateAccount;
    private EditText emailCreateAccount;
    private EditText create_username;
    private EditText passwordCreateAccount;
    private Button signUp;
    TextView signinGoBack;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference= db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth= FirebaseAuth.getInstance();

        signinGoBack=findViewById(R.id.sign_in_create);

        progressBarCreateAccount = findViewById(R.id.progress_create_acc);

        emailCreateAccount=findViewById(R.id.email_create_acc);
        create_username = findViewById(R.id.username);
        passwordCreateAccount= findViewById(R.id.password_create_acc);
        signUp= findViewById(R.id.email_signUp_btn);



        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser= firebaseAuth.getCurrentUser();
                if(currentUser != null){

                }else{

                }
            }
        };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(emailCreateAccount.getText().toString())
                && !TextUtils.isEmpty(passwordCreateAccount.getText().toString())
                && !TextUtils.isEmpty(create_username.getText().toString())){

                    String email= emailCreateAccount.getText().toString().trim();
                    String password= passwordCreateAccount.getText().toString().trim();
                    String username= create_username.getText().toString().trim();
                    createUserEmailAccount(email,password,username);

                }else{

                    Snackbar snackbar= Snackbar.make(signinGoBack, "Empty Fields Not Allowed", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });


    }
    private void createUserEmailAccount(String email, String password, final String username){
            if (!TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(password)
            && !TextUtils.isEmpty(username)){
                progressBarCreateAccount.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                currentUser= firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId=currentUser.getUid();

                                Map <String,String> userObj= new HashMap<>();
                                userObj.put("userId",currentUserId);
                                userObj.put("username",username);

                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            private static final String TAG = "collectionReference";

                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (Objects.requireNonNull(task.getResult()).exists()) {
                                                                        progressBarCreateAccount.setVisibility(View.INVISIBLE);
                                                                        String name = task.getResult().getString("username");
                                                                        JournalApi journalApi= JournalApi.getInstance();
                                                                        journalApi.setUserId( currentUserId);
                                                                        journalApi.setUsername(name);

                                                                          Intent intent1= new Intent(CreateAccount.this, PostJournal.class);
                                                                          intent1.putExtra("username",name);
                                                                          intent1.putExtra("userId",currentUserId);
                                                                          startActivity(intent1);
                                                                    Toast.makeText(journalApi, "post journal Activity", Toast.LENGTH_SHORT).show();
                                                                    Log.d(TAG, "onComplete: called");
                                                                }else{
                                                                    progressBarCreateAccount.setVisibility(View.INVISIBLE);

                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }else{

                            }
                        }
                    })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {

                         }
                     });
            }else{

            }

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser= firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
