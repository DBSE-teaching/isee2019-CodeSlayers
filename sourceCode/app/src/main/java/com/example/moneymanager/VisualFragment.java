package com.example.moneymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Iterator;

public class VisualFragment extends Fragment {
    View view;
    DatabaseHelper moneydb;
    int expenseValues[] = new int[20];

    Button getDetails;
    Spinner monthSelected;
    Spinner yearSelected;
    ArrayAdapter adapter;
    ListView myListView;
    Spinner filterOptionsSelected;
    Spinner categorySelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_visualfragment,container,false);
        String categoryValues[] = getResources().getStringArray(R.array.Category_n);
        moneydb = new DatabaseHelper(getActivity());

        getDetails = (Button) view.findViewById(R.id.getDetails);

        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Integer.toString(i + 1);
        }
        Spinner mySpinner = (Spinner) view.findViewById(R.id.monthList);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                months);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[25];
        for (int i = 0; i < 24; i++) {
            years[i] = Integer.toString(currentYear);
            currentYear--;
        }
        Spinner mySpinner2 = (Spinner) view.findViewById(R.id.yearList);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                years);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);

        Cursor resCategory = moneydb.getAllExpenseCategory();
        if (resCategory.getCount() == 0) {
            Log.d("myTag", "No data found");
        }
        ArrayList<String> CategoryExpenseList = new ArrayList<String>();
        while (resCategory.moveToNext()) {
            CategoryExpenseList.add(resCategory.getString(1));
        }
        Spinner mySpinner3 = (Spinner) view.findViewById(R.id.categoryFilterOptions);
        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                CategoryExpenseList);
        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner3.setAdapter(myAdapter3);

        String[] filterOptions = {"Show all", "Date Only", "Category Only", "Date and Category"};
        Spinner mySpinner4 = (Spinner) view.findViewById(R.id.filterOptions);
        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                filterOptions);
        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner4.setAdapter(myAdapter4);

        monthSelected = (Spinner) view.findViewById(R.id.monthList);
        yearSelected = (Spinner) view.findViewById(R.id.yearList);
        filterOptionsSelected = (Spinner) view.findViewById(R.id.filterOptions);
        categorySelected = (Spinner) view.findViewById(R.id.categoryFilterOptions);



        Cursor res = moneydb.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(getActivity(),"No expense data",Toast.LENGTH_LONG).show();
        }else{
            while (res.moveToNext()) {
                for (int k = 0; k < CategoryExpenseList.size(); k++) {
                    if(CategoryExpenseList.get(k).equals(res.getString(3))){
                        expenseValues[k] += res.getInt(1);
                    }
                }
            }
            setupPieChart(CategoryExpenseList);
        }
        chartSelectedData();
        return view;
    }

    private void setupPieChart(ArrayList<String> CategoryExpenseList){
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int k = 0; k < CategoryExpenseList.size(); k++) {
            pieEntries.add(new PieEntry(expenseValues[k],CategoryExpenseList.get(k)));
        }
        PieDataSet dataset = new PieDataSet(pieEntries, "Expense chart");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setValueTextSize(25);
        PieData data = new PieData(dataset);

        PieChart chart = (PieChart) view.findViewById(R.id.chart);
        chart.setData(data);
        chart.invalidate();
    }

    public Cursor checkFilterOption() {
        String selected = filterOptionsSelected.getSelectedItem().toString();

        switch (selected) {
            case "Show all": {
                Cursor res = moneydb.getAllData();
                return res;

            }
            case "Date Only": {
                int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                Cursor res = moneydb.getSelectedAllData(year, month);
                return res;

            }
            case "Category Only": {
                String categoryS = categorySelected.getSelectedItem().toString();
                Cursor res = moneydb.getSelectedCategoryData(categoryS);
                return res;

            }
            case "Date and Category": {
                int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                String categoryS = categorySelected.getSelectedItem().toString();
                Cursor res = moneydb.getSelectedCategoryAndDateData(year, month, categoryS);
                return res;

            }
        }
        Cursor res = moneydb.getAllData();
        return res;
    }

    public void chartSelectedData(){

        getDetails.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int init[] = new int[10];
                        expenseValues = init;

                        Cursor resCategory = moneydb.getAllExpenseCategory();
                        if (resCategory.getCount() == 0) {
                            Log.d("myTag", "No data found");
                        }
                        ArrayList<String> CategoryExpenseList = new ArrayList<String>();
                        while (resCategory.moveToNext()) {
                            CategoryExpenseList.add(resCategory.getString(1));
                        }

                        Cursor res = checkFilterOption();

                        if (res.getCount() == 0) {
                            Toast.makeText(getActivity(),"No expense data",Toast.LENGTH_LONG).show();
                        }else{
                            while (res.moveToNext()) {
                                for (int k = 0; k < CategoryExpenseList.size(); k++) {
                                    if(CategoryExpenseList.get(k).equals(res.getString(3))){
                                        expenseValues[k] += res.getInt(1);
                                    }
                                }

                            }

                        }
                        setupPieChart(CategoryExpenseList);
                    }
                }
        );
    }
}

