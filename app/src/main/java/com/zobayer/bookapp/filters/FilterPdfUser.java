package com.zobayer.bookapp.filters;

import android.widget.Filter;

import com.zobayer.bookapp.adapters.AdapterPdfUser;
import com.zobayer.bookapp.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {

    //arraylist in which we want to search
    ArrayList<ModelPdf> filterList;
    //adapter in which filter need to be imploement
    AdapterPdfUser adapterPdfUser;


    //contructor
    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // value to be searched show not be null/empty
        if(constraint!= null || constraint.length() > 0){
            // not null nor empty
            //change to uppercase or lower case to avoid case sensitivety
            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelPdf> filteredModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++)
            {
                // validate
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint));
                {
                    //search matches, add to list
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            // empty or null , return original list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;// dont miss it
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {

        //apply filter canges
        adapterPdfUser.pdfArrayList = (ArrayList<ModelPdf>)results.values;

        // notify changes
        adapterPdfUser.notifyDataSetChanged();
    }
}
