package com.zobayer.bookapp.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zobayer.bookapp.databinding.ActivityPdfAddBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {

    // view binding
    private ActivityPdfAddBinding binding;

    //firebaseauth
    private FirebaseAuth firebaseAuth;

    //progresshdialog
    private ProgressDialog progressDialog;

    //arraylist to hold pdf categories
    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;

    //uir of picked pdf
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;

    //tag for debugging
    private static final String TAG = "ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();

        //setup progress dialog

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //handle click, to back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click attach pdf File
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        //handle click pick category
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });


        //handle click, upload pdf
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate data
                validateData();

            }
        });

    }

    String title ="",description="";
    private void validateData() {
        // step-1 validate data
        Log.d(TAG,"validateDate: validating  data...");

        //get data
        title = binding.titleEt.getText().toString().trim();
        description = binding.descrptionEt.getText().toString().trim();



        //valid data
        if(TextUtils.isEmpty(title))
        {
            Toast.makeText(this, "Please enter Title...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please Enter Description...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(selectedCategoryTitle))
        {
            Toast.makeText(this, "Select Category...", Toast.LENGTH_SHORT).show();
        }

        else if(pdfUri == null)
        {
            Toast.makeText(this, "Please Pick PDF...!", Toast.LENGTH_SHORT).show();
        }
        else{
            // all data is valid
            uploadPdfToStorage();
        }
    }


    // ###################### Upload PDF To Firebase Storage ########################
    private void uploadPdfToStorage() {
        // step-2 upload pdf to firebase storage
        Log.d(TAG,"uploadPdfToStorage: Uploading To Storage...");

        //show progressbar
        progressDialog.setMessage("Uploading PDF...");
        progressDialog.show();

        //timestamp
        long timestamp = System.currentTimeMillis();


        // path od pdf in firabase storage
        String filePathAndName = "Books/"+timestamp;

        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSuccess: PDF Uploaded to storage...");
                        Log.d(TAG,"onSuccess: getting pdf url...");


                        //get pdf url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedPdfUrl =""+uriTask.getResult();

                        //uploaded to firebase db
                        uploadPdfInfoToDb(uploadedPdfUrl,timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: PDF upload failed due to "+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "PDF upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    // ###################### Upload PDF Info to Firebase Database ########################
    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {
        // step-2 upload pdf info to firebase db
        Log.d(TAG,"uploadPdfToStorage: Uploading pdf info to firebase db...");

        progressDialog.setTitle("Uploading pdf info...");

        String uid = firebaseAuth.getUid();

        //setup data to upload,
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);
        hashMap.put("url",""+uploadedPdfUrl);
        hashMap.put("timestamp",timestamp);

        //also add view count, download count
        hashMap.put("viewsCount",0);
        hashMap.put("downloadsCount",0);



        //db reference : db>books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        Log.d(TAG,"onSuccess: Successfully uploaded...");
                        Toast.makeText(PdfAddActivity.this, "Successfully Uploaded...", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Log.d(TAG,"onFailure: Failed to Upload to Database to due to"+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "Failed to Upload to Database to due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


    // ###################### Load PDF Category ########################
    //Load pdf Categories
    private void loadPdfCategories() {
        Log.d(TAG,"loadPdfCategories: Loading pdf Category...");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        //db reference to load categories ....db>categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear(); //clear before adding data
                categoryIdArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    // get id and title of category
                  String categoryId = ""+ds.child("id").getValue();
                  String categoryTitle = ""+ds.child("category").getValue();

                  //add to recpective arraylist
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // ###################### Pick Category and save this on these category ########################
    //picked category

    //selected category id and category title
    private String selectedCategoryId,  selectedCategoryTitle ;
    private void categoryPickDialog() {
        //first we need to get categroies from firebase
        Log.d(TAG,"categoryPickDialog: showing category pick dialog");

        //get String array of categories from arrylist
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for(int i = 0; i< categoryTitleArrayList.size(); i++)
        {
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item
                        //get clicked item from list
                        selectedCategoryTitle = categoryTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);

                        //set to category textview
                        binding.categoryTv.setText(selectedCategoryTitle);

                        Log.d(TAG,"onClick: Selected Categroy:  "+selectedCategoryId+" "+selectedCategoryTitle);

                    }
                })
                .show();
    }




    private void pdfPickIntent() {
        Log.d(TAG,"pdfPickIntent:starting pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent.createChooser(intent,"Select PDF"),PDF_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (requestCode ==PDF_PICK_CODE){
                Log.d(TAG,"onActivityResult: PDF Picked");
                pdfUri = data.getData();

                Log.d(TAG,"onActivityResult: URI: "+pdfUri);


            }
        }else{
            Log.d(TAG,"onActivityResult: Cancelled picking PDF");
            Toast.makeText(this, "Cancelled Picking pdf", Toast.LENGTH_SHORT).show();

        }
    }
}