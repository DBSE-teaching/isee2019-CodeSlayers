package com.example.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SummaryFragment extends Fragment {
    View view;

    DatabaseHelper moneyDatabase;
    ListView myExpenseListView;
    ListView myIncomeListView;

    int a,b,c;
    ArrayAdapter adapter;
    ArrayAdapter adapter2;

    int expenseValues[] = new int[10];
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary, container, false);

        moneyDatabase = new DatabaseHelper(getActivity());

        /* Getting the sum of current monthly income*/
        Cursor sumIncome = moneyDatabase.getIncomeSum();
        sumIncome.moveToFirst();
        TextView totalIncome = (TextView) view.findViewById(R.id.totalIncome);

        if(sumIncome.getString(0) == null){
            a=0;
            totalIncome.setText("Total Income:   " + "0");
        } else{
           a=Integer.parseInt(sumIncome.getString(0));
            totalIncome.setText("Total Income:   " + sumIncome.getString(0));
        }


        /* Getting the sum of current monthly expense*/
        Cursor sumExpense = moneyDatabase.getExpenseSum();
        sumExpense.moveToFirst();
        TextView totalExpense = (TextView) view.findViewById(R.id.totalExpense);

        if(sumExpense.getString(0) == null){
            totalExpense.setText("Total Expense:   " + "0");
            b=0;
        }else {
            totalExpense.setText("Total Expense:   " + sumExpense.getString(0));
            b = Integer.parseInt(sumExpense.getString(0));
        }

        c = a-b;
        TextView totalBalance = (TextView) view.findViewById(R.id.totalBalance);
        totalBalance.setText("Balance:   " + c + " ");

        Context context = getActivity();

        myExpenseListView = (ListView) view.findViewById(R.id.myListViewExpense);

        myIncomeListView = (ListView) view.findViewById(R.id.myListViewIncome);

        viewCurrentMonthExpenseData();
        viewCurrentMonthIncomeData();

        String categoryValues[] = getResources().getStringArray(R.array.Category_n);
        moneyDatabase = new DatabaseHelper(getActivity());
        Cursor res3 = moneyDatabase.getSelectedAllData(currentYear,currentMonth);
        if (res3.getCount() == 0) {
            Toast.makeText(getActivity(),"No expense data",Toast.LENGTH_LONG).show();
        }else{
            while (res3.moveToNext()) {
                for (int k = 0; k < categoryValues.length; k++) {
                    if(categoryValues[k].equals(res3.getString(3))){
                        expenseValues[k] += res3.getInt(1);
                    }
                }
            }
            setupThisMonthPieChart(categoryValues);
        }

        return view;


    }

    public void viewCurrentMonthExpenseData() {
        ArrayList<String> listExpenseItem = new ArrayList<>();
        Cursor res = moneyDatabase.getSelectedAllData(currentYear,currentMonth);
        if (res.getCount() == 0) {
            listExpenseItem.add("No expenses found");
        }
        while (res.moveToNext()) {
            String item = new String();
            item = res.getString(1) + "$ for " + res.getString(3) + " on "
                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
            listExpenseItem.add(item);
        }
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listExpenseItem);
        myExpenseListView.setAdapter(adapter);
    }

    public void viewCurrentMonthIncomeData() {
        ArrayList<String> listIncomeItem = new ArrayList<>();
        Cursor res = moneyDatabase.getSelectedAllIncomeData(currentYear,currentMonth);
        if (res.getCount() == 0) {
            listIncomeItem.add("No income found");
        }
        while (res.moveToNext()) {
            String item = new String();
            item = res.getString(1) + "$ by " + res.getString(3) + " on "
                    + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2);
            listIncomeItem.add(item);
        }
        adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listIncomeItem);
        myIncomeListView.setAdapter(adapter2);
    }

    private void setupThisMonthPieChart(String[] categoryValues){
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int j=0; j< categoryValues.length; j++){
            pieEntries.add(new PieEntry(expenseValues[j],categoryValues[j]));
        }

        PieDataSet dataset = new PieDataSet(pieEntries, "Expense chart");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataset);

        PieChart chart = (PieChart) view.findViewById(R.id.chartSummary);
        chart.setData(data);
        chart.invalidate();
    }
}

