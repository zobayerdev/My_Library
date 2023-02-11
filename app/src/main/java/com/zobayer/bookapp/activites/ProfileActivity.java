package com.zobayer.bookapp.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zobayer.bookapp.MyApplication;
import com.zobayer.bookapp.R;
//import com.zobayer.bookapp.adapters.AdapterPdfFavorite;
import com.zobayer.bookapp.databinding.ActivityPdfDetailBinding;
import com.zobayer.bookapp.databinding.ActivityProfileBinding;
import com.zobayer.bookapp.models.ModelPdf;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {



    //view binding
    private ActivityProfileBinding binding;

    private FirebaseAuth firebaseAuth;

    // arraylist  to hold the books
    private ArrayList<ModelPdf> pdfArrayList ;

    //adapter to set in recycelerview
  //  private AdapterPdfFavorite adapterPdfFavorite;

    private static  final String TAG ="PROFILE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

       // loadFavoriteBooks();

        //handle click, start profile edit page
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info...");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get all info of user from snapsot
                        String email = ""+snapshot.child("email").getValue();
                        String name =  ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();
                        String favouritebook = ""+snapshot.child("bookId").getValue();


                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data to ui

                        binding.nameTv.setText(name);
                        binding.emailTv.setText(email);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);
                      //  binding.favoriteBookCountTv.setText(favouritebook);



                        //setup image, using glide
                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_gray)
                                .into(binding.profileIv);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


   /*
    private void loadFavoriteBooks() {
        //init list
        pdfArrayList = new ArrayList<>();

        //load favorite books from database
        //Users > userId>favourite
        DatabaseReference ref = FirebaseDatabase .getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favourites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // clear list before starting  adding data
                        pdfArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            //we will only get the bookId here , and we get other details in adapter using that bookId
                            String bookId = ""+ds.child("bookId").getValue();

                            // set id to model
                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            //add model to list
                            pdfArrayList.add(modelPdf);

                        }

                        //set number of favorite books
                        binding.favoriteBookCountTv.setText(pdfArrayList.size()); // can't set int/long to textview  so concat with string

                        // setup adapter
                        adapterPdfFavorite = new AdapterPdfFavorite(ProfileActivity.this, pdfArrayList);
                        //set adapter to recyclerview
                        binding.booksRv.setAdapter(adapterPdfFavorite);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }*/
}