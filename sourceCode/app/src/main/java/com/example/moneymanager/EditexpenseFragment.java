package com.example.moneymanager;

import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner.TrimOption;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class EditexpenseFragment extends Fragment {
    EditText idRow;
    EditText Amount;
    EditText date;
    Spinner category;
    Spinner account;
    EditText notes;
    DatabaseHelper moneydb;
    Button deletedata;
    Button updatedata;
    int year;
    int month;
    int day;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editexpense,container,false);

        moneydb = new DatabaseHelper(getActivity());

        String value = getArguments().getString("Key");
        idRow = (EditText) view.findViewById(R.id.idRow);
        idRow.setText(value);

        Cursor res = moneydb.getDataById(Integer.parseInt(value));
        res.moveToFirst();

        Amount = (EditText) view.findViewById(R.id.editText2);
        Amount.setText(res.getString(1));

        date = (EditText) view.findViewById(R.id.date);
        String dateFromDB = res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
        day = res.getInt(8);
        month = res.getInt(7);
        year = res.getInt(2);
        date.setText(dateFromDB);

        category = (Spinner) view.findViewById(R.id.categoryOptions);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Category_n));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(myAdapter);
        category.setSelection(myAdapter.getPosition(res.getString(3)));

        account = (Spinner) view.findViewById(R.id.accountOptions);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Account_options));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account.setAdapter(myAdapter2);
        account.setSelection(myAdapter2.getPosition(res.getString(4)));

        notes = (EditText) view.findViewById(R.id.notes);
        notes.setText(res.getString(6));

        deletedata = (Button) view.findViewById(R.id.deleteExpense);
        deleteAll();
        updatedata = (Button) view.findViewById(R.id.editExpense);
        updateAll();

        return view;
    }

    public void deleteAll(){
        deletedata.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Integer deleteRow = moneydb.deleteData(idRow.getText().toString());
                        if (deleteRow > 0)
                            Toast.makeText(getActivity(),"Data deleted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(),"Data not deleted", Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    public void updateAll(){
        updatedata.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String numberString = "^[1-9]\\d*(\\.\\d+)?$";

                        if (Amount.getText().toString().matches(numberString)){
                            boolean insertData = moneydb.updateExpenseData(idRow.getText().toString(),
                                    Integer.parseInt(Amount.getText().toString()),
                                    day,
                                    month,
                                    year,
                                    category.getSelectedItem().toString(),
                                    account.getSelectedItem().toString(),
                                    null,
                                    notes.getText().toString());
                            if (insertData == true) {
                                Toast.makeText(getActivity(), "Data updated", Toast.LENGTH_LONG).show();

                            }else
                                Toast.makeText(getActivity(), "Data not updated", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getActivity(),"Enter Amount",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}
