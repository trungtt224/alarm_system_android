package com.example.smartalarmapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String uri = intent.getExtras().getString("tone");
        Uri tone = Uri.parse(uri);
        Ringtone r = RingtoneManager.getRingtone(context, tone);
        r.play();
    }
}
