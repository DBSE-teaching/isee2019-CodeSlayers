package com.example.moneymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.EditText;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "Money.db";
    public static final String TABLE_NAME = "money_table";
    public static final String INCOME_TABLE_NAME = "income_table";
    public static final String CATEGORY_EXPENSE_TABLE_NAME = "category_expense_table";
    public static final String CATEGORY_INCOME_TABLE_NAME = "category_income_table";
    public static final String AUTHORIZATION_TABLE_NAME = "authorization_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Amount";
    public static final String COL_3 = "Year";
    public static final String COL_4 = "Category";
    public static final String COL_5 = "Account";
    public static final String COL_6 = "Recurrence";
    public static final String COL_7 = "Notes";
    public static final String COL_8 = "Month";
    public static final String COL_9 = "Day";
    public static final String COL_CATEGORY_1 = "ID";
    public static final String COL_CATEGORY_2 = "Category";
    public static final String COL_AUTH_1 = "ID";
    public static final String COL_AUTH_2 = "Pin";
    public static final String COL_AUTH_3 = "Switch";
    public static final String COL_AUTH_4 = "Email";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table money_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, AMOUNT INT, YEAR INT, CATEGORY TEXT, ACCOUNT TEXT, RECURRENCE BOOLEAN, NOTES TEXT, MONTH INT, DAY INT)");
        db.execSQL("create table income_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, AMOUNT INT, YEAR INT, CATEGORY TEXT, ACCOUNT TEXT, RECURRENCE BOOLEAN, NOTES TEXT, MONTH INT, DAY INT)");
        db.execSQL("create table category_expense_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY TEXT)");
        db.execSQL("create table category_income_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY TEXT)");
        db.execSQL("create table authorization_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, Pin INTEGER, Switch INTEGER, Email TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS money_table");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS income_table");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS category_expense_table");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS category_income_table");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS authorization_table");
        onCreate(db);
    }

    public boolean insertData(int amount, int day, int month, int year, String category, String account, Boolean recurrence, String notes){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, amount);
        contentValues.put(COL_3, year);
        contentValues.put(COL_4, category);
        contentValues.put(COL_5, account);
        contentValues.put(COL_6, recurrence);
        contentValues.put(COL_7, notes);
        contentValues.put(COL_8, month);
        contentValues.put(COL_9, day);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean updateExpenseData(String ID, int amount, int day, int month, int year, String category, String account, Boolean recurrence, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, amount);
        contentValues.put(COL_3, year);
        contentValues.put(COL_4, category);
        contentValues.put(COL_5, account);
        contentValues.put(COL_6, recurrence);
        contentValues.put(COL_7, notes);
        contentValues.put(COL_8, month);
        contentValues.put(COL_9, day);
        String where = "id=?";
        String[] whereArgs = new String[] {ID};
        long result = db.update(TABLE_NAME, contentValues, where, whereArgs);
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

    public Cursor getExpenseSum(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res4 = db.rawQuery("select sum(amount) as total from money_table", null);
        return res4;
    }

    public Cursor getSelectedExpenseSum(int yearInput, int monthInput){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res4 = db.rawQuery("select sum(amount) as total from money_table where year =" + yearInput + " and month = " + monthInput, null);
        return res4;
    }

    public Cursor getSelectedExpenseSumCategory(int yearInput, int monthInput, String categoryInput){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res4 = db.rawQuery("select sum(amount) as total from money_table where year =" + yearInput + " and month = " + monthInput + " and category = '" + categoryInput + "'", null);
        return res4;
    }

    public Cursor getSelectedAllData(int yearInput, int monthInput){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from money_table where year = " + yearInput + " and month = " + monthInput,null);
        return res;
    }

    public Cursor getSelectedCategoryData(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from money_table where category = '" + category + "'",null);
        return res;
    }

    public Cursor getSelectedCategoryAndDateData(int yearInput, int monthInput, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from money_table where year = " + yearInput + " and month = " + monthInput + " and category = '" + category +"'",null);
        return res;
    }

    public boolean insertIncomeData(int amount, int day, int month, int year, String category, String account, Boolean recurrence, String notes){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, amount);
        contentValues.put(COL_3, year);
        contentValues.put(COL_4, category);
        contentValues.put(COL_5, account);
        contentValues.put(COL_6, recurrence);
        contentValues.put(COL_7, notes);
        contentValues.put(COL_8, month);
        contentValues.put(COL_9, day);
        long result = db.insert(INCOME_TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean updateIncomeData(String ID, int amount, int day, int month, int year, String category, String account, Boolean recurrence, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, amount);
        contentValues.put(COL_3, year);
        contentValues.put(COL_4, category);
        contentValues.put(COL_5, account);
        contentValues.put(COL_6, recurrence);
        contentValues.put(COL_7, notes);
        contentValues.put(COL_8, month);
        contentValues.put(COL_9, day);
        String where = "id=?";
        String[] whereArgs = new String[] {ID};
        long result = db.update(INCOME_TABLE_NAME, contentValues, where, whereArgs);
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

    public Cursor getSelectedAllIncomeData(int yearInput, int monthInput){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from income_table where year = " + yearInput + " and month = " + monthInput,null);
        return res;
    }

    public Cursor getSelectedSumAllIncomeData(int yearInput, int monthInput){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res4 = db.rawQuery("select sum(amount) as total from income_table where year =" + yearInput + " and month = " + monthInput, null);
        return res4;
    }

    public boolean insertExpenseCategory (String category){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CATEGORY_2, category);
        long result = db.insert(CATEGORY_EXPENSE_TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllExpenseCategory (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from category_expense_table", null);
        return res;
    }

    public boolean insertIncomeCategory (String category){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CATEGORY_2, category);
        long result = db.insert(CATEGORY_INCOME_TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllIncomeCategory (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from category_income_table", null);
        return res;
    }

    public boolean insertInitialPinAndSwitch (int PIN, int Switch ){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_AUTH_2, PIN);
        contentValues.put(COL_AUTH_3, Switch);
        long result = db.insert(AUTHORIZATION_TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getPIN (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select PIN from authorization_table", null);
        return res;
    }

    public Cursor getSwitch (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Switch from authorization_table", null);
        return res;
    }

    public boolean setSwitchAndPassword (int PIN, int Switch){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_AUTH_2, PIN);
        contentValues.put(COL_AUTH_3, Switch);
        String where = "id=?";
        String[] whereArgs = new String[] {String.valueOf(1)};
        long result = db.update(AUTHORIZATION_TABLE_NAME, contentValues, where, whereArgs);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean setEmail(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_AUTH_4, email);
        String where = "id=?";
        String[] whereArgs = new String[] {String.valueOf(1)};
        long result = db.update(AUTHORIZATION_TABLE_NAME, contentValues, where, whereArgs);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor showEmail (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Email from authorization_table", null);
        return res;
    }

    public Cursor getEmailAndPIN (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select PIN, Email from authorization_table", null);
        return res;
    }


}
