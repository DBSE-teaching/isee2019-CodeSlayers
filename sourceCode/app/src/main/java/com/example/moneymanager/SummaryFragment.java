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
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

        moneyDatabase = new DatabaseHelper(getActivity());
        setupBarChart();

        return view;
    }

    public void viewCurrentMonthExpenseData() {
        ArrayList<String> listItemAmount = new ArrayList<>();
        ArrayList<String> listItemDate = new ArrayList<>();
        ArrayList<String> listItemCategory = new ArrayList<>();
        ArrayList<String> listItemID= new ArrayList<>();
        Cursor res = moneyDatabase.getSelectedAllData(currentYear,currentMonth);
        if (res.getCount() == 0) {
            //listItemAmount.add("No expenses found");
        }
        while (res.moveToNext()) {
            String item = new String();
            item = res.getString(1) + "$ for " + res.getString(3) + " on "
                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
            listItemAmount.add(res.getString(1));
            listItemDate.add(res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2));
            listItemCategory.add(res.getString(3));
            listItemID.add(res.getString(0));
        }
        CustomAdaptor customAdaptor = new CustomAdaptor(listItemAmount, listItemDate, listItemCategory,listItemID);
        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

        myExpenseListView.setAdapter(customAdaptor);
        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listExpenseItem);
        //myExpenseListView.setAdapter(adapter);
    }

    public void viewCurrentMonthIncomeData() {
        ArrayList<String> listItemAmount = new ArrayList<>();
        ArrayList<String> listItemDate = new ArrayList<>();
        ArrayList<String> listItemCategory = new ArrayList<>();
        ArrayList<String> listItemID= new ArrayList<>();
        Cursor res = moneyDatabase.getSelectedAllIncomeData(currentYear,currentMonth);
        if (res.getCount() == 0) {
            //listItemAmount.add("No income found");
        }
        while (res.moveToNext()) {
            String item = new String();
            item = res.getString(1) + "$ by " + res.getString(3) + " on "
                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
            listItemAmount.add(res.getString(1));
            listItemDate.add(res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2));
            listItemCategory.add(res.getString(3));
            listItemID.add(res.getString(0));
        }
        CustomAdaptor customAdaptor = new CustomAdaptor(listItemAmount, listItemDate, listItemCategory,listItemID);
        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

        myIncomeListView.setAdapter(customAdaptor);
        //adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listIncomeItem);
        //myIncomeListView.setAdapter(adapter2);
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
                set1 = new BarDataSet(yVals2, "Expense");
                set1.setColor(Color.rgb(0, 255, 0));
                set2 = new BarDataSet(yVals1, "Threshold");
                set2.setColor(Color.rgb(255, 0, 0));


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

            String myString = res.getString(1);
            int maxLength = 4;
            if (myString.length() > maxLength)
                myString = myString.substring(0, maxLength);

            xLabels.add(myString);

            groupCount++;
        }

        create_graph(xLabels,yVals1,yVals2);

    }

    class CustomAdaptor extends BaseAdapter {
        ArrayList<String> listItemAmount = new ArrayList<>();
        ArrayList<String> listItemDate = new ArrayList<>();
        ArrayList<String> listItemCategory = new ArrayList<>();
        ArrayList<String> listItemID= new ArrayList<>();

        CustomAdaptor(ArrayList<String> listItemAmountt, ArrayList<String> listItemDatee, ArrayList<String> listItemCategoryy, ArrayList<String> listItemIDD ){
            for(String s : listItemAmountt){
                listItemAmount.add(s);
            }
            for(String s : listItemDatee){
                listItemDate.add(s);
            }
            for(String s : listItemCategoryy){
                listItemCategory.add(s);
            }
            for(String s : listItemIDD){
                listItemID.add(s);
            }
        }

        @Override
        public int getCount() {
            return listItemAmount.size();
        }

        @Override
        public Object getItem(int position) {
            return listItemID.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.listview_layout, null);



            //String imageViewName = "R.drawable." + listItemCategory.get(position).toLowerCase();

            ImageView mImageView = (ImageView) view.findViewById(R.id.imageView);
            TextView mTextView = (TextView) view.findViewById(R.id.textView);
            TextView mTextView2 = (TextView) view.findViewById(R.id.textView2);
            TextView mTextView3 = (TextView) view.findViewById(R.id.textView3);

            switch (listItemCategory.get(position).toLowerCase()){
                case "food":
                    mImageView.setImageResource(R.drawable.food);
                    break;
                case "travel":
                    mImageView.setImageResource(R.drawable.travel);
                    break;
                case "education":
                    mImageView.setImageResource(R.drawable.education);
                    break;
                case "utilities":
                    mImageView.setImageResource(R.drawable.utilities);
                    break;
                case "shopping":
                    mImageView.setImageResource(R.drawable.shopping);
                    break;
                case "household":
                    mImageView.setImageResource(R.drawable.household);
                    break;
                case "medical":
                    mImageView.setImageResource(R.drawable.medical);
                    break;
                case "salary":
                    mImageView.setImageResource(R.drawable.salary);
                    break;
                case "allowance":
                    mImageView.setImageResource(R.drawable.allowance);
                    break;
                default:
                    mImageView.setImageResource(R.drawable.others);
                    break;
            }

            //mImageView.setImageResource(R.drawable.log);
            mTextView.setText(listItemAmount.get(position) + "$");
            mTextView2.setText(listItemDate.get(position));
            mTextView3.setText(listItemCategory.get(position));

            return view;
        }
    }

}

