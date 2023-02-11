package com.zobayer.bookapp.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zobayer.bookapp.databinding.ActivityCategoryAddBinding;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {

    //view binding
    private ActivityCategoryAddBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progressbar dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //configure progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please  wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        //handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    String category = "";
    private void validateData() {

        // defore adding validate data

        //get data

        category = binding.categoryEt.getText().toString().trim();

        //validate if no empty
        if(TextUtils.isEmpty(category))
        {
            Toast.makeText(CategoryAddActivity.this, "Please enter category...!", Toast.LENGTH_SHORT).show();
        }else{
            addCategoryFirebase();
        }

    }

    private void addCategoryFirebase() {
        // show progress
        progressDialog.setMessage("Adding category...");

        //get Timestamp
        long timestamp = System.currentTimeMillis();


        //save info to add in firebasae db
        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("timeStamp",""+timestamp);
        hashMap.put("uid",""+firebaseAuth.getUid());

        //add to firebase db........ database Root> CatagoryId> category Info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //category add success
                        Toast.makeText(CategoryAddActivity.this, "Category upgrade successfully....", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //category add failes
                         progressDialog.dismiss();

                        Toast.makeText(CategoryAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}