package com.zobayer.bookapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zobayer.bookapp.adapters.AdapterPdfUser;
import com.zobayer.bookapp.databinding.FragmentBooksUserBinding;
import com.zobayer.bookapp.models.ModelPdf;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksUserFragment extends Fragment {

    // that we passed while instance of this fragment
   private String categoryId;
   private String category;
   private String uid;
   
   private ArrayList<ModelPdf>  pdfArrayList;
   private AdapterPdfUser adapterPdfUser;
   
   //view binding
    private FragmentBooksUserBinding binding;
    
    private static final  String TAG = "BOOKS_USER_TAG";
    
    
    public BooksUserFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static BooksUserFragment newInstance(String categoryId, String category, String uid) {
        BooksUserFragment fragment = new BooksUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId",categoryId);
        args.putString("category",category);
        args.putString("uid",uid);
        
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate/bind the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(getContext()), container, false);

        Log.d(TAG, "onCreateView: Category "+category);
        if(category.equals("All")){
            //load all daata
            loadAllBooks();
            
        }
        else if(category.equals("Most Viewed"))
        {
            // load most view books
            loadMostViewedDownloadedBooks("viewsCount");
        }
        else if(category.equals("Most Downloaded"))
        {
            //load mos downloads books
            loadMostViewedDownloadedBooks("downloadsCount");
        }
        else{
            // load hoibe amar selected item
            loadCategorizedBooks();
        }

        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                try{
                    adapterPdfUser.getFilter().filter(s);

                }catch (Exception e)
                {
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
                //called as and when user type any letter

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        
        return binding.getRoot();
    }


    private void loadAllBooks() {
        //init list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               pdfArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    // get data
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    // add to list
                    pdfArrayList.add(model);

                }
                // setup adatper
                adapterPdfUser  = new AdapterPdfUser(getContext(), pdfArrayList);
                // set adapter to recyclerview
                binding.bookRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadMostViewedDownloadedBooks(String orderBy) {
        //init list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(orderBy).limitToFirst(10) // load most views or downloaded books 10 books or more
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    // get data
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    // add to list
                    pdfArrayList.add(model);

                }
                // setup adatper
                adapterPdfUser  = new AdapterPdfUser(getContext(), pdfArrayList);
                // set adapter to recyclerview
                binding.bookRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadCategorizedBooks(){
        //init list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId) // load most views or downloaded books 10 books or more
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            // get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            // add to list
                            pdfArrayList.add(model);

                        }
                        // setup adatper
                        adapterPdfUser  = new AdapterPdfUser(getContext(), pdfArrayList);
                        // set adapter to recyclerview
                        binding.bookRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}