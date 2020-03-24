package com.example.smartalarmapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smartalarmapplication.AnswerActivity;

public class AlarmService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AlarmService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(getApplicationContext(), "My name Trung", Toast.LENGTH_LONG);
        Intent launchIntent = new Intent(getBaseContext(), AnswerActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(launchIntent);
    }
}
