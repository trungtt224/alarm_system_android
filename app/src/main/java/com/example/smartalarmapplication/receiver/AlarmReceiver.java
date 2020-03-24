package com.example.smartalarmapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.smartalarmapplication.service.AlarmService;
import com.example.smartalarmapplication.service.MyJobIntentService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String uri = intent.getExtras().getString("tone");
        Uri tone = Uri.parse(uri);
        MediaPlayer mediaPlayer = MediaPlayer.create(context, tone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        MyJobIntentService.enqueueWork(context, intent);
    }

}
