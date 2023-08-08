package com.example.healthcare.Guide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.R;
import com.example.healthcare.java.CameraXLivePreviewActivity;

public class PushUpsGuideActivity extends AppCompatActivity {

    private Button startButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup_guide);

        startButton = (Button) findViewById(R.id.pushup_start_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PushUpsGuideActivity.this, CameraXLivePreviewActivity.class);
                intent.putExtra("Health", 2);
                startActivity(intent);
                finish();
            }
        });
    }
}