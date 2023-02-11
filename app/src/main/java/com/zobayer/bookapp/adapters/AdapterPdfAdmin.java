package com.zobayer.bookapp.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.zobayer.bookapp.MyApplication;
import com.zobayer.bookapp.activites.PdfDetailActivity;
import com.zobayer.bookapp.activites.PdfEditActivity;
import com.zobayer.bookapp.databinding.RowPdfAdminBinding;
import com.zobayer.bookapp.filters.FilterPdfAdmin;
import com.zobayer.bookapp.models.ModelPdf;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    //context
    private Context context;

    //arraylist to hold list of data of type Modelclass
    public ArrayList<ModelPdf> pdfArrayList, filterList;

    //row_pdf_admin view binding
    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filter;

    private static final String TAG ="PDF_ADAPTER_TAG";

    //progreess
    private ProgressDialog progressDialog;

    //contructor of avobe
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        //init progress
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind layout using view binding
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfAdmin.HolderPdfAdmin holder, int position) {

        // get data, set data, handle click
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        // we need to convert timestamp to dd/MM/yyyy
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        //we will need these functions many time, so insteady of writting again and asain move them to MyApplication class and make static to use later



        // load further details like category , pdf from url, pdf size in separate functions
       /* all function move on MyApplication

        //loadCategory(model, holder);
       // loadPdfFromUrl(model,holder);
       // loadPdfSize(model, holder);

        */

        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryTv

        );


        // we don't need page number here, pass null
        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar,
                null


        );

        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv
        );


        // handle click, show dialog with options pdf fromurl , pdf size in separate functions
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });

        //handle click, open pdf details page, pass pdf/book id to get details of it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });
    }

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {

        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        // options to show in dialog
        String[] options = {"Edit","Delete"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // handle dialog options click
                        if(which == 0)
                        {
                            //Edit clicked, Open PdfEditActivity to edit the book info
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId",bookId);
                            context.startActivity(intent);


                        }else if(which == 1){
                            // Delete clicked
                            MyApplication.deleteBook(
                                    context,
                                    ""+bookId,
                                    ""+bookUrl,
                                    ""+bookTitle

                            );
                           // deleteBook(model,holder);
                        }
                    }
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return pdfArrayList.size(); // return number of records
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterPdfAdmin(filterList, this);
        }

        return filter;
    }

    // view holder class for row_pdf_xml;
    class HolderPdfAdmin extends  RecyclerView.ViewHolder{

        PDFView pdfView;
        TextView titleTv,dateTv,descriptionTv,categoryTv,sizeTv;
        ProgressBar progressBar;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            pdfView = binding.pdfView;
            titleTv = binding.titleTv;
            dateTv = binding.dateTv;
            descriptionTv =binding.descriptionTv;
            categoryTv = binding.categoryTv;
            //sizeTv = binding.sizeTv;
            moreBtn = binding.moreBtn;
            progressBar = binding.progressBar;
        }
    }
}
