package com.example.moneymanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReportFragment extends Fragment {
    View view;
    Button sendReport;
    DatabaseHelper moneydb;
    File gpxfile;
    Spinner monthSelected;
    Spinner yearSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);

        MainActivity.isSummaryShown = false;

        getActivity().setTitle("Reports");

        moneydb = new DatabaseHelper(getActivity());

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

        monthSelected = (Spinner) view.findViewById(R.id.monthList);
        yearSelected = (Spinner) view.findViewById(R.id.yearList);

        sendReport = (Button) view.findViewById(R.id.sendReport);

        sendReportToMail();


        return view;
    }

    public void sendReportToMail(){
        sendReport.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        File file = new File(getActivity().getFilesDir(),"mydir");
                        if(!file.exists()){
                            file.mkdir();
                        }

                        try{
                            gpxfile = new File(file, "Report.txt");
                            FileWriter writer = new FileWriter(gpxfile);

                            //Expense details
                            writer.append("Monthly Account Details " + monthSelected.getSelectedItem().toString() + "/" + yearSelected.getSelectedItem().toString());
                            writer.append("\n\n\nExpenses of the month:");
                            writer.append("\n\nAmount       Date        Category");
                            int month = Integer.parseInt(monthSelected.getSelectedItem().toString());
                            int year = Integer.parseInt(yearSelected.getSelectedItem().toString());
                            Cursor res = moneydb.getSelectedAllData(year, month);


                            if (res.getCount() == 0) {
                                Toast.makeText(getActivity(), "No expenses found", Toast.LENGTH_LONG).show();
                            }

                            while (res.moveToNext()) {
                                String item = new String();
                                item = res.getString(1) + "         "
                                        + res.getString(8) + "/" + res.getString(7) + "/" + res.getString(2)
                                        + "         " + res.getString(3);
                                writer.append("\n" + item);
                            }

                            /* Getting the sum of current monthly income*/
                            Cursor sumExpense = moneydb.getSelectedExpenseSum(year, month);
                            sumExpense.moveToFirst();

                            if(sumExpense.getString(0) == null){
                                writer.append("\n\n" + "Total Expense of the month = 0");
                            } else{
                                writer.append("\n\n" + "Total Expense of the month = " + sumExpense.getString(0));
                            }

                            //Income details
                            writer.append("\n\n\nIncomes of the month:");
                            writer.append("\n\nAmount       Date        Category");
                            Cursor res2 = moneydb.getSelectedAllIncomeData(year, month);


                            if (res2.getCount() == 0) {
                                Toast.makeText(getActivity(), "No Income found", Toast.LENGTH_LONG).show();
                            }

                            while (res2.moveToNext()) {
                                String item = new String();
                                item = res2.getString(1) + "        "
                                        + res2.getString(8) + "/" + res2.getString(7) + "/" + res2.getString(2)
                                        + "         " + res2.getString(3);
                                writer.append("\n" + item);
                            }

                            /* Getting the sum of current monthly income*/
                            Cursor sumIncome = moneydb.getSelectedSumAllIncomeData(year, month);
                            sumIncome.moveToFirst();

                            if(sumIncome.getString(0) == null){
                                writer.append("\n\n" + "Total Income of the month = 0");
                            } else{
                                writer.append("\n\n" + "Total Income of the month = " + sumIncome.getString(0));
                            }

                            writer.flush();
                            writer.close();

                        }catch (Exception e){

                        }

                        Cursor res6 = moneydb.getEmailAndPIN();
                        if (res6.getCount() == 0) {
                            Log.d("myTag", "No Email data found");
                        }
                        res6.moveToNext();
                        String Email = res6.getString(1);
                        if(Email == null){
                            Toast.makeText(getActivity(), "Email not set", Toast.LENGTH_LONG).show();
                        }else {
                            sendMessage(Email, gpxfile);

                            Toast.makeText(getActivity(), "Report sent", Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
    }

    private void sendMessage(String EmailFromDB, File file) {
        final String Email = EmailFromDB;
        final String subject = "BudgetBuddy - Requested Report ";
        final String message = "Please find the requested report attached";
        final File fileCreated = file;
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailSender sender = new MailSender("budgetbuddyisee@gmail.com", "isee2019");
                    sender.sendMail(subject,
                            message,
                            "budgetbuddyisee@gmail.com",
                            Email,
                            fileCreated);
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

    public void wrtieFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        File file = new File(mcoContext.getFilesDir(),"mydir");
        if(!file.exists()){
            file.mkdir();
        }

        try{
            gpxfile = new File(file, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

        }catch (Exception e){

        }
    }

}

