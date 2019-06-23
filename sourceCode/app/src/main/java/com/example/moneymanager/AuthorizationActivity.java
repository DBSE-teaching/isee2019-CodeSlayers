package com.example.moneymanager;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthorizationActivity extends AppCompatActivity {

    Button submitButton;
    Button forgotButton;
    DatabaseHelper moneydb;
    EditText boxPIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        moneydb = new DatabaseHelper(this);

        //Setting initial PIN as 0000 and Authorization switch off
        Cursor res1 = moneydb.getPIN();
        if (res1.getCount() == 0) {
            boolean insertPIN = moneydb.insertInitialPinAndSwitch(0000, 0);
            if (insertPIN == false) {
                Log.d("myTag", "Initial Pin Not inserted");
            }
        }

        //Getting authorization setting value
        int switchValue = 0;
        Cursor res2 = moneydb.getSwitch();
        if (res2.getCount() == 0) {
                Log.d("myTag", "No data");
        }else {
            res2.moveToNext();
            switchValue = res2.getInt(0);
        }

        //Checking whether authorization is switched on
        if(switchValue == 0){
            Intent launch = new Intent(AuthorizationActivity.this, MainActivity.class);
            startActivity(launch);
        }

        submitButton = (Button) findViewById(R.id.submitPIN);
        forgotButton = (Button) findViewById(R.id.forgotPin);
        boxPIN = (EditText) findViewById(R.id.boxPIN);
        submitPin();
        forgotPin();
    }

    public void submitPin(){
        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Cursor res5 = moneydb.getPIN();
                        if (res5.getCount() == 0) {
                            Log.d("myTag", "No PIN data found");
                        }
                        res5.moveToNext();
                        int PIN = res5.getInt(0);
                        if(boxPIN.getText().toString().equals("")){
                            Toast.makeText(AuthorizationActivity.this, "Enter correct PIN", Toast.LENGTH_LONG).show();
                        }else {
                            if (Integer.parseInt(boxPIN.getText().toString()) == PIN) {
                                Intent launch = new Intent(AuthorizationActivity.this, MainActivity.class);
                                startActivity(launch);
                            } else {
                                Toast.makeText(AuthorizationActivity.this, "Enter correct PIN", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }
        );
    }

    public void forgotPin(){
        forgotButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Cursor res6 = moneydb.getEmailAndPIN();
                        if (res6.getCount() == 0) {
                            Log.d("myTag", "No PIN data found");
                        }
                        res6.moveToNext();
                        int PIN = res6.getInt(0);
                        String Email = res6.getString(1);
                        if(Email == null){
                            Toast.makeText(AuthorizationActivity.this, "Email not set", Toast.LENGTH_LONG).show();
                        }else {
                            sendMessage(PIN, Email);

                            Toast.makeText(AuthorizationActivity.this, "PIN sent to given mail", Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
    }

    private void sendMessage(int PIN, String EmailFromDB) {
        final String Email = EmailFromDB;
        final String subject = "BudgetBuddy - Request to send PIN";
        final String message = "Please find your PIN - " + PIN;
        final ProgressDialog dialog = new ProgressDialog(AuthorizationActivity.this);
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
                            Email);
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }


}
