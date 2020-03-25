package com.example.smartalarmapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.smartalarmapplication.model.Alarm;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Alarm_DB";
    private static final String TABLE_NAME = "tbl_alarm";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "timeAlarm";
    private static final String KEY_RINGTONE = "ringtone";
    private static final String KEY_ISACTIVE = "isActive";
    private static final String[] COLUMNS = {KEY_ID, KEY_TIME, KEY_RINGTONE, KEY_ISACTIVE};

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE "+ TABLE_NAME + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TIME +" TEXT, "
                + KEY_RINGTONE + " TEXT, "
                + KEY_ISACTIVE + " INTEGER )";
        System.out.println(CREATION_TABLE);
        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public int addPlayer(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, alarm.getTimeAlarm());
        values.put(KEY_RINGTONE, alarm.getRingtone());
        values.put(KEY_ISACTIVE, 1);

        int id = (int) db.insert(TABLE_NAME,null, values);
        db.close();
        return id;
    }

    public List<Alarm> getAlarms() {
        List<Alarm> alarms = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
             while (!cursor.isAfterLast()) {
                 Alarm alarm = rowMapper(cursor);
                 alarms.add(alarm);
                 cursor.moveToNext();
             }
        }

        return alarms;
    }

    public int updateAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RINGTONE, alarm.getRingtone());
        values.put(KEY_TIME, alarm.getTimeAlarm());
        values.put(KEY_ISACTIVE, alarm.getIsActive());
        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(alarm.getId()) });

        db.close();
        return i;
    }

    public int updateStatus(int id, int isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ISACTIVE, isActive);
        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(id) });

        db.close();
        return i;
    }

    private Alarm rowMapper(Cursor cursor) {
        Alarm alarm = new Alarm();
        alarm.setId(cursor.getInt(0));
        alarm.setTimeAlarm(Long.valueOf(cursor.getString(1)));
        alarm.setRingtone(cursor.getString(2));
        alarm.setIsActive(cursor.getInt(3));
        return alarm;
    }
}
