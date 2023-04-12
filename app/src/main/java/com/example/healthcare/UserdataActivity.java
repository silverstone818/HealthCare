package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserdataActivity extends AppCompatActivity {

    private Button btn_move2;
    private EditText age_text;
    private EditText height_text;
    private EditText weight_text;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata);

        age_text = (EditText) findViewById(R.id.age_text);
        height_text = (EditText) findViewById(R.id.height_text);
        weight_text = (EditText) findViewById(R.id.weight_text);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        if(mFirebaseUser == null){
            startActivity(new Intent(UserdataActivity.this, AuthActivity.class));
            finish();
            return;
        }

        btn_move2 = findViewById(R.id.btn_move2);
        btn_move2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMemo();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("memos/").child(uid);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            startActivity(new Intent(UserdataActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(UserdataActivity.this, "정보를 전부 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserdataActivity.this, "데이터를 읽지 못하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });


    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("종료 하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니오", null)
                .show();
    }
    private void saveMemo(){
        User user = new User();

        user.setAge(age_text.getText().toString());
        user.setHeight(height_text.getText().toString());
        user.setWeight(weight_text.getText().toString());

        mFirebaseDatabase.getReference("memos/"+mFirebaseUser.getUid())
                .push()
                .setValue(user);
    }
}