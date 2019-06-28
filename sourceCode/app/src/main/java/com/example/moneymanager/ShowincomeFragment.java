package com.example.moneymanager;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ShowincomeFragment extends Fragment {
    View view;
    Button getDetails;
    Button getSelectedDetails;
    DatabaseHelper moneydb;

    Spinner monthSelected;
    Spinner yearSelected;

    ArrayAdapter adapter;
    ListView myListView;

    String[] monthss;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_showincome,container,false);
        moneydb = new DatabaseHelper(getActivity());
        getDetails = (Button)view.findViewById(R.id.getDetails);
        getSelectedDetails = (Button) view.findViewById(R.id.getAllDetails);

        monthss = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] months = new String[12];
        for(int i=0; i < 12; i++){
            months[i] = Integer.toString(i+1);
        }
        Spinner mySpinner = (Spinner) view.findViewById(R.id.monthList);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                monthss);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[25];
        for(int i=0; i < 25; i++){
            years[i] = Integer.toString(currentYear);
            currentYear--;
        }
        Spinner mySpinner2 = (Spinner) view.findViewById(R.id.yearList);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                years);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);

        monthSelected = (Spinner) view.findViewById(R.id.monthList);
        yearSelected = (Spinner) view.findViewById(R.id.yearList);


        myListView = (ListView) view.findViewById(R.id.myListView);
        Context context = getActivity();
        TextView header = new TextView(context);
        header.setText("List of Incomes (Click an item to edit)");
        myListView.addHeaderView(header);

        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = 3;
                Object listItemSelected = myListView.getItemAtPosition(position);
                String idSelected = Character.toString(listItemSelected.toString().charAt(0));
                EditincomeFragment fragment2 = new EditincomeFragment();
                Bundle args2 = new Bundle();
                args2.putString("Key2", idSelected);
                fragment2.setArguments(args2);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container
                        ,fragment2).commit();
            }
        });

        viewAll();
        viewSelectedData();
        return view;
    }

    public void viewAll(){
        getSelectedDetails.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> listItemAmount = new ArrayList<>();
                        ArrayList<String> listItemDate = new ArrayList<>();
                        ArrayList<String> listItemCategory = new ArrayList<>();
                        ArrayList<String> listItemID= new ArrayList<>();
                        int month = 0;
                        for(int i=0; i<12; i++){
                            if(monthss[i].equals(monthSelected.getSelectedItem().toString())){
                                month = i+1;
                            }
                        }
                        //int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                        int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                        Cursor res = moneydb.getAllIncomeData();
                        if(res.getCount() == 0){
                            Toast.makeText(getActivity(),"No incomes found",Toast.LENGTH_LONG).show();
                        }


                        while(res.moveToNext()){
                            String item = new String();
                            item = res.getString(0) + ". " + res.getString(1) + "$ by " + res.getString(3) + " on "
                                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
                            listItemAmount.add(res.getString(1));
                            listItemDate.add(res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2));
                            listItemCategory.add(res.getString(3));
                            listItemID.add(res.getString(0));
                        }
                        CustomAdaptor customAdaptor = new CustomAdaptor(listItemAmount, listItemDate, listItemCategory,listItemID);
                        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

                        myListView.setAdapter(customAdaptor);
                    }
                }
        );
    }

    public void viewSelectedData() {


        getDetails.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> listItemAmount = new ArrayList<>();
                        ArrayList<String> listItemDate = new ArrayList<>();
                        ArrayList<String> listItemCategory = new ArrayList<>();
                        ArrayList<String> listItemID= new ArrayList<>();
                        int month = 0;
                        for(int i=0; i<12; i++){
                            if(monthss[i].equals(monthSelected.getSelectedItem().toString())){
                                month = i+1;
                            }
                        }
                        //int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                        int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                        Cursor res = moneydb.getSelectedAllIncomeData(year,month);
                        if (res.getCount() == 0) {
                            Toast.makeText(getActivity(),"No incomes found",Toast.LENGTH_LONG).show();

                        }


                        while (res.moveToNext()) {
                            String item = new String();
                            item = res.getString(0) + ". " + res.getString(1) + "$ for " + res.getString(3) + " on "
                                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
                            listItemAmount.add(res.getString(1));
                            listItemDate.add(res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2));
                            listItemCategory.add(res.getString(3));
                            listItemID.add(res.getString(0));

                        }
                        CustomAdaptor customAdaptor = new CustomAdaptor(listItemAmount, listItemDate, listItemCategory,listItemID);
                        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

                        myListView.setAdapter(customAdaptor);

                    }
                }
        );
    }

    class CustomAdaptor extends BaseAdapter {
        ArrayList<String> listItemAmount = new ArrayList<>();
        ArrayList<String> listItemDate = new ArrayList<>();
        ArrayList<String> listItemCategory = new ArrayList<>();
        ArrayList<String> listItemID= new ArrayList<>();

        CustomAdaptor(ArrayList<String> listItemAmountt, ArrayList<String> listItemDatee, ArrayList<String> listItemCategoryy, ArrayList<String> listItemIDD ){
            for(String s : listItemAmountt){
                listItemAmount.add(s);
            }
            for(String s : listItemDatee){
                listItemDate.add(s);
            }
            for(String s : listItemCategoryy){
                listItemCategory.add(s);
            }
            for(String s : listItemIDD){
                listItemID.add(s);
            }
        }

        @Override
        public int getCount() {
            return listItemAmount.size();
        }

        @Override
        public Object getItem(int position) {
            return listItemID.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.listview_layout, null);



            //String imageViewName = "R.drawable." + listItemCategory.get(position).toLowerCase();

            ImageView mImageView = (ImageView) view.findViewById(R.id.imageView);
            TextView mTextView = (TextView) view.findViewById(R.id.textView);
            TextView mTextView2 = (TextView) view.findViewById(R.id.textView2);
            TextView mTextView3 = (TextView) view.findViewById(R.id.textView3);

            switch (listItemCategory.get(position).toLowerCase()){
                case "food":
                    mImageView.setImageResource(R.drawable.food);
                    break;
                case "travel":
                    mImageView.setImageResource(R.drawable.travel);
                    break;
                case "education":
                    mImageView.setImageResource(R.drawable.education);
                    break;
                case "utilities":
                    mImageView.setImageResource(R.drawable.utilities);
                    break;
                case "shopping":
                    mImageView.setImageResource(R.drawable.shopping);
                    break;
                case "household":
                    mImageView.setImageResource(R.drawable.household);
                    break;
                case "medical":
                    mImageView.setImageResource(R.drawable.medical);
                    break;
                case "salary":
                    mImageView.setImageResource(R.drawable.salary);
                    break;
                case "allowance":
                    mImageView.setImageResource(R.drawable.allowance);
                    break;
                default:
                    mImageView.setImageResource(R.drawable.others);
                    break;
            }

            //mImageView.setImageResource(R.drawable.log);
            mTextView.setText(listItemAmount.get(position) + "$");
            mTextView2.setText(listItemDate.get(position));
            mTextView3.setText(listItemCategory.get(position));

            return view;
        }
    }
}


