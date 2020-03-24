package com.example.smartalarmapplication.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartalarmapplication.R;
import com.example.smartalarmapplication.model.Alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmListAdapter extends ArrayAdapter<Alarm> {
    Activity context;
    List<Alarm> alarms;
    int resource;
    public AlarmListAdapter(@NonNull Activity context, int resource, List<Alarm> alarms) {
        super(context, resource, alarms);
        this.context = context;
        this.alarms = alarms;
        this.resource = resource;
    }

//    @Override
//    public int getCount() {
//        return alarms.size();
//    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(resource, null);
        TextView timeView = rowView.findViewById(R.id.tvTime);
        TextView dateView = rowView.findViewById(R.id.tvDate);
        ImageView activeView = rowView.findViewById(R.id.ivActive);
        // convert millis to time, day, month, year, dayOfWeek
        String time = fromMilisTo(this.alarms.get(position).getTimeAlarm(), "hh:mm");
        String date = fromMilisTo(this.alarms.get(position).getTimeAlarm(), "dd-MM");
        String dayOfWeek = fromMilisTo(this.alarms.get(position).getTimeAlarm(), "EEEE");

        timeView.setText(time + " " + getAMOrPM((this.alarms.get(position).getTimeAlarm())));
        dateView.setText(dayOfWeek + ", " + date);

        // Set icon alarm is active or disable
        int isActive = this.alarms.get(position).getIsActive();
        if (isActive == 1) {
            activeView.setImageResource(R.drawable.clock_enable);
        } else {
            activeView.setImageResource(R.drawable.clock_disable);
        }

        return rowView;
    }

    private String fromMilisTo(long millis, String dateFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    private String getAMOrPM(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int a = calendar.get(Calendar.AM_PM);
        if (a == Calendar.AM) {
            return "AM";
        } else {
            return "PM";
        }
    }

}
