package com.zobayer.bookapp.filters;

import android.widget.Filter;

import com.zobayer.bookapp.adapters.AdapterCategory;
import com.zobayer.bookapp.models.ModelCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {

    //array in which as want to search
    ArrayList<ModelCategory> filterList;

    // adapter in which filter need to be implemented
    AdapterCategory adapterCategory;

    //contructor
    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should no be null and empty

        if(constraint != null && constraint.length()>0)
        {


            // chaneg to upper class or lower case avoid case sensative
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filterModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++)
            {
                // add to filtermodels
                if(filterList.get(i).getCategory().toUpperCase().contains(constraint)){
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


        adapterCategory.categoryArrayList = (ArrayList<ModelCategory>)results.values;

        adapterCategory.notifyDataSetChanged();

    }
}
