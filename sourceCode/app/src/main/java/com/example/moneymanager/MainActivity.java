package com.example.moneymanager;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FloatingActionButton addButton;
    DatabaseHelper moneydb;
    public static boolean isSummaryShown = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moneydb = new DatabaseHelper(this);

        //Setting up the default Expense category list
        Cursor res = moneydb.getAllExpenseCategory();
        if (res.getCount() == 0) {
            String[] categoryDefaultList = {"Food", "Shopping", "Entertainment", "Travel", "Others"};
            int i = 0;
            while(i < categoryDefaultList.length) {
                boolean insertCategory = moneydb.insertThreshold(categoryDefaultList[i], 0);
                if (insertCategory == true)
                    i++;
                else
                    Log.d("myTag", "Not inserted");
            }
        }

        //Setting up the default Income category list
        Cursor res2 = moneydb.getAllIncomeCategory();
        if (res2.getCount() == 0) {
            String[] categoryDefaultList = {"Salary", "Allowance", "Bonus", "Others"};
            int i = 0;
            while (i < categoryDefaultList.length - 1) {
                boolean insertCategory = moneydb.insertIncomeCategory(categoryDefaultList[i]);
                if (insertCategory == true)
                    i++;
                else
                    Log.d("myTag", "Not inserted");
            }
        }

        //Setting up the side navigation bar layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();



            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //Floating action button to add a new transaction from anywhere
            addButton = (FloatingActionButton) findViewById(R.id.addButton);
            addButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new IncomeFragment()).commit();

                }
            });

            //Setting up the default fragment
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new SummaryFragment()).commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.history:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new ExpenseFragment()).commit();
                break;
            case R.id.addNew:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new IncomeFragment()).commit();
                break;
            case R.id.summary:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new SummaryFragment()).commit();
                break;
            case R.id.visual:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new VisualFragment()).commit();
                break;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new SettingFragment()).commit();
                break;
            case R.id.report:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new ReportFragment()).commit();
                break;
            case R.id.threshold:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,new ThresholdFragment()).commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if(isSummaryShown){
            finishAffinity();

        }
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
}
