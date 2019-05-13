package com.example.moneymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SummaryFragment extends Fragment {
    TextView totalincome;
    TextView totalexpense;
    TextView totalbalance;
    DatabaseHelper moneydb;
    int a,b,c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        moneydb = new DatabaseHelper(getActivity());


        Cursor res = moneydb.getIncomeSum();
        res.moveToFirst();
        totalincome = (TextView) view.findViewById(R.id.totalIncome);

        if(res.getString(0) == null){
            a=0;
            totalincome.setText("0");
        } else{
           a=Integer.parseInt(res.getString(0));
           totalincome.setText(res.getString(0));
        }



        Cursor res2 = moneydb.getExpenseSum();
        res2.moveToFirst();
        totalexpense = (TextView) view.findViewById(R.id.totalExpense);

        if(res2.getString(0) == null){
            totalexpense.setText("0");
            b=0;
        }else {
            totalexpense.setText(res2.getString(0));
            b = Integer.parseInt(res2.getString(0));
        }

        c = a-b;
        totalbalance = (TextView) view.findViewById(R.id.totalBalance);
        totalbalance.setText(" " + c + " ");

        return view;


    }
}

