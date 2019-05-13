package com.example.moneymanager;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowincomeFragment extends Fragment {
    View view;
    Button getDetails;
    DatabaseHelper moneydb;

    ArrayList<String> listItem;
    ArrayAdapter adapter;
    ListView myListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_showincome,container,false);
        moneydb = new DatabaseHelper(getActivity());
        getDetails = (Button)view.findViewById(R.id.getDetails);

        listItem = new ArrayList<>();
        myListView = (ListView) view.findViewById(R.id.myListView);

        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = 3;
                Object listItemSelected = myListView.getItemAtPosition(position);
                String idSelected = Character.toString(listItemSelected.toString().charAt(4));
                EditincomeFragment fragment2 = new EditincomeFragment();
                Bundle args2 = new Bundle();
                args2.putString("Key2", idSelected);
                fragment2.setArguments(args2);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,fragment2).commit();
            }
        });

        viewAll();
        return view;
    }

    public void viewAll(){
        getDetails.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = moneydb.getAllIncomeData();
                        if(res.getCount() == 0){
                            showMessages("Error", "Nothing found");
                            return;
                        }


                        while(res.moveToNext()){
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("ID: "+res.getString(0)+"\n");
                            buffer.append("Amount: "+res.getString(1)+"\n");
                            buffer.append("Date: "+res.getString(2)+"\n");
                            buffer.append("Category: "+res.getString(3)+"\n");
                            buffer.append("Account: "+res.getString(4)+"\n");
                            buffer.append("Recurrence: "+res.getString(5)+"\n");
                            buffer.append("Notes: "+res.getString(6)+"\n\n");
                            listItem.add(buffer.toString());
                        }

                        //showMessages("Data",buffer.toString());
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);
                        myListView.setAdapter(adapter);
                    }
                }
        );
    }

    public void showMessages (String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}


