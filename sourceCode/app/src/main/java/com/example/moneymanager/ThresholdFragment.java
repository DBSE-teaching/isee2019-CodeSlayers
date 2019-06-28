package com.example.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ThresholdFragment extends Fragment {
    View view;

    DatabaseHelper moneydb;


    Button addThreshold;
    EditText newThresholdValue;
    Button submit1;
    Button submit2;


    ArrayAdapter adapter;
    ArrayAdapter adapter2;
    ListView myListView1;
    ListView myListView2;

    TextView header1;
    TextView header2;


    Spinner mySpinner;

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

    BarChart barChart;

    int groupCount;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_threshold, container, false);
        moneydb = new DatabaseHelper(getActivity());

        MainActivity.isSummaryShown = false;

        barChart = (BarChart) view.findViewById(R.id.barChart);

        getActivity().setTitle("Threshold");

        addThreshold = (Button) view.findViewById(R.id.addThreshold);
        newThresholdValue = (EditText) view.findViewById(R.id.thresholdValue);

        submit1 = (Button) view.findViewById(R.id.submit1);
        submit2 = (Button) view.findViewById(R.id.submit2);
        header1 = (TextView) view.findViewById(R.id.headerExpense);
        header2 = (TextView) view.findViewById(R.id.headerExpense2);
        myListView1 = (ListView) view.findViewById(R.id.myListView1);
        Context context = getActivity();
        TextView header = new TextView(context);
        header.setText("");
        myListView1.addHeaderView(header);

        myListView2 = (ListView) view.findViewById(R.id.myListView2);
        Context context2 = getActivity();
        TextView header2 = new TextView(context);
        header2.setText("");
        myListView2.addHeaderView(header2);


        AddNewThresholdValue();
        viewAll1();
        viewChart();

// Setting initial values for threshold
        Cursor res=moneydb.getThreshValue();
        if(res.getCount()==0){
            boolean insertThreshold1 = moneydb.insertThreshold("Food", 0);
            if (insertThreshold1== false) {
                Log.d("myTag", "Threshold not inserted");
            }
            boolean insertThreshold2 = moneydb.insertThreshold("Shopping", 0);
            if (insertThreshold2 == false) {
                Log.d("myTag", "Threshold not inserted");
            }
            boolean insertThreshold3 = moneydb.insertThreshold("Entertainment", 0);
            if (insertThreshold3 == false) {
                Log.d("myTag", "Threshold not inserted");
            }
            boolean insertThreshold4 = moneydb.insertThreshold("Travel", 0);
            if (insertThreshold4 == false) {
                Log.d("myTag", "Threshold not inserted");
            }
            boolean insertThreshold5 = moneydb.insertThreshold("Others", 0);
            if (insertThreshold5 == false) {
                Log.d("myTag", "Threshold not inserted");
            }
        }

        /* Monthly threshold value*/




        ArrayList<String> CategoryExpenseList = new ArrayList<String>();
        while (res.moveToNext()) {
            CategoryExpenseList.add(res.getString(1));
        }




        mySpinner = (Spinner) view.findViewById(R.id.categoryOptions1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                CategoryExpenseList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        barChart.setVisibility(View.GONE);
        viewThresholdandExpenseData();
        return view;

    }
    public void AddNewThresholdValue(){
        addThreshold.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String numberString1 = "^[0-9]\\d*(\\.\\d+)?$";
                        if (newThresholdValue.getText().toString().matches(numberString1)){

                            boolean insertThreshold = moneydb.setThreshold(mySpinner.getSelectedItem().toString(), Integer.parseInt(newThresholdValue.getText().toString()));

                            if (insertThreshold == true) {
                                Toast.makeText(getActivity(), "Threshold inserted", Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                        , new ThresholdFragment()).commit();
                            }else
                                Toast.makeText(getActivity(), "Threshold not inserted", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getActivity(),"Enter Threshold",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
    public void viewAll1() {


        submit1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> listItem = new ArrayList<>();
                        ArrayList<String> listItem2 = new ArrayList<>();
                        String category =mySpinner.getSelectedItem().toString();
                        Cursor res = moneydb.getThreshValue();
                        if (res.getCount() == 0) {
                            Toast.makeText(getActivity(), "no threshold", Toast.LENGTH_LONG).show();
                        }


                        while (res.moveToNext()) {
                            String item = new String();
                            item = res.getString(0) + ". " + res.getString(1) + ": " + res.getString(2) ;
                            listItem.add(item);

                            Cursor res2 = moneydb.getSum(res.getString(1),currentMonth,currentYear);
                            res2.moveToFirst();
                            item = res.getString(0) + ". " + res.getString(1) + ": " + res2.getString(0) ;
                            listItem2.add(item);

                        }
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);
                        adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem2);

                        myListView1.setAdapter(adapter);
                        myListView2.setAdapter(adapter2);
                        barChart.setVisibility(View.GONE);
                        myListView1.setVisibility(View.VISIBLE);
                        myListView2.setVisibility(View.VISIBLE);
                        header1.setVisibility(View.VISIBLE);
                        header2.setVisibility(View.VISIBLE);


                    }
                }

        );

    }


    public void viewThresholdandExpenseData(){
        ArrayList<String> listItem = new ArrayList<>();
        ArrayList<String> listItem2 = new ArrayList<>();
        String category =mySpinner.getSelectedItem().toString();
        Cursor res = moneydb.getThreshValue();
        if (res.getCount() == 0) {
            Toast.makeText(getActivity(), "no threshold", Toast.LENGTH_LONG).show();
        }


        while (res.moveToNext()) {
            String item = new String();
            item = res.getString(0) + ". " + res.getString(1) + ": " + res.getString(2) ;
            listItem.add(item);

            Cursor res2 = moneydb.getSum(res.getString(1),currentMonth,currentYear);
            res2.moveToFirst();
            item = res.getString(0) + ". " + res.getString(1) + ": " + res2.getString(0) ;
            listItem2.add(item);

        }
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);
        adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem2);

        myListView1.setAdapter(adapter);
        myListView2.setAdapter(adapter2);
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
        Cursor res = moneydb.getThreshValue();
        if (res.getCount() == 0) {
            Toast.makeText(getActivity(), "no threshold", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            yVals1.add(res.getInt(2));
            Cursor res2 = moneydb.getSum(res.getString(1),currentMonth,currentYear);
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

    public void viewChart(){
        submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListView1.setVisibility(View.GONE);
                myListView2.setVisibility(View.GONE);
                header1.setVisibility(View.GONE);
                header2.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                setupBarChart();
            }
        });
    }

}

