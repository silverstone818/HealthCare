package com.example.healthcare.Guide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.R;
import com.example.healthcare.java.CameraXLivePreviewActivity;

public class PullupGuideActivity extends AppCompatActivity {

    private Button PullupStartButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pullup_guide);


        PullupStartButton = (Button) findViewById(R.id.pullup_start_btn);
        PullupStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null; // 다른 Activity로 전환하는 Intent 객체를 생성합니다.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    intent = new Intent(PullupGuideActivity.this, CameraXLivePreviewActivity.class);
                    intent.putExtra("Health", 3);
                }
                startActivity(intent);
            }
        });
    }
}