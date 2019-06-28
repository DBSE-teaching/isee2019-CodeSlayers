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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class ShowexpenseFragment extends Fragment {
    View view;

    DatabaseHelper moneydb;

    Button getDetails;
    Spinner monthSelected;
    Spinner yearSelected;
    ArrayAdapter adapter;
    ListView myListView;
    Spinner filterOptionsSelected;
    Spinner categorySelected;
    Spinner noOfMonthsSelected;

    TextView selectDateTag;
    TextView noSelectDateTag;
    TextView categoryFilterTag;


    String[] monthss;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_showexpense, container, false);

        moneydb = new DatabaseHelper(getActivity());

        getDetails = (Button) view.findViewById(R.id.getDetails);


        monthss = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Integer.toString(i + 1);
        }
        Spinner mySpinner = (Spinner) view.findViewById(R.id.monthList);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                monthss);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[25];
        for (int i = 0; i < 25; i++) {
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

        String[] filterOptions = {"No Filter", "Date Based", "Category Based", "Date and Category"};
        Spinner mySpinner4 = (Spinner) view.findViewById(R.id.filterOptions);
        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                filterOptions);
        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner4.setAdapter(myAdapter4);

        String[] noMonthsOptions = {"One Month Data", "3 Months Data", "6 Months Data", "One Year Data"};
        Spinner mySpinner5 = (Spinner) view.findViewById(R.id.noMonthList);
        ArrayAdapter<String> myAdapter5 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                noMonthsOptions);
        myAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner5.setAdapter(myAdapter5);

        monthSelected = (Spinner) view.findViewById(R.id.monthList);
        yearSelected = (Spinner) view.findViewById(R.id.yearList);
        filterOptionsSelected = (Spinner) view.findViewById(R.id.filterOptions);
        categorySelected = (Spinner) view.findViewById(R.id.categoryFilterOptions);
        noOfMonthsSelected = (Spinner) view.findViewById(R.id.noMonthList);

        selectDateTag = (TextView) view.findViewById(R.id.selectDateTag);
        noSelectDateTag = (TextView) view.findViewById(R.id.noSelectDateTag);
        categoryFilterTag = (TextView) view.findViewById(R.id.categoryFilterTag);


        myListView = (ListView) view.findViewById(R.id.myListView);
        Context context = getActivity();
        TextView header = new TextView(context);
        header.setText("List of Expenses (Click an item to edit)");
        myListView.addHeaderView(header);

        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("myTag", " " + listItemSelected);
                String idSelected = (String) myListView.getItemAtPosition(position);
                EditexpenseFragment fragment = new EditexpenseFragment();
                Bundle args = new Bundle();
                args.putString("Key", idSelected);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container
                        , fragment).commit();
            }
        });

        filterView();
        viewData();
        return view;

    }

    public void viewData() {

        getDetails.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkFilterOption();
                    }
                }
        );
    }

    public void checkFilterOption() {
        String selected = filterOptionsSelected.getSelectedItem().toString();

        switch (selected) {
            case "No Filter": {
                Cursor res = moneydb.getAllData();
                {
                    if (res.getCount() == 0) {
                        Toast.makeText(getActivity(), "No expenses found", Toast.LENGTH_LONG).show();
                    }
                    //ArrayList<String> listItem = new ArrayList<>();
                    ArrayList<String> listItemAmount = new ArrayList<>();
                    ArrayList<String> listItemDate = new ArrayList<>();
                    ArrayList<String> listItemCategory = new ArrayList<>();
                    ArrayList<String> listItemID= new ArrayList<>();
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
                    myListView.setAdapter(customAdaptor);
                }
                break;

            }
            case "Date Based": {
                int noSelected = checkNoOfMonthsSelected();
                int month = 0;
                for(int i=0; i<12; i++){
                    if(monthss[i].equals(monthSelected.getSelectedItem().toString())){
                        month = i+1;
                    }
                }
                //int month = Integer.parseInt(monthSelected.getSelectedItem().toString());


                ArrayList<String> listItemAmount = new ArrayList<>();
                ArrayList<String> listItemDate = new ArrayList<>();
                ArrayList<String> listItemCategory = new ArrayList<>();
                ArrayList<String> listItemID= new ArrayList<>();
                for(int i = month; i < month+noSelected; i++){
                    int monthForDataBase=i;
                    int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                    if(monthForDataBase>12){
                        monthForDataBase=monthForDataBase-12;
                        year=year+1;
                    }
                    Cursor res = moneydb.getSelectedAllData(year, monthForDataBase);
                    while (res.moveToNext()) {
                        String item = new String();
                        item = res.getString(0) + ". " + res.getString(1) + "$ for " + res.getString(3) + " on "
                                + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
                        listItemAmount.add(res.getString(1));
                        listItemDate.add(res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2));
                        listItemCategory.add(res.getString(3));
                        listItemID.add(res.getString(0));
                    }
                }
                CustomAdaptor customAdaptor = new CustomAdaptor(listItemAmount, listItemDate, listItemCategory,listItemID);
                //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

                myListView.setAdapter(customAdaptor);
                if(listItemAmount.size() == 0){
                    Toast.makeText(getActivity(), "No expenses found", Toast.LENGTH_LONG).show();
                }
                break;

            }
            case "Category Based": {
                String categoryS = categorySelected.getSelectedItem().toString();
                Cursor res = moneydb.getSelectedCategoryData(categoryS);
                {
                    if (res.getCount() == 0) {
                        Toast.makeText(getActivity(), "No expenses found", Toast.LENGTH_LONG).show();
                    }
                    //ArrayList<String> listItem = new ArrayList<>();
                    ArrayList<String> listItemAmount = new ArrayList<>();
                    ArrayList<String> listItemDate = new ArrayList<>();
                    ArrayList<String> listItemCategory = new ArrayList<>();
                    ArrayList<String> listItemID= new ArrayList<>();
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
                break;

            }
            case "Date and Category": {
                int noSelected = checkNoOfMonthsSelected();
                int month = 0;
                for(int i=0; i<12; i++){
                    if(monthss[i].equals(monthSelected.getSelectedItem().toString())){
                        month = i+1;
                    }
                }
                //int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                String categoryS = categorySelected.getSelectedItem().toString();
                ArrayList<String> listItemAmount = new ArrayList<>();
                ArrayList<String> listItemDate = new ArrayList<>();
                ArrayList<String> listItemCategory = new ArrayList<>();
                ArrayList<String> listItemID= new ArrayList<>();
                for(int i = month; i < month+noSelected; i++){
                    int monthForDataBase=i;
                    int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                    if(monthForDataBase>12){
                        monthForDataBase=monthForDataBase-12;
                        year=year+1;
                    }
                    Cursor res = moneydb.getSelectedCategoryAndDateData(year, monthForDataBase, categoryS);
                    while (res.moveToNext()) {
                        String item = new String();
                        item = res.getString(0) + ". " + res.getString(1) + "$ for " + res.getString(3) + " on "
                                + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
                        listItemAmount.add(res.getString(1));
                        listItemDate.add(res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2));
                        listItemCategory.add(res.getString(3));
                        listItemID.add(res.getString(0));

                    }
                }
                CustomAdaptor customAdaptor = new CustomAdaptor(listItemAmount, listItemDate, listItemCategory,listItemID);
                //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItem);

                myListView.setAdapter(customAdaptor);
                if(listItemAmount.size() == 0){
                    Toast.makeText(getActivity(), "No expenses found", Toast.LENGTH_LONG).show();
                }
                break;

            }
        }
    }

    public void filterView() {
        filterOptionsSelected.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = filterOptionsSelected.getSelectedItem().toString();

                switch (position) {

                    case 0:
                        categorySelected.setVisibility(View.GONE);
                        monthSelected.setVisibility(View.GONE);
                        yearSelected.setVisibility(View.GONE);
                        noOfMonthsSelected.setVisibility(View.GONE);
                        selectDateTag.setVisibility(View.GONE);
                        noSelectDateTag.setVisibility(View.GONE);
                        categoryFilterTag.setVisibility(View.GONE);
                        break;
                    case 1:
                        categorySelected.setVisibility(View.GONE);
                        monthSelected.setVisibility(View.VISIBLE);
                        yearSelected.setVisibility(View.VISIBLE);
                        noOfMonthsSelected.setVisibility(View.VISIBLE);
                        selectDateTag.setVisibility(View.VISIBLE);
                        noSelectDateTag.setVisibility(View.VISIBLE);
                        categoryFilterTag.setVisibility(View.GONE);
                        break;
                    case 2:
                        categorySelected.setVisibility(View.VISIBLE);
                        monthSelected.setVisibility(View.GONE);
                        yearSelected.setVisibility(View.GONE);
                        noOfMonthsSelected.setVisibility(View.GONE);
                        selectDateTag.setVisibility(View.GONE);
                        noSelectDateTag.setVisibility(View.GONE);
                        categoryFilterTag.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        categorySelected.setVisibility(View.VISIBLE);
                        monthSelected.setVisibility(View.VISIBLE);
                        yearSelected.setVisibility(View.VISIBLE);
                        noOfMonthsSelected.setVisibility(View.VISIBLE);
                        selectDateTag.setVisibility(View.VISIBLE);
                        noSelectDateTag.setVisibility(View.VISIBLE);
                        categoryFilterTag.setVisibility(View.VISIBLE);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public int checkNoOfMonthsSelected(){
        String selected = noOfMonthsSelected.getSelectedItem().toString();
        int noSelected;

        switch(selected){
            case "One Month Data":
                noSelected = 1;
                return noSelected;
            case "3 Months Data":
                noSelected = 3;
                return noSelected;
            case "6 Months Data":
                noSelected = 6;
                return noSelected;
            case "One Year Data":
                noSelected = 12;
                return noSelected;
        }
        return 1;
    }

    class CustomAdaptor extends BaseAdapter{
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



            String imageViewName = "R.drawable." + listItemCategory.get(position).toLowerCase();

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
