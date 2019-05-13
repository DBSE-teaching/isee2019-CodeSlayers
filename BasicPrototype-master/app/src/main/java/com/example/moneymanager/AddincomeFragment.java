package com.example.moneymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
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

public class AddincomeFragment extends Fragment {
    View view;
    private EditText date;
    DatePickerDialog datePickerDialog;
    EditText editAmount;
    Spinner category;
    Spinner account;
    RadioGroup recurrence;
    EditText notes;
    Button addData;
    DatabaseHelper moneydb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addincome,container,false);

        createRadioButtons(view);
        moneydb = new DatabaseHelper(getActivity());

        Spinner mySpinner = (Spinner) view.findViewById(R.id.categoryOptions);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Category_m));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        Spinner mySpinner2 = (Spinner) view.findViewById(R.id.accountOptions);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Account_options));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);

        date=view.findViewById(R.id.date);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Calendar calendar = Calendar.getInstance();
                final int year=calendar.get(Calendar.YEAR);
                final int month=calendar.get(Calendar.MONTH);
                final int day=calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2){
                        date.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                },year,month,day);
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

    private void createRadioButtons(View view) {
        RadioGroup g = (RadioGroup) view.findViewById(R.id.Recurrence);

        String[] cat = getResources().getStringArray((R.array.Recurrence));
        for (int i=0;i<cat.length;i++)
        {
            String cate =cat[i];
            RadioButton b = new RadioButton(getActivity());
            b.setText(cate);

            g.addView(b);
        }
    }

    public void AddData(){
        addData.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean insertData= moneydb.insertIncomeData(Integer.parseInt(editAmount.getText().toString()),
                                date.getText().toString(),
                                category.getSelectedItem().toString(),
                                account.getSelectedItem().toString(),
                                null,
                                notes.getText().toString());
                        if (insertData==true)
                            Toast.makeText(getActivity(),"Data inserted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(),"Data not inserted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

}
