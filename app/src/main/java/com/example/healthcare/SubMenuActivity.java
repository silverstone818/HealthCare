package com.example.healthcare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SubMenuActivity extends AppCompatActivity {
    private Button btn_move2;
    private EditText age_text;
    private EditText height_text;
    private EditText weight_text;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu);

        age_text = (EditText) findViewById(R.id.age_text);
        height_text = (EditText) findViewById(R.id.height_text);
        weight_text = (EditText) findViewById(R.id.weight_text);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        if(mFirebaseUser == null){
            startActivity(new Intent(SubMenuActivity.this, AuthActivity.class));
            finish();
            return;
        }

        btn_move2 = findViewById(R.id.btn_move2);
        btn_move2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수정할 User 객체 생성
                User updatedUser = new User();
                updatedUser.setAge(age_text.getText().toString());
                updatedUser.setHeight(height_text.getText().toString());
                updatedUser.setWeight(weight_text.getText().toString());

                // 수정할 데이터의 참조 경로 가져오기
                DatabaseReference memoRef = mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid());

                // 수정할 데이터의 참조 경로와 고유 ID를 결합하여 해당 데이터의 참조 경로를 가져옵니다.
                DatabaseReference memoToUpdateRef = memoRef.child("profile");

                // 수정할 데이터의 값을 Map 객체로 만듭니다.
                Map<String, Object> updates = new HashMap<>();
                updates.put("age", updatedUser.getAge());
                updates.put("height", updatedUser.getHeight());
                updates.put("weight", updatedUser.getWeight());

                // 해당 데이터의 참조 경로에 updateChildren() 메소드를 호출하여 값을 수정합니다.
                memoToUpdateRef.updateChildren(updates);
                startActivity(new Intent(SubMenuActivity.this, MainActivity.class));
                finish();
            }

        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(SubMenuActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
