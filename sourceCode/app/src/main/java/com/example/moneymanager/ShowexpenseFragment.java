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

import java.util.ArrayList;
import java.util.Calendar;

public class ShowexpenseFragment extends Fragment {
    View view;
    Button getDetails;
    DatabaseHelper moneydb;

    private EditText fromdate;
    private EditText todate;

    DatePickerDialog datePickerDialog;

    ArrayList<String> listItem;
    ArrayAdapter adapter;
    ListView myListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_showexpense, container, false);
        moneydb = new DatabaseHelper(getActivity());
        getDetails = (Button) view.findViewById(R.id.getDetails);

        fromdate = view.findViewById(R.id.fromdatatext);
        fromdate.setInputType(InputType.TYPE_NULL);
        todate = view.findViewById(R.id.todatatext);
        todate.setInputType(InputType.TYPE_NULL);

        fromdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int fyear = calendar.get(Calendar.YEAR);
                final int fmonth = calendar.get(Calendar.MONTH);
                final int fday = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        fromdate.setText(i2 + "/" + (i1 + 1) + "/" + i);
                    }
                }, fyear, fmonth, fday);
                datePickerDialog.show();
            }
        });


        todate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_YEAR);
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        todate.setText(i2 + "/" + (i1 + 1) + "/" + i);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        listItem = new ArrayList<>();
        myListView = (ListView) view.findViewById(R.id.myListView);

        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = 3;
                Object listItemSelected = myListView.getItemAtPosition(position);
                String idSelected = Character.toString(listItemSelected.toString().charAt(4));
                EditexpenseFragment fragment = new EditexpenseFragment();
                Bundle args = new Bundle();
                args.putString("Key", idSelected);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , fragment).commit();
            }
        });

        viewAll();
return view;

    }

                    public void viewAll() {


                        getDetails.setOnClickListener(
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Cursor res = moneydb.getAllData();
                                        if (res.getCount() == 0) {
                                            showMessages("Error", "Nothing found");
                                            return;
                                        }


                                        while (res.moveToNext()) {
                                            StringBuffer buffer = new StringBuffer();
                                            buffer.append("ID: " + res.getString(0) + "\n");
                                            buffer.append("Amount: " + res.getString(1) + "\n");
                                            buffer.append("Date: " + res.getString(2) + "\n");
                                            buffer.append("Category: " + res.getString(3) + "\n");
                                            buffer.append("Account: " + res.getString(4) + "\n");
                                            buffer.append("Recurrence: " + res.getString(5) + "\n");
                                            buffer.append("Notes: " + res.getString(6) + "\n\n");

                                            listItem.add(buffer.toString());
                                        }

                                        //showMessages("Data",buffer.toString());
                                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);
                                        myListView.setAdapter(adapter);
                                    }
                                }
                        );
                    }

                    public void showMessages(String title, String message) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(true);
                        builder.setTitle(title);
                        builder.setMessage(message);
                        builder.show();
                    }
                }
