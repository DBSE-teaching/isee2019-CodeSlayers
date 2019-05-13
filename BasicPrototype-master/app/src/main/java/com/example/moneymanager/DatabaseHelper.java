package com.example.moneymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.EditText;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String fromdate="";
    public static final String todate="";

    public static final String DATABASE_NAME = "Money.db";
    public static final String TABLE_NAME = "money_table";
    public static final String INCOME_TABLE_NAME = "income_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Amount";
    public static final String COL_3 = "Date";
    public static final String COL_4 = "Category";
    public static final String COL_5 = "Account";
    public static final String COL_6 = "Recurrence";
    public static final String COL_7 = "Notes";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table money_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, AMOUNT INT, DATE TEXT, CATEGORY TEXT, ACCOUNT TEXT, RECURRENCE BOOLEAN, NOTES TEXT)");
        db.execSQL("create table income_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, AMOUNT INT, DATE TEXT, CATEGORY TEXT, ACCOUNT TEXT, RECURRENCE BOOLEAN, NOTES TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS money_table");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS income_table");
        onCreate(db);
    }

    public boolean insertData(int amount, String date, String category, String account, Boolean recurrence, String notes){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, amount);
        contentValues.put(COL_3, date);
        contentValues.put(COL_4, category);
        contentValues.put(COL_5, account);
        contentValues.put(COL_6, recurrence);
        contentValues.put(COL_7, notes);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from money_table",null);
        return res;
    }

    public Cursor getDataById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res2 = db.rawQuery("select * from money_table where ID =" + id, null);
        return res2;
    }

    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id} );
    }

    public boolean insertIncomeData(int amount, String date, String category, String account, Boolean recurrence, String notes){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, amount);
        contentValues.put(COL_3, date);
        contentValues.put(COL_4, category);
        contentValues.put(COL_5, account);
        contentValues.put(COL_6, recurrence);
        contentValues.put(COL_7, notes);
        long result = db.insert(INCOME_TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllIncomeData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from income_table", null);
        return res;
    }

    public Cursor getIncomeDataById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res2 = db.rawQuery("select * from income_table where ID =" + id, null);
        return res2;
    }

    public Integer deleteIncomeData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INCOME_TABLE_NAME, "ID = ?",new String[] {id} );
    }

    public Cursor getIncomeSum(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res3 = db.rawQuery("select sum(amount) as total from income_table", null);
        return res3;
    }

    public Cursor getExpenseSum(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res4 = db.rawQuery("select sum(amount) as total from money_table", null);
        return res4;
    }
}
