package com.example.mynewapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.ContactsContract;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketHandler extends Service {
    public static final String START_SERVER = "startserver";
    public static final String STOP_SERVER = "stopserver";
    public static final int SERVERPORT = 4999;
    public static final String IPADDRESS_TEXT = "com.example.mytestapp.IPADDRESS_TEXT";

    ArrayList<String> jsonList = new ArrayList<>();
    Thread serverThread;
    ServerSocket serverSocket;
    boolean listIsSent = false;

    public String ipAddress;

    public void MyService() {

    }

    //called when the services starts
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //action set by setAction() in activity
        ipAddress = intent.getStringExtra(IPADDRESS_TEXT);
        String action = intent.getAction();

        if (action.equals(START_SERVER)) {
            //start your server thread from here
            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
        }

        if (action.equals(STOP_SERVER)) {
            //stop server
            if (serverSocket != null) {
                try {
                    serverSocket.close();
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

    public void startIntent() {
        Intent dialogIntent = new Intent(this, Activity2.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putStringArrayListExtra("MOBARR", jsonList);
        startActivity(dialogIntent);
    }

    class ServerThread implements Runnable {
        int state = 1;

        public void run() {
            try {
                Socket s = new Socket(ipAddress, SERVERPORT);

                OutputStream stateStream = s.getOutputStream();
                DataOutputStream stateOutput = new DataOutputStream(stateStream);
                stateOutput.writeInt(state);

                switch (state) {
                    case 1:
                        if (!listIsSent) {
                            InputStream inputStream = s.getInputStream();
                            ObjectInputStream objIn = new ObjectInputStream(inputStream);
                            List<String> jsonSocketList = (List<String>) objIn.readObject();
                            jsonList.addAll(jsonSocketList);
                            listIsSent = true;
                        } else {
                            startIntent();
                        }
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("SERVER IS NOT REACHABLE");
                e.printStackTrace();
            }
        }

    }

}
