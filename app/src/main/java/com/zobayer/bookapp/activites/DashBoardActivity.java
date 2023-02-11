package com.zobayer.bookapp.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zobayer.bookapp.databinding.ActivityDashBoardBinding;

public class DashBoardActivity extends AppCompatActivity {

    //view binding
    private ActivityDashBoardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //handle login click
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(DashBoardActivity.this, "Welcome To Log In", Toast.LENGTH_SHORT).show();

            }
        });

        //handle skipBtn, start continue without login
        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, AboutUsActivity.class);
                startActivity(intent);
                Toast.makeText(DashBoardActivity.this, "About Us", Toast.LENGTH_SHORT).show();

            }
        });

    }
}