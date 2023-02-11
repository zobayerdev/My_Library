package com.zobayer.bookapp.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zobayer.bookapp.adapters.AdapterCategory;
import com.zobayer.bookapp.databinding.ActivityDashboardAdminBinding;
import com.zobayer.bookapp.models.ModelCategory;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {

    //view binding
    private ActivityDashboardAdminBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arraylist to store category
    private ArrayList<ModelCategory> categoryArrayList;

    //adapter
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // #################################
        //init firebasae auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategories(); /* load categories for Admin */


        // #################################
        //editText change listener Search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                //called as and when user type each letter
                try {
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // #################################
        //handle click, logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // handle click, start category add screen
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardAdminActivity.this, CategoryAddActivity.class);
                startActivity(intent);
                Toast.makeText(DashboardAdminActivity.this, "Create Category...!", Toast.LENGTH_SHORT).show();
            }
        });



        // handle click, start pdf add screen
        binding.addPdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardAdminActivity.this, PdfAddActivity.class);
                startActivity(intent);
                Toast.makeText(DashboardAdminActivity.this, "Upload Book Your Favourite Category...", Toast.LENGTH_SHORT).show();
            }
        });

        //handle clicl, open profile
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardAdminActivity.this,ProfileActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Welcome your Profile", Toast.LENGTH_SHORT).show();
            }
        });



    }


    // ############################ Function ##########################
    // load categories of admin
    // ################################################################
    private void loadCategories() {
        // init arraylis
        categoryArrayList = new ArrayList<>();
        //get all categories from databse> categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear arrayList before adding data
                categoryArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    // get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    //add to arraylist
                    categoryArrayList.add(model);
                }

                //setup adapter
                adapterCategory = new AdapterCategory (DashboardAdminActivity.this,categoryArrayList);

                //set adapter to recyclerview
                binding.categoriesRv.setAdapter(adapterCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    // user checking
    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            Intent intent = new Intent(DashboardAdminActivity.this, DashBoardActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            String email = firebaseUser.getEmail();
            //set in textview of toolbar
            binding.subTitleTv.setText(email);
        }
    }
}