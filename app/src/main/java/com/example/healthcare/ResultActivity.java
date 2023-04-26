package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    private int numSquats;
    private ArrayList<Double> maxAngle;
    private ArrayList<Boolean> waist_banding;
    private ArrayList<Integer> contract;
    private ArrayList<Boolean> Tension;
    private TextView txt_numSquats;
    private TextView txt_maxAngle;
    private TextView txt_waist_banding;
    private TextView txt_contract;
    private TextView txt_Tension;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        numSquats = intent.getIntExtra("numSquats", 12);
        maxAngle = (ArrayList<Double>) intent.getSerializableExtra("maxAngle");
        waist_banding = (ArrayList<Boolean>) intent.getSerializableExtra("waist_banding");
        Tension = (ArrayList<Boolean>) intent.getSerializableExtra("Tension");
        contract = (ArrayList<Integer>) intent.getSerializableExtra("contract");

        txt_numSquats = (TextView) findViewById(R.id.txt_numSquats);
        txt_maxAngle = (TextView) findViewById(R.id.txt_maxAngle);
        txt_waist_banding = (TextView) findViewById(R.id.txt_waist_banding);
        txt_contract = (TextView) findViewById(R.id.txt_contract);
        txt_Tension = (TextView) findViewById(R.id.txt_Tension);

        txt_numSquats.setText("스쿼트 개수 : " + Integer.toString(numSquats));
        txt_maxAngle.setText("이완 : " + maxAngle);
        txt_waist_banding.setText("허리 굽음 : " + waist_banding);
        txt_Tension.setText("긴장 : " + Tension);
        txt_contract.setText("수축 : " + contract);

    }
}