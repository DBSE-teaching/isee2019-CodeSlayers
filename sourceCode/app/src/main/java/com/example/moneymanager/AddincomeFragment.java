package com.example.moneymanager;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.Calendar;

public class AddincomeFragment extends Fragment {
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addincome,container,false);
        moneydb = new DatabaseHelper(getActivity());

        Cursor res = moneydb.getAllIncomeCategory();
        if (res.getCount() == 0) {
            Log.d("myTag", "No data found");
        }
        ArrayList<String> CategoryIncomeList = new ArrayList<String>();
        while (res.moveToNext()) {
            CategoryIncomeList.add(res.getString(1));
        }

        Spinner mySpinner = (Spinner) view.findViewById(R.id.categoryOptions);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                CategoryIncomeList);
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
                            boolean insertData = moneydb.insertIncomeData(Integer.parseInt(editAmount.getText().toString()),
                                    day,
                                    month,
                                    year,
                                    category.getSelectedItem().toString(),
                                    account.getSelectedItem().toString(),
                                    null,
                                    notes.getText().toString());
                            if (insertData == true) {
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
