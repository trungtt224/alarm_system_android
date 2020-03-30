package com.example.smartalarmapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartalarmapplication.adapter.AlarmListAdapter;
import com.example.smartalarmapplication.helper.DbHelper;
import com.example.smartalarmapplication.model.Alarm;
import com.example.smartalarmapplication.receiver.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private DbHelper db;
    private AlarmListAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ListView listViewAlarm = findViewById(R.id.lvAlarm);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        db = new DbHelper(this);
        setSupportActionBar(toolbar);

        List<Alarm> alarms = db.getAlarms();
        alarmAdapter = new AlarmListAdapter(MainActivity.this, R.layout.list_alarm, alarms);
        listViewAlarm.setAdapter(alarmAdapter);
        listViewAlarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Alarm alarm = (Alarm) parent.getItemAtPosition(position);
                openDialog("update", alarm);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_alarm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addAlarm) {
            // If create alarm set alarm to null
            openDialog("create", null);
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialog(String type, final Alarm alarm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alarm_picker, null);
        // Set up spinner select Ringtone
        final Spinner spinner = view.findViewById(R.id.spinner);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);
        final Map<String, String> ringList = getRingtone();
        ArrayList<String> titles = new ArrayList<>(ringList.keySet());
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, titles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(view);

        // Set up positive button: (Two type action: create alarm and update alarm)
        if (type.equals("create")) {
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    long timeAlarm = getTimeAlarmViaTimePicker(timePicker);
                    String tone = getRingtone(spinner, ringList);
                    Alarm alarm = new Alarm(timeAlarm, tone);
                    int id = db.addPlayer(alarm);
                    createBroadcast(id, tone);
                    setAlarm(timeAlarm);
                    reloadListView(alarmAdapter);
                }
            });
        } else if (type.equals("update")) {
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    long timeAlarm = getTimeAlarmViaTimePicker(timePicker);
                    String tone = getRingtone(spinner, ringList);
                    alarm.setTimeAlarm(timeAlarm);
                    alarm.setRingtone(tone);
                    alarm.setIsActive(1);
                    db.updateAlarm(alarm);
                    createBroadcast(alarm.getId(), tone);
                    setAlarm(timeAlarm);
                    reloadListView(alarmAdapter);
                }
            });
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getRingtone(Spinner spinner, Map<String, String> ringList) {
        String name = spinner.getSelectedItem().toString();
        return ringList.get(name);
    }

    private Long getTimeAlarmViaTimePicker(TimePicker timePicker) {
        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Check if time alarm less than current time -> Set time alarm is tomorrow
        long timeAlarm = calendar.getTimeInMillis();
        if (timeAlarm < System.currentTimeMillis()) {
            timeAlarm = timeAlarm + 24 * 60 * 60 * 1000;
        }
        return timeAlarm;
    }

    private void createBroadcast(int id, String tone) {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("tone", tone);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                Integer.parseInt(id + ""), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void reloadListView(AlarmListAdapter alarmAdapter) {
        alarmAdapter.clear();
        alarmAdapter.addAll(db.getAlarms());
        alarmAdapter.notifyDataSetChanged();
    }

    private void setAlarm(long timeAlarm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
        }
    }

    private Map<String, String> getRingtone() {
        RingtoneManager manager = new RingtoneManager(getApplicationContext());
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        Map<String, String> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            list.put(notificationTitle, notificationUri);
        }

        return list;
    }

    @Override
    protected void onResume() {
        alarmAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
