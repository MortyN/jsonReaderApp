package com.example.mynewapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Activity2 extends AppCompatActivity implements View.OnClickListener{

    public static final String mobArrList = "com.example.mytestapp.mobArrList";
    public static final String ADDITEM_TEXT = "com.example.mytestapp.ADDITEM_TEXT";
    public static final String STATE = "com.example.mytestapp.STATE";
    public static final String START_SERVER = "startserver";
    public static final String ITEMADD_FEEDBACK = "com.example.mytestapp.ITEMADD_FEEDBACK";

    ListView listView;
    CheckBox checkBox;

    ArrayList<String> jsonList = new ArrayList<>();
    ArrayList<DataModel> jsonDataModel = new ArrayList<>();
    public String test = "hei";
    private CustomAdapter adapter;
    public EditText itemInputField;
    int feedbackNum;
    public ArrayList<String> jsonBroadList2;

    public SharedPreferences.OnSharedPreferenceChangeListener listner;




    boolean gotList = false;
    Set<String> newJsonArrayList = null;


    ArrayList<String> removeList = new ArrayList<>();

    String state;

    public static class AppReceiver extends BroadcastReceiver {
        public ArrayList<String> jsonBroadList;
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, intent.getStringExtra("101"), Toast.LENGTH_LONG).show();
            jsonBroadList = intent.getExtras().getStringArrayList("MOBARR");

//            Set set = new HashSet(jsonBroadList);
//            SharedPreferences prefs = context.getSharedPreferences("SHARED_PREFS_FILE", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putStringSet("SETLIST", set);
//            editor.apply();
        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        ArrayList<DataModel> newDataModelList = new ArrayList<>();




        listner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if(key.equals("SETLIST")){
                    System.out.println("hei");
                    newJsonArrayList = prefs.getStringSet("SETLIST", new HashSet<String>());

                    newDataModelList.clear();

                    for (String s : newJsonArrayList) {
                        newDataModelList.add(new DataModel(s, false));
                    }

                    adapter.updateList(newDataModelList);

                }
            }
        };












//        SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//
//
//            }
//        };






        listView = findViewById(R.id.listView);
        checkBox = findViewById(R.id.checkBox);


        final Button button = findViewById(R.id.addItemButton);
        itemInputField = (EditText) findViewById(R.id.addItemText);
        button.setOnClickListener(this);

        final Button removeItemButton = findViewById(R.id.removeChecked);
        removeItemButton.setOnClickListener(this);

        feedbackNum = getIntent().getIntExtra(ITEMADD_FEEDBACK, 0);

        jsonList = getIntent().getExtras().getStringArrayList("MOBARR");
        for (String s : jsonList) {
            jsonDataModel.add(new DataModel(s, false));
        }

        adapter = new CustomAdapter(jsonDataModel, getApplicationContext());
        listView.setAdapter(adapter);
        listView.getCheckedItemPosition();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel = jsonDataModel.get(position);
                dataModel.checked = !dataModel.checked;
                System.out.println(dataModel.name + dataModel.checked);

                if(dataModel.checked){
                    if(!removeList.contains(dataModel.name)){
                        removeList.add(dataModel.name);
                    }
                }
                else if(!dataModel.checked){
                    removeList.remove(dataModel.name);
                }
                adapter.notifyDataSetChanged();

            }
        });
    }


    @Override
    public void onClick(View v) {
        SharedPreferences prefs = getSharedPreferences("SHARED_PREFS_FILE", Context.MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(listner);

        switch (v.getId()) {

            case R.id.addItemButton:

                state = "2";
                String addItem = itemInputField.getText().toString();
                Intent sendAddItem = new Intent(this, SocketHandler.class);
                sendAddItem.putExtra(ADDITEM_TEXT, addItem);
                sendAddItem.putExtra(STATE, state);
                sendAddItem.setAction(START_SERVER);
                startService(sendAddItem);






                break;

            case R.id.removeChecked:
                state = "3";
                Intent removeItem = new Intent(this, SocketHandler.class);
                removeItem.putStringArrayListExtra("REMOVELIST", removeList);
                removeItem.putExtra(STATE, state);
                removeItem.setAction(START_SERVER);
                startService(removeItem);



        }
    }



    @Override
    public void onBackPressed() {
        Toast.makeText(Activity2.this, "Session Ended", Toast.LENGTH_LONG).show();

        return;
    }

}