package com.example.moneymanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VoiceActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecog;


    FloatingActionButton voiceButton;

    DatabaseHelper moneydb;


    ArrayAdapter adapter;
    ListView myListView;

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    boolean breakOutConditionExpense = false;
    boolean breakOutConditionshow = false;
    boolean notNumber = false;

    TextView speakTextView;

    Button voiceButtonNormal;

    PieChart chart;

    int expenseValues[] = new int[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        moneydb = new DatabaseHelper(VoiceActivity.this);
        myListView = (ListView) findViewById(R.id.myListView);
        speakTextView = (TextView) findViewById(R.id.speakTag);
        chart = (PieChart) findViewById(R.id.chart);
        chart.setVisibility(View.GONE);

        voiceButtonNormal = (Button) findViewById(R.id.getDetails);
        voiceButtonNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(VoiceActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(VoiceActivity.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(VoiceActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    speechRecog.startListening(intent);
                }
            }
        });

        voiceButton = (FloatingActionButton) findViewById(R.id.voiceButtonn);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(VoiceActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(VoiceActivity.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(VoiceActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    speechRecog.startListening(intent);
                }
            }
        });

        initializeTextToSpeech();
        initializeSpeechRecognizer();


    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0) {
                    Toast.makeText(VoiceActivity.this, "Not supported in this device", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    tts.setLanguage(Locale.US);
                    speak("Hello there, What can I do for you?");
                }
            }
        });

    }

    private void speak(String message) {
        if (Build.VERSION.SDK_INT >= 21) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecog = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecog.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> result_arr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    speakTextView.setText(result_arr.get(0));
                    processResult(result_arr.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void processResult(String result_message) {
        breakOutConditionExpense = false;
        result_message = result_message.toLowerCase();

//        Handle at least four sample cases

        //        First: What is your Name?
//        Second: What is the time?
//        Third: Is the earth flat or a sphere?
//        Fourth: Open a browser and open url
        if (result_message.indexOf("what") != -1) {
            if (result_message.indexOf("name") != -1) {
                chart.setVisibility(View.GONE);
                myListView.setVisibility(View.GONE);
                speak("My Name is Budget Buddy. Nice to meet you!");
            }
            if (result_message.contains("show") || result_message.contains("do")) {
                chart.setVisibility(View.GONE);
                myListView.setVisibility(View.GONE);
                speak("Please check the keywords");
            }
        } else if (result_message.contains("hello") || (result_message.contains("good"))) {
            speak("Hello ! I can help you if you stick to only key words here after");
            chart.setVisibility(View.GONE);
            myListView.setVisibility(View.GONE);
        } else if (result_message.contains("short") || result_message.contains("chore") || result_message.contains("shortcut") || result_message.contains("no") || result_message.contains("show") || result_message.contains("so") || result_message.contains("sho") || result_message.contains("give") || result_message.contains("transaction") || result_message.contains("history") || result_message.contains("transactions") || result_message.contains("expenses") || result_message.contains("expense") || result_message.contains("income") || result_message.contains("incomes")) {
            if (result_message.contains("sharp") || result_message.contains("visual") || result_message.contains("chart") || result_message.contains("chat")) {
                Cursor resCategory = moneydb.getAllExpenseCategory();
                Log.d("myTag", "No data found");
                if (resCategory.getCount() == 0) {
                    Log.d("myTag", "No data found");
                }
                ArrayList<String> CategoryExpenseList = new ArrayList<String>();
                while (resCategory.moveToNext()) {
                    CategoryExpenseList.add(resCategory.getString(1));
                }
                Cursor res = moneydb.getAllData();
                if (res.getCount() == 0) {
                    Toast.makeText(this, "No expense data", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("myTag", "chart creation");
                    while (res.moveToNext()) {
                        for (int k = 0; k < CategoryExpenseList.size(); k++) {
                            if (CategoryExpenseList.get(k).equals(res.getString(3))) {
                                expenseValues[k] += res.getInt(1);
                            }
                        }
                    }
                    List<PieEntry> pieEntries = new ArrayList<>();
                    final int[] COLORFUL_COLORS = {
                            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53), Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
                            Color.rgb(191, 134, 134), Color.rgb(179, 48, 80), Color.rgb(217, 80, 138), Color.rgb(254, 149, 7)
                    };
                    for (int k = 0; k < CategoryExpenseList.size(); k++) {
                        if (expenseValues[k] != 0)
                            pieEntries.add(new PieEntry(expenseValues[k], CategoryExpenseList.get(k)));
                    }
                    PieDataSet dataset = new PieDataSet(pieEntries, "");
                    dataset.setColors(COLORFUL_COLORS);
                    dataset.setValueTextSize(15);
                    PieData data = new PieData(dataset);


                    chart.setData(data);
                    chart.getLegend().setWordWrapEnabled(true);
                    chart.getDescription().setEnabled(false);
                    chart.setDrawEntryLabels(false);
                    chart.invalidate();
                    chart.setVisibility(View.VISIBLE);
                    myListView.setVisibility(View.GONE);
                    Log.d("myTag", "chart speak");
                    speak("Visual stats of this month expense");
                }
                Log.d("myTag", "chart breakout");
                breakOutConditionshow = true;
            }
            if (result_message.contains("threshold") || result_message.contains("limit")) {
                ArrayList<String> listItem = new ArrayList<>();
                Cursor res = moneydb.getThreshValue();
                if (res.getCount() == 0) {
                    Toast.makeText(this, "no threshold", Toast.LENGTH_LONG).show();
                }

                while (res.moveToNext()) {
                    String item = new String();
                    item = res.getString(0) + ". " + res.getString(1) + ": " + res.getString(2);
                    listItem.add(item);
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItem);
                myListView.setAdapter(adapter);
                chart.setVisibility(View.GONE);
                myListView.setVisibility(View.VISIBLE);
                breakOutConditionshow = true;
            }
            if (result_message.contains("expense") || result_message.contains("expenditure") || result_message.contains("spent")) {
                if (result_message.contains("monthly") || result_message.contains("this month")) {
                    speak("Showing this month transactions");
                    chart.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    Cursor res = moneydb.getSelectedAllData(currentYear, currentMonth);
                    {
                        if (res.getCount() == 0) {
                            Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                        }
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
                        breakOutConditionExpense = true;
                    }
                }
                if (result_message.contains("food") || result_message.contains("dinner") || result_message.contains("supermarket")) {
                    speak("Showing all food transactions");
                    chart.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    Cursor res = moneydb.getSelectedCategoryData("Food");
                    {
                        if (res.getCount() == 0) {
                            Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                        }
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
                        breakOutConditionExpense = true;
                    }
                }
                if (result_message.contains("shopping")) {
                    speak("Showing all shopping transactions");
                    chart.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    Cursor res = moneydb.getSelectedCategoryData("Shopping");
                    {
                        if (res.getCount() == 0) {
                            Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                        }
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
                        breakOutConditionExpense = true;
                    }
                }
                if (result_message.contains("entertainment")) {
                    speak("Showing all entertainment transactions");
                    chart.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    Cursor res = moneydb.getSelectedCategoryData("Entertainment");
                    {
                        if (res.getCount() == 0) {
                            Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                        }
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
                        breakOutConditionExpense = true;
                    }
                }
                if (result_message.contains("travel")) {
                    speak("Showing all travel transactions");
                    chart.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    Cursor res = moneydb.getSelectedCategoryData("Travel");
                    {
                        if (res.getCount() == 0) {
                            Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                        }
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
                        breakOutConditionExpense = true;
                    }
                }
                if (result_message.contains("education")) {
                    speak("Showing all education transactions");
                    chart.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    Cursor res = moneydb.getSelectedCategoryData("Education");
                    {
                        if (res.getCount() == 0) {
                            Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                        }
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
                        breakOutConditionExpense = true;
                    }
                }
                if (!breakOutConditionExpense) {
                    speak("Showing all expense transactions");
                    chart.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    Cursor res = moneydb.getAllData();
                    {
                        if (res.getCount() == 0) {
                            Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                        }
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
                        breakOutConditionExpense = false;
                    }

                }
                breakOutConditionshow = true;
            }
            if (result_message.contains("income") || result_message.contains("earned") || result_message.contains("incomes")) {
                speak("Showing all income transactions");
                chart.setVisibility(View.GONE);
                myListView.setVisibility(View.VISIBLE);
                Cursor res = moneydb.getAllIncomeData();
                {
                    if (res.getCount() == 0) {
                        Toast.makeText(VoiceActivity.this, "No incomes found", Toast.LENGTH_LONG).show();
                    }
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
                breakOutConditionshow = true;
            }
            if (!breakOutConditionshow) {
                speak("Showing all expense transactions");
                chart.setVisibility(View.GONE);
                myListView.setVisibility(View.VISIBLE);
                Cursor res = moneydb.getAllData();
                {
                    if (res.getCount() == 0) {
                        Toast.makeText(VoiceActivity.this, "No expenses found", Toast.LENGTH_LONG).show();
                    }
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
                    breakOutConditionshow = false;
                }
            }
        } else if (result_message.contains("add")) {
            chart.setVisibility(View.GONE);
            myListView.setVisibility(View.GONE);
            notNumber = false;

            String[] splits = result_message.split("add|category|mode");
            splits[1] = splits[1].replaceAll("\\s", "");
            Log.d("Number", splits[1]);

            String category = " ";
            boolean categorySet = false;

            if (splits.length > 2) {
                Cursor resCategory = moneydb.getAllExpenseCategory();
                if (resCategory.getCount() == 0) {
                    Log.d("myTag", "No data found");
                }
                ArrayList<String> CategoryExpenseList = new ArrayList<String>();
                while (resCategory.moveToNext()) {
                    CategoryExpenseList.add(resCategory.getString(1));
                }
                for (String stringCategory : CategoryExpenseList) {
                    if ((splits[2]).contains(stringCategory.toLowerCase())) {
                        category = stringCategory;
                        categorySet = true;

                    }
                }
            }
            if (category.equals(" ")) {
                category = "Others";
            }
            boolean numberBoolean = isNumber(splits[1]);
            if (numberBoolean) {
                int number = Integer.parseInt(splits[1]);
                boolean insertData = moneydb.insertData(number,
                        currentDay,
                        currentMonth,
                        currentYear,
                        category,
                        "Debit Card",
                        null,
                        " ");
                if (insertData == true) {
                    if (categorySet) {
                        speak("Data inserted with category provided, today's date and default mode of payment");
                    } else {
                        speak("Data inserted with default category, today's date and default mode of payment");
                    }
                } else {
                    speak("Data not inserted");
                }
            } else {
                speak("Please say the amount properly");
            }
        } else if (result_message.contains("at")) {
            chart.setVisibility(View.GONE);
            myListView.setVisibility(View.GONE);
            notNumber = false;

            String[] splits = result_message.split("at|category|mode");
            splits[1] = splits[1].replaceAll("\\s", "");
            Log.d("Number", splits[1]);

            String category = " ";
            boolean categorySet = false;

            if (splits.length > 2) {
                Cursor resCategory = moneydb.getAllExpenseCategory();
                if (resCategory.getCount() == 0) {
                    Log.d("myTag", "No data found");
                }
                ArrayList<String> CategoryExpenseList = new ArrayList<String>();
                while (resCategory.moveToNext()) {
                    CategoryExpenseList.add(resCategory.getString(1));
                }
                for (String stringCategory : CategoryExpenseList) {
                    Log.d("fromdb", stringCategory.toLowerCase());
                    Log.d("fromdb", "category" + splits[2]);
                    Log.d("fromdb", "boolean" + (splits[2]).contains(stringCategory.toLowerCase()));

                    if ((splits[2]).contains(stringCategory.toLowerCase())) {
                        category = stringCategory;
                        categorySet = true;

                    }
                }
            }
            if (category.equals(" ")) {
                category = "Others";
            }
            boolean numberBoolean = isNumber(splits[1]);
            if (numberBoolean) {
                int number = Integer.parseInt(splits[1]);
                boolean insertData = moneydb.insertData(number,
                        currentDay,
                        currentMonth,
                        currentYear,
                        category,
                        "Debit Card",
                        null,
                        " ");
                if (insertData == true) {
                    if (categorySet) {
                        speak("Data inserted with category provided, today's date and default mode of payment");
                    } else {
                        speak("Data inserted with default category, today's date and default mode of payment");
                    }
                } else {
                    speak("Data not inserted");
                }
            } else {
                speak("Please say the amount properly");
            }
        } else if (result_message.contains("ad")) {
            chart.setVisibility(View.GONE);
            myListView.setVisibility(View.GONE);
            notNumber = false;

            String[] splits = result_message.split("ad|category|mode");
            splits[1] = splits[1].replaceAll("\\s", "");
            Log.d("Number", splits[1]);

            String category = " ";
            boolean categorySet = false;

            if (splits.length > 2) {
                Cursor resCategory = moneydb.getAllExpenseCategory();
                if (resCategory.getCount() == 0) {
                    Log.d("myTag", "No data found");
                }
                ArrayList<String> CategoryExpenseList = new ArrayList<String>();
                while (resCategory.moveToNext()) {
                    CategoryExpenseList.add(resCategory.getString(1));
                }
                for (String stringCategory : CategoryExpenseList) {
                    Log.d("fromdb", stringCategory.toLowerCase());
                    Log.d("fromdb", "category" + splits[2]);
                    Log.d("fromdb", "boolean" + (splits[2]).contains(stringCategory.toLowerCase()));

                    if ((splits[2]).contains(stringCategory.toLowerCase())) {
                        category = stringCategory;
                        categorySet = true;

                    }
                }
            }
            if (category.equals(" ")) {
                category = "Others";
            }
            boolean numberBoolean = isNumber(splits[1]);
            if (numberBoolean) {
                int number = Integer.parseInt(splits[1]);
                boolean insertData = moneydb.insertData(number,
                        currentDay,
                        currentMonth,
                        currentYear,
                        category,
                        "Debit Card",
                        null,
                        " ");
                if (insertData == true) {
                    if (categorySet) {
                        speak("Data inserted with category provided, today's date and default mode of payment");
                    } else {
                        speak("Data inserted with default category, today's date and default mode of payment");
                    }
                } else {
                    speak("Data not inserted");
                }
            } else {
                speak("Please say the amount properly");
            }
        } else {
            speak("Sorry, Please stick to the keywords");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        initializeSpeechRecognizer();
        initializeTextToSpeech();
    }

    protected boolean isNumber(String number) {
        boolean result = false;
        try {
            int numberInt = Integer.parseInt(number);
            result = true;
            return result;
        } catch (NumberFormatException e) {
            result = false;
            return result;
        }
    }

    private void setupPieChart(ArrayList<String> CategoryExpenseList) {
        List<PieEntry> pieEntries = new ArrayList<>();
        final int[] COLORFUL_COLORS = {
                Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53), Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
                Color.rgb(191, 134, 134), Color.rgb(179, 48, 80), Color.rgb(217, 80, 138), Color.rgb(254, 149, 7)
        };
        for (int k = 0; k < CategoryExpenseList.size(); k++) {
            if (expenseValues[k] != 0)
                pieEntries.add(new PieEntry(expenseValues[k], CategoryExpenseList.get(k)));
        }
        PieDataSet dataset = new PieDataSet(pieEntries, "");
        dataset.setColors(COLORFUL_COLORS);
        dataset.setValueTextSize(15);
        PieData data = new PieData(dataset);


        chart.setData(data);
        chart.getLegend().setWordWrapEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.invalidate();
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
