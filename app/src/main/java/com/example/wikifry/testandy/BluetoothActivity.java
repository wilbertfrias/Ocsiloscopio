package com.example.wikifry.testandy;

import android.app.ListActivity;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.wikifry.testandy.adapters.BluetoothDevicesAdapter;
import com.example.wikifry.testandy.models.BTDevice;

import java.util.ArrayList;

public class BluetoothActivity extends ListActivity{

    private ArrayList<BTDevice> btDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        populateBtDevList();
        ArrayAdapter<BTDevice> adapter = new BluetoothDevicesAdapter(this, populateBtDevList());

        //Esta yo la hice buscando en blocks
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BTDevice btDev = btDeviceList.get(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("macAddress", btDev.getMacAddress());
                returnIntent.putExtra("nameDevice", btDev.getName());

                setResult(2, returnIntent);
                finish();
            }
        });

    }



    //Obtenemos los dispositivos, sus nombres y sus MACs. Esta App no escanea
    //usara los dispositivos conectados previamente, así que primero, emparejen sus arduinos

    private ArrayList<BTDevice> populateBtDevList()
    {
        this.btDeviceList = new ArrayList<BTDevice>();
        final ArrayList<String> BTmacs = getIntent().getStringArrayListExtra("BTmacs");
        final ArrayList<String> BTnames = getIntent().getStringArrayListExtra("BTnames");

        int contador=0;

        for (String name:BTnames)
        {

            Log.i("listas", BTnames.get(contador));
            Log.i("listas", BTmacs.get(contador));

            btDeviceList.add(new BTDevice(BTnames.get(contador), BTmacs.get(contador) ));
            contador++;

        }

        return btDeviceList;

    }
}
