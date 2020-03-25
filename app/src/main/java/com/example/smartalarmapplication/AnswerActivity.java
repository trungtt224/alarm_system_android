package com.example.smartalarmapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartalarmapplication.helper.DbHelper;
import com.example.smartalarmapplication.service.AlarmService;

public class AnswerActivity extends AppCompatActivity {

    private DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        db = new DbHelper(this);

        Button btnAnswer = findViewById(R.id.btnAnswer);
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), AlarmService.class));
                int id = getIntent().getIntExtra("id", 0);
                db.updateStatus(id, 0);
            }
        });

        Button btnWrong1 = findViewById(R.id.btnWrong1);
        btnWrong1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Wrong answer", Toast.LENGTH_LONG).show();
            }
        });

        Button btnWrong2 = findViewById(R.id.btnWrong2);
        btnWrong2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Wrong answer", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Please choose answer", Toast.LENGTH_LONG).show();
    }
}
