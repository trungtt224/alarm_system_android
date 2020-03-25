package com.example.smartalarmapplication.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.smartalarmapplication.AnswerActivity;
import com.example.smartalarmapplication.MainActivity;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String uri = intent.getExtras().getString("tone");
        Uri tone = Uri.parse(uri);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), tone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Intent targetIntent = new Intent(getApplicationContext(), AnswerActivity.class);
        targetIntent.putExtra("id", intent.getExtras().getInt("id"));
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(targetIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        Intent targetIntent = new Intent(getApplicationContext(), MainActivity.class);
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(targetIntent);
    }
}
