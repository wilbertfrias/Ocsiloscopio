package com.example.wikifry.testandy;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import android.content.BroadcastReceiver;
import android.widget.Toast;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import java.io.InputStream;
import  java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.example.wikifry.testandy.models.BTDevice;
import com.example.wikifry.testandy.adapters.BluetoothDevicesAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private Button BTbutton;

    private DrawView myDraw;

    private int height = 50;
    private int width = 100;

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> BluetoothDevices = null;
    ArrayList<String> macs = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();

    Handler bluetoothIn;
    final int handlerState = 0;
    int readBufferPosition;
    byte[] readBuffer;

    volatile boolean stopWorker;

    private BluetoothSocket btSocket = null;

    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //MAC
    private static  String address = null;

    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = findViewById(R.id.textView);

        setContentView(R.layout.activity_main);

        myDraw = findViewById(R.id.myView);
        myDraw.setBackgroundColor(Color.WHITE);
        myDraw.invalidate();

        BTbutton = findViewById(R.id.BTdev);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.wikifry.testandy.NotificationService");
        registerReceiver(receiver, filter);

        BTbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btAdapter.isEnabled()) {
                    //solicita activar el BT
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, /*REQUEST_ENABLE_BT*/1);
                } else {
                    dialogo();
                    myDraw.setSize(myDraw.getWidth(), myDraw.getHeight());
                }
            }
        });

        bluetoothIn = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                if(msg.what == handlerState)
                {
                    float f = 0;
                    String mess = (String) msg.obj;
                    try {
                        f = Float.valueOf( mess );

                    }catch (Exception e)
                    {

                    }
                    try{textView.setText(mess);}catch (Exception e){}
                    myDraw.insertData(f);
                    myDraw.invalidate();
                }else{}

                return false;


            }
        });

    }

    protected void testNotification(){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("TitleTest")
                .setContentText("ContentText")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(1, mBuilder.build());
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private void dialogo()
    {
        this.BluetoothDevices = btAdapter.getBondedDevices();

        macs.clear();
        names.clear();

        int contador = 0;
        for (BluetoothDevice btdevice:this.BluetoothDevices)
        {
            //Toast.makeText(this, btdevice.getName(), Toast.LENGTH_LONG);
            macs.add(btdevice.getAddress());
            names.add(btdevice.getName());

            contador++;
        }
        final Intent intent = new Intent(this, BluetoothActivity.class);

        intent.putStringArrayListExtra("BTmacs", macs);
        intent.putStringArrayListExtra("BTnames", names);

        //createSingleListDialog().show();
        startActivityForResult(intent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case 1: {
                if (resultCode == RESULT_OK) {
                    dialogo();
                }

                break;
            }

        }

        switch (resultCode)
        {
            case 2:
                Toast.makeText(this, data.getStringExtra("macAddress"), Toast.LENGTH_LONG).show();
                Toast.makeText(this, data.getStringExtra("nameDevice"), Toast.LENGTH_LONG).show();

                BluetoothDevice device = btAdapter.getRemoteDevice( data.getStringExtra("macAddress") );

                try {
                    btSocket = createBluetoothSocket(device);
                }catch (IOException e)
                {
                    Toast.makeText(getApplicationContext(), "La creacion del socket fallo", Toast.LENGTH_LONG).show();
                }

                //Establish

                try{
                    btSocket.connect();
                }catch (IOException e){
                    try {
                        btSocket.close();
                    }catch (IOException e2)
                    {

                    }
                }
                if(btSocket.isConnected()) {
                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                    Toast.makeText(getApplicationContext(), "conectado", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "la conexión del socket fallo", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //create

        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            byte[] buffer = new byte[256];
            int bytes;

            try {
                //Create IO Streams
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {

            stopWorker = false;
            byte[] buffer = new byte[256];
            int bytes;

            final byte delimiter = 10; //ASCII for newline character
            while(!Thread.currentThread().isInterrupted() && !stopWorker){
                try {
                    int bytesAvalaible = mmInStream.available();
                    byte[] packetB = new byte[bytesAvalaible];

                    readBuffer = new byte[bytesAvalaible];
                    readBufferPosition = 0;

                    mmInStream.read(packetB);
                    for (int i = 0; i < bytesAvalaible; i++) {

                        byte b = packetB[i];
                        if (b == delimiter) {
                            byte[] encodeBytes = new byte[readBufferPosition];
                            System.arraycopy(readBuffer, 0, encodeBytes, 0, encodeBytes.length);

                            final String data = new String(encodeBytes, "US-ASCII");
                            readBufferPosition = 0;
                            Message m = new Message();
                            m.what = handlerState;
                            m.setTarget(bluetoothIn);
                            m.obj = data;
                            m.sendToTarget();

                        } else {
                            readBuffer[readBufferPosition++] = b;
                        }

                    }
                } catch (IOException e) {
                    stopWorker = true;
                }
            }

        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {

                mmOutStream.write(msgBuffer);

            } catch (IOException e) {

                Toast.makeText(getApplicationContext(), "La conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
