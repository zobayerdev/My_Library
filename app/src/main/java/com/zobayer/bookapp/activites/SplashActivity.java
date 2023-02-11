package com.zobayer.bookapp.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zobayer.bookapp.R;

public class SplashActivity extends AppCompatActivity {

    //firebase auth
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //init firebase atuh
        firebaseAuth = FirebaseAuth.getInstance();
        //start main screen after 2second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },2000);
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            // start main Activity
            Intent intent = new Intent(SplashActivity.this, DashBoardActivity.class);
            startActivity(intent);
            finish(); // finish this activity
        }
        else
        {
            // user logging in check usetype, same done in  login screen
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //get user type
                            String userType = ""+snapshot.child("userType").getValue();
                            // check user type
                            if(userType.equals("user"))
                            {
                                // this is a simple , open dashboard
                                Intent intent = new Intent(SplashActivity.this, DashboardUserActivity.class);
                                startActivity(intent);
                                Toast.makeText(SplashActivity.this, "Welcome To Dashboard", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                            else if(userType.equals("admin"))
                            {
                                // this is an admin, open admin dashboard
                                Intent intent = new Intent(SplashActivity.this, DashboardAdminActivity.class);
                                startActivity(intent);
                                Toast.makeText(SplashActivity.this, "Welcome Boss", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



        }
    }
}