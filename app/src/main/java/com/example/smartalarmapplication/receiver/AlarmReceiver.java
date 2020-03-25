package com.example.smartalarmapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smartalarmapplication.service.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 0);
        String uri = intent.getExtras().getString("tone");
        Intent alarmIntent = new Intent(context, AlarmService.class);
        alarmIntent.putExtra("tone", uri);
        alarmIntent.putExtra("id", id);
        context.startService(alarmIntent);
    }
}
