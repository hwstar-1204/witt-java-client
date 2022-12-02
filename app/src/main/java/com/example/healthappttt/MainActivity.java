package com.example.healthappttt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.aware.DiscoverySession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button StartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartBtn = (Button) findViewById(R.id.startBtn); // 시작 버튼

        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), ExercizeRecordActivity.class);
                //startActivity(intent);
                myStartActivity(ExercizeRecordActivity.class);
            }
        });

        //화면전환 버튼
        Button btn_main;
        btn_main = findViewById(R.id.calender);

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(CalenderActivity.class);
            }
        });
    //화면전환 함수

    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}