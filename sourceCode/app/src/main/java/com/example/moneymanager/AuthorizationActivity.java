package com.example.moneymanager;

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
        boxPIN = (EditText) findViewById(R.id.boxPIN);
        submitPin();

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
                        if(Integer.parseInt(boxPIN.getText().toString()) == PIN){
                            Intent launch = new Intent(AuthorizationActivity.this, MainActivity.class);
                            startActivity(launch);
                        }else {
                            Toast.makeText(AuthorizationActivity.this, "Enter correct PIN", Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
    }


}
