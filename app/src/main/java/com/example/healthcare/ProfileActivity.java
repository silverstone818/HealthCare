package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtEmail, txtName;

    private Button btn_move, btn_cancel;
    private TextView age_text, age_text2;
    private TextView height_text, height_text2;
    private TextView weight_text, weight_text2;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private TextView sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        User user = new User();
        age_text = (TextView) findViewById(R.id.age_text);
        height_text = (TextView) findViewById(R.id.height_text);
        weight_text = (TextView) findViewById(R.id.weight_text);
        age_text2 = (TextView) findViewById(R.id.age_text2);
        height_text2 = (TextView) findViewById(R.id.height_text2);
        weight_text2 = (TextView) findViewById(R.id.weight_text2);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail.setText(mFirebaseUser.getEmail());
        txtName.setText(mFirebaseUser.getDisplayName());
        sex = (TextView) findViewById(R.id.sex_group);
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        DatabaseReference userRef = mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid()).child("/AfterData");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하는 경우
                    mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid()).child("/AfterData")
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    User user = dataSnapshot.getValue(User.class);
                                    age_text2.setText(user.getAge());
                                    height_text2.setText(user.getHeight());
                                    weight_text2.setText(user.getWeight());
                                    // user 데이터를 사용하여 출력
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    // 변경된 데이터 처리
                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                    // 삭제된 데이터 처리
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    // 이동된 데이터 처리
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // 처리할 오류가 있으면 여기에 작성
                                }
                            });
                } else {
                    // 데이터가 존재하지 않는 경우
                    mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid()).child("/BeforeData")
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    User user = dataSnapshot.getValue(User.class);
                                    age_text2.setText(user.getAge());
                                    height_text2.setText(user.getHeight());
                                    weight_text2.setText(user.getWeight());
                                    sex.setText(user.getSex());
                                    // user 데이터를 사용하여 출력
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    // 변경된 데이터 처리
                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                    // 삭제된 데이터 처리
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    // 이동된 데이터 처리
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // 처리할 오류가 있으면 여기에 작성
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기가 실패한 경우

            }
        });

        mFirebaseDatabase.getReference("memos/" + mFirebaseAuth.getUid()+"/BeforeData")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User user = dataSnapshot.getValue(User.class);
                        age_text.setText(user.getAge());
                        height_text.setText(user.getHeight());
                        weight_text.setText(user.getWeight());
                        // user 데이터를 사용하여 출력
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // 변경된 데이터 처리
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // 삭제된 데이터 처리
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // 이동된 데이터 처리
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 처리할 오류가 있으면 여기에 작성
                    }
                });



        btn_move = (Button) findViewById(R.id.btn_move);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SubMenuActivity.class));
                finish();
            }
        });
    }

    public void onBackPressed() {
        finish();
    }
}