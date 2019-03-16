package com.example.wikifry.testandy;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.Console;

public class NotificationService extends NotificationListenerService {

    @Override
    public void onCreate(){

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Intent i = new Intent("com.example.wikifry.testandy.NotificationService");
        i.putExtra("notification_event", "onNotificationPosted");
        sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        Intent i = new Intent("com.example.wikifry.testandy.NotificationService");
        i.putExtra("notification_event", "onNotificationRemoved");
        sendBroadcast(i);

    }

}
