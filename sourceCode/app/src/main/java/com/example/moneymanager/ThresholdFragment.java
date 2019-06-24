package com.example.moneymanager;

import android.content.Context;
import android.database.Cursor;
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

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;


public class ThresholdFragment extends Fragment {
    View view;

    DatabaseHelper moneydb;


    Button addThreshold;
    EditText newThresholdValue;
    Button submit1;


    ArrayAdapter adapter;
    ArrayAdapter adapter2;
    ListView myListView1;
    ListView myListView2;


    Spinner mySpinner;

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_threshold, container, false);
        moneydb = new DatabaseHelper(getActivity());

        addThreshold = (Button) view.findViewById(R.id.addThreshold);
        newThresholdValue = (EditText) view.findViewById(R.id.thresholdValue);

        submit1 = (Button) view.findViewById(R.id.submit1);

        myListView1 = (ListView) view.findViewById(R.id.myListView1);
        Context context = getActivity();
        TextView header = new TextView(context);
        header.setText("Threshold Values");
        myListView1.addHeaderView(header);

        myListView2 = (ListView) view.findViewById(R.id.myListView2);
        Context context2 = getActivity();
        TextView header2 = new TextView(context);
        header2.setText("This month expenses");
        myListView2.addHeaderView(header2);


        AddNewThresholdValue();
        viewAll1();

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

                    }
                }

        );

    }




}

