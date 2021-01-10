package com.example.mynewapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Activity2 extends AppCompatActivity {
    public static final String mobArrList = "com.example.mytestapp.mobArrList";
    ListView listView;
    Boolean adapterList = true;
    ArrayList<String> jsonList = new ArrayList<>();
    ArrayList<DataModel> jsonDataModel = new ArrayList<>();

    private CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        listView = findViewById(R.id.listView);

        jsonList = getIntent().getExtras().getStringArrayList("MOBARR");
        for (String s : jsonList) {
            jsonDataModel.add(new DataModel(s, false));
        }
        adapter = new CustomAdapter(jsonDataModel, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("CLICKED");
                DataModel dataModel = jsonDataModel.get(position);
                dataModel.checked = !dataModel.checked;
                System.out.println(jsonDataModel);
                adapter.notifyDataSetChanged();
            }
        });

    }

//    class MyAdapter extends ArrayAdapter<String> {
//        Context context;
//        ArrayList<String> list;
//
//        MyAdapter (Context c, ArrayList<String> l){
//            super(c, R.layout.row, R.id.singleItem, l);
//            this.context = c;
//            this.list = l;
//        }
//
////        @NonNull
////        @Override
////        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
////
//////            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//////            View row = layoutInflater.inflate(R.layout.row, parent, false);
//////            TextView arrItemList = row.findViewById(R.id.singleItem);
//////            //setting resources to view
//////
//////            arrItemList.setText(list.get(position));
//////            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox2);
//////            checkBox.setText(list.get(position));
//////            checkBox.setOnCheckedChangeListener();
////
////
////            return row;
////
////        }
//    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Activity2.this,"Session Ended",Toast.LENGTH_LONG).show();
        finish();
        return;
    }

}