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

    private Toolbar toolbar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private DbHelper db;
    private List<Alarm> alarms;
    private ListView listViewAlarm;
    private AlarmListAdapter alarmAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        listViewAlarm = findViewById(R.id.lvAlarm);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        db = new DbHelper(this);
        setSupportActionBar(toolbar);

        alarms = db.getAlarms();
        alarmAdapter = new AlarmListAdapter(MainActivity.this, R.layout.list_alarm,  alarms);
        listViewAlarm.setAdapter(alarmAdapter);
        listViewAlarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Alarm alarm = (Alarm) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), alarm.getId() + "", Toast.LENGTH_LONG).show();
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
            openDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alarm_picker, null);
        final Spinner spinner = view.findViewById(R.id.spinner);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);
        final Map<String, String> ringList = getRingtone();
        ArrayList<String> titles = new ArrayList<>(ringList.keySet());
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, titles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(view);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                long timeAlarm = calendar.getTimeInMillis();
                String tone = getRingtone(spinner, ringList);
                Alarm alarm = new Alarm(timeAlarm, tone);
                int id = db.addPlayer(alarm);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent.putExtra("id", id);
                intent.putExtra("tone", tone);
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                        Integer.parseInt(id + ""), intent, 0);
                setAlarm(calendar.getTimeInMillis());
                alarmAdapter.clear();
                alarmAdapter.addAll(db.getAlarms());
                alarmAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getRingtone(Spinner spinner, Map<String, String> ringList) {
        String name = spinner.getSelectedItem().toString();
        return ringList.get(name);
    }

    private void setAlarm(long timeAlarm) {
        if (timeAlarm < System.currentTimeMillis()) {
            timeAlarm = timeAlarm + 24 * 60 * 60 * 1000;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAlarm, pendingIntent);
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
}
