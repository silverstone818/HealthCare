package com.example.healthcare.Guide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.R;
import com.example.healthcare.java.CameraXLivePreviewActivity;

public class SquatsGuideActivity extends AppCompatActivity {

    private Button startButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_guide);

        startButton = (Button) findViewById(R.id.squat_start_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SquatsGuideActivity.this, CameraXLivePreviewActivity.class);
                intent.putExtra("Health", 1);
                startActivity(intent);
                finish();
            }
        });
    }
}