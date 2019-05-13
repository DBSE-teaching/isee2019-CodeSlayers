package com.example.moneymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class VisualFragment extends Fragment {
    View view;
    DatabaseHelper moneydb;
    int expenseValues[] = new int[10];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_visualfragment,container,false);
        String categoryValues[] = getResources().getStringArray(R.array.Category_n);
        moneydb = new DatabaseHelper(getActivity());
        Cursor res = moneydb.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(getActivity(),"No expense data",Toast.LENGTH_LONG).show();
        }else{
            while (res.moveToNext()) {
                for (int k = 0; k < categoryValues.length; k++) {
                    if(categoryValues[k].equals(res.getString(3))){
                        expenseValues[k] += res.getInt(1);
                    }
                }
            }
            setupPieChart(categoryValues);
        }


        return view;
    }

    private void setupPieChart(String[] categoryValues){
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int j=0; j< categoryValues.length; j++){
            pieEntries.add(new PieEntry(expenseValues[j],categoryValues[j]));
        }

        PieDataSet dataset = new PieDataSet(pieEntries, "Expense chart");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataset);

        PieChart chart = (PieChart) view.findViewById(R.id.chart);
        chart.setData(data);
        chart.invalidate();
    }
}

