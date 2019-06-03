package com.example.moneymanager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;

public class ShowexpenseFragment extends Fragment {
    View view;

    DatabaseHelper moneydb;

    Button getDetails;
    Button getSelectedDetails;
    Spinner monthSelected;
    Spinner yearSelected;
    ArrayAdapter adapter;
    ListView myListView;
    Spinner filterOptionsSelected;
    Spinner categorySelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_showexpense, container, false);

        moneydb = new DatabaseHelper(getActivity());

        getDetails = (Button) view.findViewById(R.id.getDetails);

        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Integer.toString(i + 1);
        }
        Spinner mySpinner = (Spinner) view.findViewById(R.id.monthList);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                months);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[25];
        for (int i = 0; i < 24; i++) {
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

        String[] filterOptions = {"Show all", "Date Only", "Category Only", "Date and Category"};
        Spinner mySpinner4 = (Spinner) view.findViewById(R.id.filterOptions);
        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                filterOptions);
        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner4.setAdapter(myAdapter4);

        monthSelected = (Spinner) view.findViewById(R.id.monthList);
        yearSelected = (Spinner) view.findViewById(R.id.yearList);
        filterOptionsSelected = (Spinner) view.findViewById(R.id.filterOptions);
        categorySelected = (Spinner) view.findViewById(R.id.categoryFilterOptions);

        myListView = (ListView) view.findViewById(R.id.myListView);
        Context context = getActivity();
        TextView header = new TextView(context);
        header.setText("List of Expenses (Click an item to edit)");
        myListView.addHeaderView(header);

        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItemSelected = myListView.getItemAtPosition(position);
                String idSelected = Character.toString(listItemSelected.toString().charAt(0));
                EditexpenseFragment fragment = new EditexpenseFragment();
                Bundle args = new Bundle();
                args.putString("Key", idSelected);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , fragment).commit();
            }
        });

        //viewAll();
        viewData();
        return view;

    }

    public void viewAll() {


        getSelectedDetails.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> listItem = new ArrayList<>();
                        int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                        int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                        Cursor res = moneydb.getAllData();
                        if (res.getCount() == 0) {
                            Toast.makeText(getActivity(), "No expenses found", Toast.LENGTH_LONG).show();

                        }


                        while (res.moveToNext()) {
                            String item = new String();
                            item = res.getString(0) + ". " + res.getString(1) + "$ for " + res.getString(3) + " on "
                                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
                            listItem.add(item);

                        }
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

                        myListView.setAdapter(adapter);
                    }
                }
        );
    }

    public void viewData() {

        getDetails.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Cursor res = checkFilterOption();

                        ArrayList<String> listItem = new ArrayList<>();

                        if (res.getCount() == 0) {
                            Toast.makeText(getActivity(), "No expenses found", Toast.LENGTH_LONG).show();

                        }

                        while (res.moveToNext()) {
                            String item = new String();
                            item = res.getString(0) + ". " + res.getString(1) + "$ for " + res.getString(3) + " on "
                                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
                            listItem.add(item);

                        }
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

                        myListView.setAdapter(adapter);
                    }
                }
        );
    }

    public Cursor checkFilterOption() {
        String selected = filterOptionsSelected.getSelectedItem().toString();

        switch (selected) {
            case "Show all": {
                Cursor res = moneydb.getAllData();
                return res;

            }
            case "Date Only": {
                int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                Cursor res = moneydb.getSelectedAllData(year, month);
                return res;

            }
            case "Category Only": {
                String categoryS = categorySelected.getSelectedItem().toString();
                Cursor res = moneydb.getSelectedCategoryData(categoryS);
                return res;

            }
            case "Date and Category": {
                int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                String categoryS = categorySelected.getSelectedItem().toString();
                Cursor res = moneydb.getSelectedCategoryAndDateData(year, month, categoryS);
                return res;

            }
        }
        Cursor res = moneydb.getAllData();
        return res;
    }
}
