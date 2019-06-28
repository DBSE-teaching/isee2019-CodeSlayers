package com.example.moneymanager;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
    BarChart chartBar;
    PieChart chart;

    TextView selectDateTag;
    TextView categoryFilterTag;

    String[] monthss;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_visualfragment,container,false);

        MainActivity.isSummaryShown = false;

        chartBar = (BarChart) view.findViewById(R.id.chartBar);
        chart  = (PieChart) view.findViewById(R.id.chart);

        getActivity().setTitle("Visual Statistics");

        String categoryValues[] = getResources().getStringArray(R.array.Category_n);
        moneydb = new DatabaseHelper(getActivity());

        getDetails = (Button) view.findViewById(R.id.getDetails);

        monthss = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Integer.toString(i + 1);
        }
        Spinner mySpinner = (Spinner) view.findViewById(R.id.monthList);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                monthss);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[25];
        for (int i = 0; i < 25; i++) {
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

        String[] filterOptions = {"No Filter", "Monthly", "Yearly - All Expenses", "Yearly - Specific Category"};
        Spinner mySpinner4 = (Spinner) view.findViewById(R.id.filterOptions);
        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                filterOptions);
        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner4.setAdapter(myAdapter4);

        monthSelected = (Spinner) view.findViewById(R.id.monthList);
        yearSelected = (Spinner) view.findViewById(R.id.yearList);
        filterOptionsSelected = (Spinner) view.findViewById(R.id.filterOptions);
        categorySelected = (Spinner) view.findViewById(R.id.categoryFilterOptions);

        selectDateTag = (TextView) view.findViewById(R.id.selectDateTag);
        categoryFilterTag = (TextView) view.findViewById(R.id.categoryFilterTag);



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
        chartSelectedData();
        filterView();
        return view;
    }

    private void setupPieChart(ArrayList<String> CategoryExpenseList){
        List<PieEntry> pieEntries = new ArrayList<>();
        final int[] COLORFUL_COLORS = {
                Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53),Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
                Color.rgb(191, 134, 134), Color.rgb(179, 48, 80), Color.rgb(217, 80, 138), Color.rgb(254, 149, 7)
        };
        for (int k = 0; k < CategoryExpenseList.size(); k++) {
            if(expenseValues[k] != 0)
            pieEntries.add(new PieEntry(expenseValues[k],CategoryExpenseList.get(k)));
        }
        PieDataSet dataset = new PieDataSet(pieEntries, "");
        dataset.setColors(COLORFUL_COLORS);
        dataset.setValueTextSize(15);
        PieData data = new PieData(dataset);


        chart.setData(data);
        chart.getLegend().setWordWrapEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.invalidate();
        chartBar.setVisibility(View.GONE);
        chart.setVisibility(View.VISIBLE);
    }

    private void setupBarChart(ArrayList<String> CategoryExpenseList){

        int currentYear = Integer.parseInt(yearSelected.getSelectedItem().toString());
        //int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String categoryS = categorySelected.getSelectedItem().toString();
        List<Integer> barEntries = new ArrayList<>();

        for(int monthCounter = 1; monthCounter < 13; monthCounter++){
            Cursor monthlyCategory = moneydb.getSelectedExpenseSumCategory(currentYear, monthCounter, categoryS);
            monthlyCategory.moveToFirst();
            if(monthlyCategory.getString(0) == null){
                barEntries.add(0);
            } else{
                barEntries.add(monthlyCategory.getInt(0));
            }
        }

        List<String> xLabels = new ArrayList<>();
        xLabels.add("Jan");
        xLabels.add("Feb");
        xLabels.add("Mar");
        xLabels.add("Apr");
        xLabels.add("May");
        xLabels.add("Jun");
        xLabels.add("Jul");
        xLabels.add("Aug");
        xLabels.add("Sep");
        xLabels.add("Oct");
        xLabels.add("Nov");
        xLabels.add("Dec");

        create_graph(xLabels,barEntries);

    }

    private void setupBarYearlyChart(ArrayList<String> CategoryExpenseList){


        int currentYear = Integer.parseInt(yearSelected.getSelectedItem().toString());
        //int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<Integer> barEntries = new ArrayList<>();

        for(int monthCounter = 1; monthCounter < 13; monthCounter++){
            Cursor monthlyCategory = moneydb.getSelectedExpenseSum(currentYear, monthCounter);
            monthlyCategory.moveToFirst();
            if(monthlyCategory.getString(0) == null){
                barEntries.add(0);
            } else{
                barEntries.add(monthlyCategory.getInt(0));
            }
        }

        List<String> xLabels = new ArrayList<>();
        xLabels.add("Jan");
        xLabels.add("Feb");
        xLabels.add("Mar");
        xLabels.add("Apr");
        xLabels.add("May");
        xLabels.add("Jun");
        xLabels.add("Jul");
        xLabels.add("Aug");
        xLabels.add("Sep");
        xLabels.add("Oct");
        xLabels.add("Nov");
        xLabels.add("Dec");

        create_graph(xLabels,barEntries);

    }


    public Cursor checkFilterOption() {
        String selected = filterOptionsSelected.getSelectedItem().toString();

        switch (selected) {
            case "No Filter": {
                Cursor res = moneydb.getAllData();
                return res;

            }
            case "Monthly": {
                int month = 0;
                for(int i=0; i<12; i++){
                    if(monthss[i].equals(monthSelected.getSelectedItem().toString())){
                        month = i+1;
                    }
                }
                //int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                Cursor res = moneydb.getSelectedAllData(year, month);
                return res;

            }
            case "Yearly - Specific Category": {
                String categoryS = categorySelected.getSelectedItem().toString();
                Cursor res = moneydb.getSelectedCategoryData(categoryS);
                return res;

            }
            case "Yearly - All Expenses": {
                int month = 0;
                for(int i=0; i<12; i++){
                    if(monthss[i].equals(monthSelected.getSelectedItem().toString())){
                        month = i+1;
                    }
                }
                //int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
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

                        String selected = filterOptionsSelected.getSelectedItem().toString();

                        if(selected.equals("Yearly - Specific Category")){
                            setupBarChart(CategoryExpenseList);
                        }else if (selected.equals("Yearly - All Expenses")){
                            setupBarYearlyChart(CategoryExpenseList);
                        }
                        else {
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
                }
        );
    }

    public void create_graph(List<String> graph_label, List<Integer> userScore) {

        try {
            chartBar.setDrawBarShadow(false);
            chartBar.setDrawValueAboveBar(true);
            chartBar.getDescription().setEnabled(false);
            chartBar.setPinchZoom(false);

            chartBar.setDrawGridBackground(false);


            YAxis yAxis = chartBar.getAxisLeft();
            yAxis.setValueFormatter(new IndexAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.valueOf((int) value);
                }
            });

            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);


            yAxis.setGranularity(1f);
            yAxis.setGranularityEnabled(true);



            chartBar.getAxisRight().setEnabled(false);


            XAxis xAxis = chartBar.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            //xAxis.setCenterAxisLabels(true);
            //xAxis.setDrawGridLines(true);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(graph_label));
            xAxis.setLabelCount(12);

            List<BarEntry> yVals1 = new ArrayList<BarEntry>();

            for (int i = 0; i < userScore.size(); i++) {
                yVals1.add(new BarEntry(i, userScore.get(i)));
            }


            BarDataSet set1;

            if (chartBar.getData() != null && chartBar.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) chartBar.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                chartBar.getData().notifyDataChanged();
                chartBar.notifyDataSetChanged();
            } else {
                // create 2 datasets with different types
                set1 = new BarDataSet(yVals1, "Expenses");
                set1.setColor(Color.rgb(255, 204, 0));


                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
                chartBar.setData(data);


            }

            chartBar.setFitBars(true);

            Legend l = chartBar.getLegend();
            l.setFormSize(12f); // set the size of the legend forms/shapes
            l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used


            //l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
            l.setTextSize(10f);
            l.setTextColor(Color.BLACK);
            l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis

            chartBar.invalidate();
            chartBar.animateY(2000);
            chart.setVisibility(View.GONE);
            chartBar.setVisibility(View.VISIBLE);

        } catch (Exception ignored) {
        }
    }

    public class LabelFormatter implements IAxisValueFormatter {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
    }

    public void filterView() {
        filterOptionsSelected.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = filterOptionsSelected.getSelectedItem().toString();

                switch (position) {
                    case 0:
                        categorySelected.setVisibility(View.GONE);
                        monthSelected.setVisibility(View.GONE);
                        yearSelected.setVisibility(View.GONE);
                        selectDateTag.setVisibility(View.GONE);
                        categoryFilterTag.setVisibility(View.GONE);
                        break;
                    case 1:
                        categorySelected.setVisibility(View.GONE);
                        monthSelected.setVisibility(View.VISIBLE);
                        yearSelected.setVisibility(View.VISIBLE);
                        selectDateTag.setVisibility(View.VISIBLE);
                        categoryFilterTag.setVisibility(View.GONE);
                        break;
                    case 2:
                        categorySelected.setVisibility(View.GONE);
                        monthSelected.setVisibility(View.GONE);
                        yearSelected.setVisibility(View.VISIBLE);
                        selectDateTag.setVisibility(View.VISIBLE);
                        categoryFilterTag.setVisibility(View.GONE);
                        break;
                    case 3:
                        categorySelected.setVisibility(View.VISIBLE);
                        monthSelected.setVisibility(View.GONE);
                        yearSelected.setVisibility(View.VISIBLE);
                        selectDateTag.setVisibility(View.VISIBLE);
                        categoryFilterTag.setVisibility(View.VISIBLE);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

