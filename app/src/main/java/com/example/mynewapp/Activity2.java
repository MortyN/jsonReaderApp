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
    CheckBox checkBox;
    ArrayList<String> jsonList = new ArrayList<>();
    ArrayList<DataModel> jsonDataModel = new ArrayList<>();
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isChecked = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        listView = findViewById(R.id.listView);
        checkBox = findViewById(R.id.checkBox);
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
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Activity2.this, "Session Ended", Toast.LENGTH_LONG).show();
        finish();
        return;
    }

}