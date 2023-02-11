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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zobayer.bookapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    //view binding
    private ActivityLoginBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    // progress ba
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebaseauth
        firebaseAuth = FirebaseAuth.getInstance();

        //init progressbar apps
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);



        //handle click, go to the register
        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        //handle click begin login
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validUserdata();
            }
        });
    }

    String email = "",password ="";
    private void validUserdata() {
        // Before login , lets do some valid data
        //get data

        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        // valided data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getApplicationContext(), "Invalid Email Pattern...!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Enter Password...!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //data is valid so login begin
            loginUser();
        }


    }

    private void loginUser() {
        // show progress
        progressDialog.setTitle("Loging in...!");
        progressDialog.show();

        //login user
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                   //login successfull, check if user is user or admin
                   checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });



    }

    private void checkUser() {
        progressDialog.setMessage("Checking User...");
        //check is user is user or admin realtime database
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //check in DB
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        //get user type
                        String userType = ""+snapshot.child("userType").getValue();
                        // check user type
                        if(userType.equals("user"))
                        {
                            // this is a simple , open dashboard
                            Intent intent = new Intent(LoginActivity.this, DashboardUserActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "Welcome To Dashboard", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                        else if(userType.equals("admin"))
                        {
                            // this is an admin, open admin dashboard
                            Intent intent = new Intent(LoginActivity.this, DashboardAdminActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "Welcome Boss", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}