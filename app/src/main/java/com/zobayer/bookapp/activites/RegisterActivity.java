package com.zobayer.bookapp.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zobayer.bookapp.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //view binding
    private ActivityRegisterBinding binding;
    //Firebase auth
    private FirebaseAuth firebaseAuth;
    //progrees dialog
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /* Add following  dependencies of firebase
        * firebase abuth
        * Firebase Real-Time Database
        * firebase storage
        */


        // implement firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //init progressbar apps
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //handle click, go bak
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // handle click, begain register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });



    }

    private String name ="",email="",password = "",university="";
    private void validateData() {
        /* before creating account, lets do some data validation */
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        university = binding.universityEt.getText().toString().trim();
        String cPassword = binding.cPasswordEt.getText().toString().trim();


        //valiade data
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(getApplicationContext(), "Enter your name...!", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getApplicationContext(), "Invalid Email Pattern...!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Enter Password...!", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(cPassword))
        {
            Toast.makeText(getApplicationContext(), "Confirm Password...!", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(university))
        {
            Toast.makeText(getApplicationContext(), "University Name Please...!", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(cPassword))
        {
            Toast.makeText(getApplicationContext(), "Password Did't Match...!", Toast.LENGTH_SHORT).show();
        }
        else{
            createUserAccount();
        }
    }

    private void createUserAccount() {
        progressDialog.setMessage("Creating account......!");
        progressDialog.show();


        //create user firebase in Auth
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account creation successfull, now add in firebase database
                updateUserInfo();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // account creation
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user Info....!");

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current user uid, since user is registered so we can get now
        String uid = firebaseAuth.getUid();


        //setup data to add in fo
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("name",name);
        hashMap.put("profileImage",""); // add empty, will do after
        hashMap.put("userType","user");// possable values are users, admin: will make admin manually in firebase realtime database by change this value
        hashMap.put("timestamp",timestamp);
        hashMap.put("password",password);
        hashMap.put("university",university);

        //set data to DB
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account created...!", Toast.LENGTH_SHORT).show();

                        //since user account is created so start dashboard of user
                        Intent intent = new Intent(RegisterActivity.this, DashboardUserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //data failed adding db
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}