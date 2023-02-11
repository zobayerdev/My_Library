package com.zobayer.bookapp.filters;

import android.widget.Filter;

import com.zobayer.bookapp.adapters.AdapterCategory;
import com.zobayer.bookapp.adapters.AdapterPdfAdmin;
import com.zobayer.bookapp.models.ModelCategory;
import com.zobayer.bookapp.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {

    //array in which as want to search
    ArrayList<ModelPdf> filterList;

    // adapter in which filter need to be implemented
    AdapterPdfAdmin adapterPdfAdmin;

    //contructor
    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin  = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should no be null and empty

        if(constraint != null && constraint.length()>0)
        {

            // chaneg to upper class or lower case avoid case sensative
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++)
            {
                // add to filtermodels
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    filterModels.add(filterList.get(i));
                }

            }

            results.count = filterModels.size();
            results.values = filterModels;

        }
        else
        {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results; //don't null it and miss it
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {


        adapterPdfAdmin.pdfArrayList = (ArrayList<ModelPdf>)results.values;

        adapterPdfAdmin.notifyDataSetChanged();

    }
}
