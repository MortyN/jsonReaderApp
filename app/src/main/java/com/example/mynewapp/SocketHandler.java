package com.example.mynewapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SocketHandler extends Service {

    public static final int SERVERPORT = 4999;
    public static final String START_SERVER = "startserver";
    public static final String STOP_SERVER = "stopserver";

    public static final String ITEMADD_FEEDBACK = "com.example.mytestapp.ITEMADD_FEEDBACK";
    public static final String IPADDRESS_TEXT = "com.example.mytestapp.IPADDRESS_TEXT";
    public static final String ADDITEM_TEXT = "com.example.mytestapp.ADDITEM_TEXT";
    public static final String STATE = "com.example.mytestapp.STATE";

//    ArrayList<String> jsonList = new ArrayList<>();
    Set<String> jsonListSet = null;
    public ArrayList<String> jsonList = new ArrayList<>();
    Thread serverThread;
    Socket s;

    public int state;
    public String ipAddress;
    public String addItem;
    public String removeItem;
    public ArrayList<String> removeList;
    boolean listIsSent = false;
    boolean connected = false;
    boolean intentStart = false;
    String feedbackNum;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //action set by setAction() in activity
        ipAddress = intent.getStringExtra(IPADDRESS_TEXT);
        String stateString = intent.getStringExtra(STATE);
        addItem = intent.getStringExtra(ADDITEM_TEXT);
        state = Integer.parseInt(stateString);
        removeList = intent.getStringArrayListExtra("REMOVELIST");
        String action = intent.getAction();

        if (action.equals(START_SERVER)) {
            //start your server thread from here
            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
        }

        if (action.equals(STOP_SERVER)) {
            //stop server
            if (s != null) {
                try {
                    s.close();
                } catch (IOException ignored) {
                }
            }
        }
        //configures behaviour if service is killed by system
        return START_REDELIVER_INTENT;
    }

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startIntent() throws IOException, ClassNotFoundException {
        Intent dialogIntent = new Intent(this, Activity2.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putStringArrayListExtra("MOBARR", jsonList);
        startActivity(dialogIntent);

    }

    public void itemFeedbackIntent() {
        Activity2.AppReceiver mAppReceiver = new Activity2.AppReceiver();
        registerReceiver(mAppReceiver, new IntentFilter("MyReceiver"));
        Intent intents = new Intent("MyReceiver");
        intents.putStringArrayListExtra("MOBARR", jsonList);
        sendBroadcast(intents);


    }

    public void refreshList() throws IOException, ClassNotFoundException {
        jsonList = new ArrayList<>();
        InputStream inpuStream = s.getInputStream();
        ObjectInputStream obIn = new ObjectInputStream(inpuStream);
        List<String> jsoSocketList = (List<String>) obIn.readObject();
        jsonList.addAll(jsoSocketList);

        Set set = new HashSet(jsonList);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("SHARED_PREFS_FILE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("SETLIST", set);
        editor.apply();
        editor.commit();
    }


    class ServerThread implements Runnable {

        public void run() {
            try {
                if (!connected) {
                    s = new Socket(ipAddress, SERVERPORT);
                    s.setKeepAlive(true);
                    connected = true;

                    OutputStream stateStream = s.getOutputStream();
                    DataOutputStream stateOutput = new DataOutputStream(stateStream);
                    stateOutput.writeInt(state);
                    stateOutput.flush();

                    InputStream inputStream = s.getInputStream();
                    ObjectInputStream objIn = new ObjectInputStream(inputStream);
                    List<String> jsonSocketList = (List<String>) objIn.readObject();
                    jsonList.addAll(jsonSocketList);
                    listIsSent = true;


                    startIntent();

                } else {
                    switch (state) {
                        case 1:
                            jsonList.clear();
                            OutputStream stateStream = s.getOutputStream();
                            DataOutputStream stateOutput = new DataOutputStream(stateStream);
                            stateOutput.writeInt(1);
                            InputStream inputStream = s.getInputStream();
                            ObjectInputStream objIn = new ObjectInputStream(inputStream);
                            List<String> jsonSocketList = (List<String>) objIn.readObject();
                            jsonList.addAll(jsonSocketList);
                            stateOutput.flush();
                            stateStream.flush();
                            startIntent();


                            break;

                        case 2:
                            OutputStream itemStream = s.getOutputStream();
                            DataOutputStream itemOut = new DataOutputStream(itemStream);
                            itemOut.writeInt(state);
                            itemOut.writeUTF(addItem);
                            itemOut.flush();
                            itemStream.flush();
                            System.out.println("STATE 2 SUCCESS");



                            break;

                        case 3:

                            OutputStream removeStream = s.getOutputStream();
                            DataOutputStream removeDataOut = new DataOutputStream(removeStream);
                            removeDataOut.writeInt(state);
                            removeDataOut.flush();
                            ObjectOutputStream removeObj = new ObjectOutputStream(removeStream);
                            removeObj.writeObject(removeList);
                            removeObj.flush();
                            System.out.println("sent remove request");



                            break;
                    }
                    refreshList();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("SERVER IS NOT REACHABLE");
                e.printStackTrace();
            }
        }

    }

}
