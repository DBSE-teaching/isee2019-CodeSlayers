package com.example.moneymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

public class AddexpenseFragment extends Fragment {
    View view;
    private EditText date;
    DatePickerDialog datePickerDialog;
    EditText editAmount;
    Spinner category;
    Spinner account;
    Spinner recurrence;
    EditText notes;
    Button addData;
    DatabaseHelper moneydb;
    int year;
    int month;
    int day;
    ThresholdFragment tf;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addexpense,container,false);
        moneydb = new DatabaseHelper(getActivity());
        tf=new ThresholdFragment();


        Cursor res = moneydb.getAllExpenseCategory();
        if (res.getCount() == 0) {
            Log.d("myTag", "No data found");
        }
        ArrayList<String> CategoryExpenseList = new ArrayList<String>();
        while (res.moveToNext()) {
            CategoryExpenseList.add(res.getString(1));
        }

        Spinner mySpinner = (Spinner) view.findViewById(R.id.categoryOptions);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                CategoryExpenseList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        Spinner mySpinner2 = (Spinner) view.findViewById(R.id.accountOptions);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Account_options));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);



        Calendar calendar = Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH) + 1;
        day=calendar.get(Calendar.DAY_OF_MONTH);

        String Today = day + "/" + month + "/" + year;
        date=view.findViewById(R.id.date);
        date.setText(Today);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2){
                        date.setText(i2+"/"+(i1+1)+"/"+i);
                        year = i;
                        month = i1+1;
                        day = i2;
                    }
                },year,month-1,day);
                datePickerDialog.show();
            }
        });

        editAmount = (EditText)view.findViewById(R.id.editText2);
        addData = (Button)view.findViewById(R.id.getexpdet);
        category = (Spinner) view.findViewById(R.id.categoryOptions);
        account = (Spinner) view.findViewById(R.id.accountOptions);

        notes = (EditText) view.findViewById(R.id.notes);
        AddData();

        return view;
    }

    public void AddData(){
        addData.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String numberString = "^[1-9]\\d*(\\.\\d+)?$";

                        if (editAmount.getText().toString().matches(numberString)){
                            boolean insertData = moneydb.insertData(Integer.parseInt(editAmount.getText().toString()),
                                    day,
                                    month,
                                    year,
                                    category.getSelectedItem().toString(),
                                    account.getSelectedItem().toString(),
                                    null,
                                    notes.getText().toString());
                            if (insertData == true) {
                                Cursor Value = moneydb.getThreshold(category.getSelectedItem().toString());
                                Value.moveToFirst();
                                int Catsel= Integer.parseInt(Value.getString(0));

                                Cursor TValue = moneydb.getSum(category.getSelectedItem().toString(),month,year);
                                TValue.moveToFirst();
                                int Sumsel= Integer.parseInt(TValue.getString(0));

                                if(Catsel > 0){
                                    if(Sumsel>Catsel){
                                        // Build an AlertDialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        // Set a title for alert dialog
                                        builder.setTitle("ALERT!");

                                        // Ask the final question
                                        builder.setMessage("Threshold exceeded");

                                        // Set click listener for alert dialog buttons
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ;
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        // User clicked the Yes button
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        // User clicked the No button
                                                        break;
                                                }
                                            }
                                        };

                                        // Set the alert dialog yes button click listener
                                        builder.setPositiveButton("Okay", dialogClickListener);

                                        // Set the alert dialog no button click listener
                                        //builder.setNegativeButton("No", dialogClickListener);

                                        AlertDialog dialog = builder.create();
                                        // Display the alert dialog on interface
                                        dialog.show();




                                    }
                                }


                                Toast.makeText(getActivity(), "Data inserted", Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                        , new IncomeFragment()).commit();
                            }else
                                Toast.makeText(getActivity(), "Data not inserted", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getActivity(),"Enter Amount",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }



}
