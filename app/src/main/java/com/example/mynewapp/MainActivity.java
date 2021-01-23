package com.example.mynewapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String IPADDRESS_TEXT = "com.example.mytestapp.IPADDRESS_TEXT";
    public static final String STATE = "com.example.mytestapp.STATE";
    public EditText serverIPInput;
    public String serverIP;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    public String state = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);

        serverIPInput = (EditText) findViewById(R.id.serverIPfield);
        button.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button:
                serverIP = serverIPInput.getText().toString();
                //will start the server
                Intent startServer = new Intent(this, SocketHandler.class);
                startServer.putExtra(IPADDRESS_TEXT, serverIP);
                startServer.putExtra(STATE, state);
                startServer.setAction(SocketHandler.START_SERVER);
                startService(startServer);
                break;
        }
    }


}