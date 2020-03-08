package com.introid.self.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.introid.self.R;
import com.introid.self.ui.PostJournal;
import com.introid.self.util.JournalApi;

public class Login extends AppCompatActivity {
    ProgressBar progressBarlogin;
    private EditText emailLogin;
    private EditText passwordlogin;
    private Button login;
    TextView Signup;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference= db.collection("Users");





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = firebaseAuth.getInstance();
        progressBarlogin=findViewById(R.id.progress);


        emailLogin= findViewById(R.id.email);

        passwordlogin= findViewById(R.id.password);

        login= findViewById(R.id.email_signin_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPasswordUser(emailLogin.getText().toString().trim(),
                        passwordlogin.getText().toString().trim());
            }

            private void loginEmailPasswordUser(String email, String password) {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;
                                    String currentUserId = user.getUid();

                                    collectionReference.whereEqualTo("userId",currentUserId)
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot
                                                                            queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                    if (e != null){

                                                    }
                                                    if (!queryDocumentSnapshots.isEmpty()){

                                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                                            JournalApi journalApi=JournalApi.getInstance();
                                                            journalApi.setUsername(snapshot.getString("username"));
                                                            journalApi.setUserId(snapshot.getString("userId"));

                                                            startActivity(new Intent(Login.this, PostJournal.class));

                                                        }


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
                    Snackbar snackbar= Snackbar.make(login, "Empty Fields Not Allowed", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        Signup= findViewById(R.id.sign_up);
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this,CreateAccount.class);
                startActivity(intent);
            }
        });
    }

}
