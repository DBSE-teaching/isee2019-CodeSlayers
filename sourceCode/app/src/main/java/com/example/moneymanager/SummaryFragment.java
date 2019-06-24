package com.example.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
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
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SummaryFragment extends Fragment {
    View view;

    DatabaseHelper moneyDatabase;
    ListView myExpenseListView;
    ListView myIncomeListView;

    int a,b,c;
    ArrayAdapter adapter;
    ArrayAdapter adapter2;

    int expenseValues[] = new int[10];
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

    BarChart barChart;

    int groupCount;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary, container, false);
        MainActivity.isSummaryShown = true;

        barChart = (BarChart) view.findViewById(R.id.barChart);

        getActivity().setTitle("Summary");

        moneyDatabase = new DatabaseHelper(getActivity());

        /* Getting the sum of current monthly income*/
        Cursor sumIncome = moneyDatabase.getIncomeSum();
        sumIncome.moveToFirst();
        TextView totalIncome = (TextView) view.findViewById(R.id.totalIncome);

        if(sumIncome.getString(0) == null){
            a=0;
            totalIncome.setText("Total Income:   " + "0");
        } else{
           a=Integer.parseInt(sumIncome.getString(0));
            totalIncome.setText("Total Income:   " + sumIncome.getString(0));
        }


        /* Getting the sum of current monthly expense*/
        Cursor sumExpense = moneyDatabase.getExpenseSum();
        sumExpense.moveToFirst();
        TextView totalExpense = (TextView) view.findViewById(R.id.totalExpense);

        if(sumExpense.getString(0) == null){
            totalExpense.setText("Total Expense:   " + "0");
            b=0;
        }else {
            totalExpense.setText("Total Expense:   " + sumExpense.getString(0));
            b = Integer.parseInt(sumExpense.getString(0));
        }

        c = a-b;
        TextView totalBalance = (TextView) view.findViewById(R.id.totalBalance);
        totalBalance.setText("Balance:   " + c + " ");

        /* Getting the sum of current monthly income*/
        TextView totalMonthlyExpense = (TextView) view.findViewById(R.id.headerExpense);
        Cursor sumExpenseMonth = moneyDatabase.getSelectedExpenseSum(currentYear,currentMonth);
        sumExpenseMonth.moveToFirst();

        if(sumExpenseMonth.getString(0) == null){
            totalMonthlyExpense.setText("This month expenses: " + "0");
        } else{
            totalMonthlyExpense.setText("This month expenses: " + sumExpenseMonth.getString(0));
        }

        /* Getting the sum of current monthly income*/
        TextView totalMonthlyIncome = (TextView) view.findViewById(R.id.headerIncome);
        Cursor sumIncomeMonth = moneyDatabase.getSelectedSumAllIncomeData(currentYear,currentMonth);
        sumIncomeMonth.moveToFirst();

        if(sumIncomeMonth.getString(0) == null){
            totalMonthlyIncome.setText("This month incomes: " + "0");
        } else{
            totalMonthlyIncome.setText("This month incomes: " + sumIncomeMonth.getString(0));
        }

        Context context = getActivity();

        myExpenseListView = (ListView) view.findViewById(R.id.myListViewExpense);

        myIncomeListView = (ListView) view.findViewById(R.id.myListViewIncome);

        viewCurrentMonthExpenseData();
        viewCurrentMonthIncomeData();

        String categoryValues[] = getResources().getStringArray(R.array.Category_n);
        moneyDatabase = new DatabaseHelper(getActivity());
        Cursor res3 = moneyDatabase.getSelectedAllData(currentYear,currentMonth);
        if (res3.getCount() == 0) {
            Toast.makeText(getActivity(),"No expense data",Toast.LENGTH_LONG).show();
        }else{
            while (res3.moveToNext()) {
                for (int k = 0; k < categoryValues.length; k++) {
                    if(categoryValues[k].equals(res3.getString(3))){
                        expenseValues[k] += res3.getInt(1);
                    }
                }
            }
            //setupThisMonthPieChart(categoryValues);
            setupBarChart();
        }



        return view;


    }

    public void viewCurrentMonthExpenseData() {
        ArrayList<String> listExpenseItem = new ArrayList<>();
        Cursor res = moneyDatabase.getSelectedAllData(currentYear,currentMonth);
        if (res.getCount() == 0) {
            listExpenseItem.add("No expenses found");
        }
        while (res.moveToNext()) {
            String item = new String();
            item = res.getString(1) + "$ for " + res.getString(3) + " on "
                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
            listExpenseItem.add(item);
        }
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listExpenseItem);
        myExpenseListView.setAdapter(adapter);
    }

    public void viewCurrentMonthIncomeData() {
        ArrayList<String> listIncomeItem = new ArrayList<>();
        Cursor res = moneyDatabase.getSelectedAllIncomeData(currentYear,currentMonth);
        if (res.getCount() == 0) {
            listIncomeItem.add("No income found");
        }
        while (res.moveToNext()) {
            String item = new String();
            item = res.getString(1) + "$ by " + res.getString(3) + " on "
                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
            listIncomeItem.add(item);
        }
        adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listIncomeItem);
        myIncomeListView.setAdapter(adapter2);
    }



    private void setChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for(int monthCounter = 1; monthCounter < 9; monthCounter++){
            Cursor monthlyCategory = moneyDatabase.getSelectedExpenseSum(currentYear, monthCounter);
            monthlyCategory.moveToFirst();
            if(monthlyCategory.getString(0) == null){
                barEntries.add(new BarEntry(monthCounter, 0));
            } else{
                barEntries.add(new BarEntry(monthCounter, monthlyCategory.getInt(0)));
            }
        }


        ArrayList<BarEntry> barEntries2 = barEntries;
        //ArrayList<BarEntry> incomeEntries = getIncomeEntries();
        //ArrayList<BarEntry> expenseEntries = getExpenseEntries();

        BarDataSet set1, set2;

        set1 = new BarDataSet(barEntries, "Income");
        set1.setColor(Color.rgb(65, 168, 121));
        set1.setValueTextColor(Color.rgb(55, 70, 73));
        set1.setValueTextSize(10f);

        set2 = new BarDataSet(barEntries2, "Expense");
        set2.setColors(Color.rgb(241, 107, 72));
        set2.setValueTextColor(Color.rgb(55, 70, 73));
        set2.setValueTextSize(10f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(set1,set2);
        barChart.setData(data);


        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        //barChart.setMaxVisibleValueCount(10);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        //barChart.animateY(1400, Easing.EaseInOutQuad);
        //barChart.animateXY(3000, 3000);

        Legend l = barChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setTextSize(14);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);

        ArrayList<String> xLabels = new ArrayList<>();
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


        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setLabelCount(8);



        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setTypeface(Typeface.DEFAULT);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true
        barChart.getAxisRight().setEnabled(false);

        float barSpace = 0.02f;
        float groupSpace = 0.3f;
        int groupCount = 7;

        //IMPORTANT *****
        data.setBarWidth(0.15f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
        //***** IMPORTANT
        barChart.invalidate();

    }

    private ArrayList<String> getFormattter() {

        ArrayList<String> xLabels = new ArrayList<>();
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

        return xLabels;
    }

    public void setaChart(){




        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        //barChart.setDescription("");
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);


        barChart.getAxisRight().setEnabled(false);

        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.46f; // x2 dataset
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"

        int startYear = 1980;
        int endYear = 1986;


        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<BarEntry> yVals2 = new ArrayList<BarEntry>();

         int i = startYear;
         Cursor res = moneyDatabase.getThreshValue();
        if (res.getCount() == 0) {
            Toast.makeText(getActivity(), "no threshold", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            yVals1.add(new BarEntry(i, res.getInt(2)));
            Cursor res2 = moneyDatabase.getSum(res.getString(1),currentMonth,currentYear);
            res2.moveToFirst();
            yVals2.add(new BarEntry(i, res2.getInt(0)));
            i++;
            Log.d("sollunga", " " + res.getInt(2));
            Log.d("sollunganna", " " + res2.getInt(0));
            Log.d("sollunganna", " " + i);
            yVals1.get(0);
        }
        Log.d("sollunganna", " " + yVals1.get(0));
        Log.d("sollunganna", " " + yVals1.get(1));
        Log.d("sollunganna", " " + yVals1.get(2));
        Log.d("sollunganna", " " + yVals1.get(3));
        Log.d("sollunganna", " " + yVals1.get(4));









        BarDataSet set1, set2;


            // create 2 datasets with different types
            set1 = new BarDataSet(yVals1, "Threshold");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(yVals2, "Amount");
            set2.setColor(Color.rgb(164, 228, 251));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);

        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("Food");
        xLabels.add("Feb");
        xLabels.add("Mar");
        xLabels.add("Apr");
        //xLabels.add("May");




        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(startYear);
        barChart.groupBars(startYear, groupSpace, barSpace);
        barChart.invalidate();
    }



    public void create_graph(List<String> graph_label, List<Integer> userScore, List<Integer> userScore2) {

        try {
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.getDescription().setEnabled(false);
            barChart.setPinchZoom(false);

            barChart.setDrawGridBackground(false);


            YAxis yAxis = barChart.getAxisLeft();
            yAxis.setValueFormatter(new IndexAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.valueOf((int) value);
                }
            });

            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);


            yAxis.setGranularity(1f);
            yAxis.setGranularityEnabled(true);

            barChart.getAxisRight().setEnabled(false);


            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setCenterAxisLabels(true);
            //xAxis.setDrawGridLines(true);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(graph_label));
            //xAxis.setLabelCount(5);

            List<BarEntry> yVals1 = new ArrayList<BarEntry>();

            for (int i = 0; i < userScore.size(); i++) {
                yVals1.add(new BarEntry(i, userScore.get(i)));
            }

            List<BarEntry> yVals2 = new ArrayList<BarEntry>();

            for (int i = 0; i < userScore2.size(); i++) {
                yVals2.add(new BarEntry(i, userScore2.get(i)));
            }



            BarDataSet set1;
            BarDataSet set2;

            if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
                set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
                set1.setValues(yVals1);
                set2.setValues(yVals2);
                barChart.getData().notifyDataChanged();
                barChart.notifyDataSetChanged();
            } else {
                // create 2 datasets with different types
                set1 = new BarDataSet(yVals1, "Threshold");
                set1.setColor(Color.rgb(255, 0, 0));
                set2 = new BarDataSet(yVals2, "Expense");
                set2.setColor(Color.rgb(0, 204, 0));


                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);
                dataSets.add(set2);

                BarData data = new BarData(dataSets);
                barChart.setData(data);
                data.setBarWidth(0.15f);

            }

            barChart.setFitBars(true);

            Legend l = barChart.getLegend();
            l.setFormSize(12f); // set the size of the legend forms/shapes
            l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used


            //l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
            l.setTextSize(10f);
            l.setTextColor(Color.BLACK);
            l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis

            float barSpace = 0.02f;
            float groupSpace = .6f;


            barChart.getXAxis().setAxisMinimum(0);
            barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
            barChart.groupBars(0, groupSpace, barSpace);

            barChart.invalidate();
            barChart.animateY(2000);

            barChart.setVisibility(View.VISIBLE);

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

    private void setupBarChart(){

        List<Integer> yVals1 = new ArrayList<Integer>();
        List<Integer> yVals2 = new ArrayList<Integer>();
        List<String> xLabels = new ArrayList<>();

        groupCount = 0;
        Cursor res = moneyDatabase.getThreshValue();
        if (res.getCount() == 0) {
            Toast.makeText(getActivity(), "no threshold", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            yVals1.add(res.getInt(2));
            Cursor res2 = moneyDatabase.getSum(res.getString(1),currentMonth,currentYear);
            res2.moveToFirst();
            yVals2.add(res2.getInt(0));
            xLabels.add(res.getString(1));

            groupCount++;
        }

        create_graph(xLabels,yVals1,yVals2);

    }


}

