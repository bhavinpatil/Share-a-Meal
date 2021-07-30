package com.bhavinpatil.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mFullname, mEmail, mPhone, mPassword;
    Button mRegisterBtn;
    FirebaseAuth fAuth;

    ProgressBar progressBar;
    //Changes
    FirebaseFirestore fStore;
    String userID;
    //-------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFullname = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email1);
        mPhone = findViewById(R.id.phone1);
        mPassword = findViewById(R.id.password1);
        mRegisterBtn = findViewById(R.id.signupBtn);


        //keyboard




        //Changes
        fStore = FirebaseFirestore.getInstance();
        //-------------------------
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar1);

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //Changes
                String fullName = mFullname.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();

                //----------------------------------------------

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password must contain more than 6 characters");
                    return;
                }

                //register user in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signup.this, "User created", Toast.LENGTH_SHORT).show();

                            //Changes
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName",fullName);
                            user.put("email",email);
                            user.put("Phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User profile is created for"+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: "+ e.toString());
                                }
                            });

                            //-------------------------------------


                            startActivity(new Intent(getApplicationContext(), Location.class));
                        }


                        else{
                            Toast.makeText(Signup.this,"Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });




        //public void completeSignup(View view) {
        //startActivity(new Intent(getApplicationContext(), Location.class));
        //}
    }



}