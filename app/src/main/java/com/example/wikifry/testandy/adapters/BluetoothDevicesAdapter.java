package com.example.wikifry.testandy.adapters;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wikifry.testandy.models.BTDevice;
import com.example.wikifry.testandy.R;

import java.util.List;

public class BluetoothDevicesAdapter extends ArrayAdapter<BTDevice>
{

    private final List<BTDevice> list;
    private final Activity context;

    static class ViewHolder
    {
        protected ImageButton logo;
        protected TextView btName;
        protected TextView macAddress;
    }

    public BluetoothDevicesAdapter(Activity context, List<BTDevice> list)
    {
        super(context, R.layout.activity_bluetooth_row, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;

        if(convertView == null) {

            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.activity_bluetooth_row, null);

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.btName = (TextView) view.findViewById(R.id.btName);
            viewHolder.macAddress = (TextView) view.findViewById(R.id.macAddress);

            view.setTag(viewHolder);
        }
            else
        {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.btName.setText(list.get(position).getName());
        holder.macAddress.setText(list.get(position).getMacAddress());

        return view;

    }
}
