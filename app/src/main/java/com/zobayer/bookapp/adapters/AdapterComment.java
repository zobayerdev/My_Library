package com.zobayer.bookapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zobayer.bookapp.MyApplication;
import com.zobayer.bookapp.R;
import com.zobayer.bookapp.databinding.RowCommentBinding;
import com.zobayer.bookapp.models.ModelComment;

import java.util.ArrayList;

public class AdapterComment  extends RecyclerView.Adapter<AdapterComment.HolderComment>{

    //context
    private Context context;

    //arraylist to hold comments
    private ArrayList<ModelComment> commentArrayList;

    private FirebaseAuth firebaseAuth;

    //view binding
    private RowCommentBinding binding;

    //constructor
    public AdapterComment(Context context, ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //iflate binding
        binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent,false);


        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        /*get data from specefic position list, set data handle click ect*/
        //get data
        ModelComment modelComment = commentArrayList.get(position);
        String id = modelComment.getId();
        String  bookId = modelComment.getBookId();
        String  comment = modelComment.getComment();
        String  uid = modelComment.getUid();
        String timestamp = modelComment.getTimestamp();


        //formate data, already made functions in MyApplication class
        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

        //set data
        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        //we don't have user's name, profile pictire , so we will load it using uid we stored in each comment
        loadUserDetails(modelComment, holder);

        //handle click, show options to delete comment
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null && uid.equals(firebaseAuth.getUid())){
                    deleteComment(modelComment, holder);

                }
            }
        });


    }

    private void deleteComment(ModelComment modelComment, HolderComment holder) {
       // show confirm dialog before deleting comment
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Comment")
                .setMessage("Are you sure want to delete this Comment...")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete from dialog clicked, begain delete
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(modelComment.getBookId())
                                .child("Comments")
                                .child(modelComment.getId())// comment id
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Delete Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to delete due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //sencel clicked
                        dialog.dismiss();
                    }
                }).show();
    }







    private void loadUserDetails(ModelComment modelComment, HolderComment holder) {

        String uid = modelComment.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        //set data
                        holder.nameTv.setText(name);
                        try{
                            Glide.with(context)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(holder.profileIv);
                        }catch(Exception e){
                            holder.profileIv.setImageResource(R.drawable.ic_person_gray);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }





    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    //view holder class for row-Comment.xml
    class HolderComment extends RecyclerView.ViewHolder{

        //ui views of row_commnet.xml
        ShapeableImageView profileIv;
        TextView nameTv,commentTv,dateTv;


        public HolderComment(@NonNull View itemView) {
            super(itemView);

            profileIv = binding.profileIv;
            nameTv = binding.nameTv;
            commentTv = binding.commentTv;
            dateTv = binding.dateTv;
        }
    }
}
