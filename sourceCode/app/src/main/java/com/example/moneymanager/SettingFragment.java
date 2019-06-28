package com.example.moneymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;

public class SettingFragment extends Fragment {
    View view;

    DatabaseHelper moneydb;

    Button addNewExpenseCategory;
    EditText newUserExpenseCategory;

    Button addNewIncomeCategory;
    EditText newUserIncomeCategory;

    Button setAuthorization;
    Spinner setAuthorizationSwitch;
    EditText inputPIN;

    Button setEmail ;
    Button showEmail;
    EditText emailBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        MainActivity.isSummaryShown = false;

        getActivity().setTitle("Settings");

        moneydb = new DatabaseHelper(getActivity());

        addNewExpenseCategory = (Button) view.findViewById(R.id.addNewExpenseCategory);
        newUserExpenseCategory = (EditText) view.findViewById(R.id.categoryName);

        addNewIncomeCategory = (Button) view.findViewById(R.id.addNewIncomeCategory);
        newUserIncomeCategory = (EditText) view.findViewById(R.id.categoryIncomeName);

        AddNewExpenseCategoryData();
        AddNewIncomeCategoryData();

        String[] options = {"On", "Off"};
        Spinner mySpinner = (Spinner) view.findViewById(R.id.authorizationOptions);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                options);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        setAuthorization = (Button) view.findViewById(R.id.setAuthorizationSwitch);
        inputPIN = (EditText) view.findViewById(R.id.settingPinBox);
        setAuthorizationSwitch = (Spinner) view.findViewById(R.id.authorizationOptions);

        verifyAuthorizationSetting();

        setEmail = (Button) view.findViewById(R.id.setEmail);
        showEmail = (Button) view.findViewById(R.id.showEmail);
        emailBox = (EditText) view.findViewById(R.id.emailBox);

        setEmailid();
        showEmailid();

        return view;
    }

    public void AddNewExpenseCategoryData(){
        addNewExpenseCategory.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!newUserExpenseCategory.getText().toString().equals("")){
                            boolean insertData = moneydb.insertThreshold(newUserExpenseCategory.getText().toString(),0);
                            if (insertData == true) {
                                Toast.makeText(getActivity(), "Category inserted", Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                        , new SettingFragment()).commit();
                            }else
                                Toast.makeText(getActivity(), "Category not inserted", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getActivity(),"Enter Category",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void AddNewIncomeCategoryData(){
        addNewIncomeCategory.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!newUserIncomeCategory.getText().toString().equals("")){
                            boolean insertData = moneydb.insertIncomeCategory(newUserIncomeCategory.getText().toString());
                            if (insertData == true) {
                                Toast.makeText(getActivity(), "Category inserted", Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                        , new SettingFragment()).commit();
                            }else
                                Toast.makeText(getActivity(), "Category not inserted", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getActivity(),"Enter Category",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void verifyAuthorizationSetting(){
        setAuthorization.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(setAuthorizationSwitch.getSelectedItem().toString().equals("On")){
                            String numberString = "^[1-9]\\d*(\\.\\d+)?$";
                            if(inputPIN.getText().toString().matches(numberString) && inputPIN.getText().toString().length() > 3){
                                boolean updateData = moneydb.setSwitchAndPassword(Integer.parseInt(inputPIN.getText().toString()), 1);
                                if (updateData == true) {
                                    Toast.makeText(getActivity(), "Authorization On - PIN set", Toast.LENGTH_LONG).show();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                            , new SettingFragment()).commit();
                                }else
                                    Toast.makeText(getActivity(), "PIN not set", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getActivity(),"Enter PIN in correct format",Toast.LENGTH_LONG).show();
                            }
                        }else {
                            boolean updateData = moneydb.setSwitchAndPassword(0000, 0);
                            if (updateData == true)
                                Toast.makeText(getActivity(), "Authorization Off", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getActivity(), "PIN not set", Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
    }

    public void setEmailid(){
        setEmail.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emailString =  "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        if (emailBox.getText().toString().matches(emailString)){
                            boolean insertData = moneydb.setEmail(emailBox.getText().toString());
                            emailBox.setText("");
                            Toast.makeText(getActivity(), "Email set", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getActivity(), "Enter valid Email", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void showEmailid(){
        showEmail.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor resCategory = moneydb.showEmail();
                        if (resCategory.getCount() == 0) {
                            Log.d("myTag", "No data found");
                        }else{
                            resCategory.moveToNext();
                            if(resCategory.getString(0) == null){
                                Toast.makeText(getActivity(), "Email not set", Toast.LENGTH_LONG).show();
                            }else{
                                emailBox.setText(resCategory.getString(0));
                            }

                        }

                    }
                }
        );


    }
}

