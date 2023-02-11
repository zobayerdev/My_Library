package com.zobayer.bookapp.activites;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zobayer.bookapp.MyApplication;
import com.zobayer.bookapp.R;
//import com.zobayer.bookapp.adapters.AdapterPdfFavorite;
import com.zobayer.bookapp.adapters.AdapterComment;
import com.zobayer.bookapp.databinding.ActivityPdfDetailBinding;
import com.zobayer.bookapp.databinding.DialogCommentAddBinding;
import com.zobayer.bookapp.models.ModelComment;
import com.zobayer.bookapp.models.ModelPdf;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfDetailActivity extends AppCompatActivity {

    // view binding
    private ActivityPdfDetailBinding binding;

    private static final String TAG_DOWNLOAD ="DOWNLOAD_TAG";

    //progress bar
    private ProgressDialog progressDialog;

    private ArrayList<ModelComment> commentArrayList;
    //adapter to set recyclerview
    private AdapterComment adapterComment;

    //pdf id, get from intent
    String bookId,bookTitle, bookUrl;

    boolean isMyFavourite =false;

    private  FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent e.g bookId
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");//requires bookId, so lets pass intent


        //at start hide download button, because we need book url that we will load later in function loadBookDetails();
        binding.downloadBookBtn.setVisibility(View.GONE);

        //init progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            checkIsFavourite();
        }


        loadBookDetails();
        loadComments();
        //increment book view count, whenever this page starts
        MyApplication.incrementBookViewCount(bookId);

        // need to do in profile not pdf details

        //handle click, goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click, open to view pdf in apps
        binding.readBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
                intent1.putExtra("bookId",bookId);
                startActivity(intent1); // gone this activity
            }
        });

        // ############### books download korar options #################
        // handle click , download pdf
        // #############################################################
        binding.downloadBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_DOWNLOAD, "onClick: Checking Permission");
                if(ContextCompat.checkSelfPermission(PdfDetailActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG_DOWNLOAD, "onClick: Permission already granted, you can download book");
                    MyApplication.downloadBook(PdfDetailActivity.this,""+bookId, ""+bookTitle,""+bookUrl);
                }
                else{
                    Log.d(TAG_DOWNLOAD, "onClick: Permission was not granted, request permission");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        //handle click, add/remove favourite
        binding.favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(PdfDetailActivity.this, "You are not logged in...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(isMyFavourite){
                        //in favourite, removve from favourite
                        MyApplication.removeFromFavourite(PdfDetailActivity.this,bookId);

                    }
                    else{
                        //not in favourite, add to favourite
                        MyApplication.addToFavourite(PdfDetailActivity.this,bookId);

                    }
                }
            }
        });

        //handle click, show comment add dialog
        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Requirements: user must be logged in to add commnet*/
                if(firebaseAuth.getCurrentUser() == null)
                {
                    Toast.makeText(PdfDetailActivity.this, "You are not logged in this apps", Toast.LENGTH_SHORT).show();
                }else{
                    addCommentDialog();
                }
            }
        });



    }

    private void loadComments() {
        //init arraylist before adding data inite it
        commentArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // clear arraylist before sart add data into ot
                        commentArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            //get data as model
                            ModelComment model = ds.getValue(ModelComment.class);
                            //add to arraylist
                            commentArrayList.add(model);

                        }

                        //set adapter
                        adapterComment = new AdapterComment(PdfDetailActivity.this,commentArrayList);
                        //set adapter to recycler
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String comment =" ";
    private void addCommentDialog() {
        //inflate bind view for dialog
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        // setup alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());

        //create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //handle click, dismiss comment
        commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        // handle click, add commnet
        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data
                comment = commentAddBinding.commentEt.getText().toString().trim();

                //validate comment
                if(TextUtils.isEmpty(comment)){
                    Toast.makeText(PdfDetailActivity.this, "Please write your comment", Toast.LENGTH_SHORT).show();
                }
                else{
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });

    }

    private void addComment() {
        // show progress
        progressDialog.setMessage("Submit your Comment");
        progressDialog.show();

        // timestamp for comment id, comment time
        String timestamp = ""+System.currentTimeMillis();

        //setup data to add in db for comment
        HashMap<String , Object>hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("bookId",""+bookId);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("comment",""+comment);
        hashMap.put("uid",""+firebaseAuth.getUid());


        //Db path to add data into it
        //Books>bookId>Comments>timestamp>commentdata
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PdfDetailActivity.this, "Comment added successfully...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed to add comment
                        progressDialog.dismiss();
                        Toast.makeText(PdfDetailActivity.this, "Failed to add comment due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }// now we need to show the comment

    // #######################################################################################
      // #######################################################################################
     // #######################################################################################
    //request premission to download books to storage
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if(isGranted)
                {
                    Log.d(TAG_DOWNLOAD, "Permission Granted ");
                    MyApplication.downloadBook(this,""+bookId,""+bookTitle,""+bookUrl);
                }
                else{
                    Log.d(TAG_DOWNLOAD, "Permission was denied...: ");
                    Toast.makeText(this, "Permission was Denied...", Toast.LENGTH_SHORT).show();
                }
            });


    private void loadBookDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        bookTitle = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        bookUrl = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        //required data is loaded, show downloaded button
                        binding.downloadBookBtn.setVisibility(View.VISIBLE);


                        // format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(
                                ""+categoryId,
                                binding.categoryTv
                        );
                        MyApplication.loadPdfFromUrlSinglePage(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.pdfView,
                                binding.progressBar,
                                binding.pagesTv
                        );

                        MyApplication.loadPdfSize(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.sizeTv
                        );

            /*            // remove
                        MyApplication.loadPdfPageCount(
                                PdfDetailActivity.this,
                                ""+bookUrl,
                                binding.pagesTv);
*/
                        //set data
                        binding.titleTv.setText(bookTitle);
                        binding.descriptionTv.setText(description);
                        binding.viewsTv.setText(viewsCount.replace("null","N/A"));
                        binding.downloadsTv.setText(downloadsCount.replace("null","N/A"));
                        binding.dateTv.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    //checking favourite
    private void checkIsFavourite(){
            //logged in check its in favoerites list or not
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Favourites").child(bookId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            isMyFavourite = snapshot.exists();
                            if(isMyFavourite){
                                // exsist in favorite
                                binding.favouriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite,0,0);
                                binding.favouriteBtn.setText("Remove Favourite");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {


                            binding.favouriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_border_white,0,0);
                            binding.favouriteBtn.setText("Add Favourite");

                        }
                    });


    }
}