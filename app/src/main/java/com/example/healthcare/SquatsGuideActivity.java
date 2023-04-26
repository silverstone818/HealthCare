package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.java.CameraXLivePreviewActivity;

public class SquatsGuideActivity extends AppCompatActivity {

    private Button startButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squats_guide);


        startButton = (Button) findViewById(R.id.start_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SquatsGuideActivity.this, CameraXLivePreviewActivity.class));
                finish();
            }
        });
    }
}