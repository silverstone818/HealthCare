package com.example.healthcare;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserdataActivity extends AppCompatActivity {

    private Button btn_move2, btn_cancel;
    private EditText age_text;
    private EditText height_text;
    private EditText weight_text;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private TextView txtEmail2, txtName2, age_limit, height_limit, weight_limit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        User user = new User();

        age_text = (EditText) findViewById(R.id.age_text);
        height_text = (EditText) findViewById(R.id.height_text);
        weight_text = (EditText) findViewById(R.id.weight_text);
        txtEmail2 = (TextView) findViewById(R.id.txtEmail2);
        txtName2 = (TextView) findViewById(R.id.txtName2);
        age_limit = (TextView) findViewById(R.id.age_limit);
        height_limit = (TextView) findViewById(R.id.height_limit);
        weight_limit = (TextView) findViewById(R.id.weight_limit);
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        txtEmail2.setText(mFirebaseUser.getEmail());
        txtName2.setText(mFirebaseUser.getDisplayName());

        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                startActivity(new Intent(UserdataActivity.this, AuthActivity.class));
                finish();
            }
        });

        if(mFirebaseUser == null){
            startActivity(new Intent(UserdataActivity.this, AuthActivity.class));
            finish();
            return;
        }

        age_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = age_text.getText().toString();
                age_limit.setText(input.length()+"/3");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //키 글자 제한 수 표시
        height_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = height_text.getText().toString();
                height_limit.setText(input.length()+"/3");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //몸무게 글자 제한 수 표시
        weight_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = weight_text.getText().toString();
                weight_limit.setText(input.length()+"/3");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_move2 = findViewById(R.id.btn_move2);
        btn_move2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(age_text.getText().toString().length() == 0){
                    Toast.makeText(UserdataActivity.this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(height_text.getText().toString().length() == 0){
                    Toast.makeText(UserdataActivity.this, "키를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(weight_text.getText().toString().length() == 0){
                    Toast.makeText(UserdataActivity.this, "몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 수정할 User 객체 생성
                User updatedUser = new User();
                updatedUser.setAge(age_text.getText().toString());
                updatedUser.setHeight(height_text.getText().toString());
                updatedUser.setWeight(weight_text.getText().toString());

                // 수정할 데이터의 참조 경로 가져오기
                DatabaseReference memoRef = mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid()).child("/BeforeData/");

                // 수정할 데이터의 참조 경로와 고유 ID를 결합하여 해당 데이터의 참조 경로를 가져옵니다.
                DatabaseReference memoToUpdateRef = memoRef.child("profile");

                // 수정할 데이터의 값을 Map 객체로 만듭니다.
                Map<String, Object> updates = new HashMap<>();
                updates.put("age", updatedUser.getAge());
                updates.put("height", updatedUser.getHeight());
                updates.put("weight", updatedUser.getWeight());

                // 해당 데이터의 참조 경로에 updateChildren() 메소드를 호출하여 값을 수정합니다.
                memoToUpdateRef.updateChildren(updates);

                Button moveButton;
                moveButton = findViewById(R.id.btn_move2);
                moveButton.setOnClickListener(onClickListener);

                Intent intent = new Intent(UserdataActivity.this, TutorialActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UserdataActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }
    };
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("종료 하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseAuth.signOut();
                        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                        finish();
                    }
                })
                .setNegativeButton("아니오", null)
                .show();
    }
    private void saveMemo(){
        User updatedUser = new User();
        updatedUser.setAge(age_text.getText().toString());
        updatedUser.setHeight(height_text.getText().toString());
        updatedUser.setWeight(weight_text.getText().toString());

        // 수정할 데이터의 참조 경로 가져오기
        DatabaseReference memoRef = mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid());

        // 수정할 데이터의 참조 경로와 고유 ID를 결합하여 해당 데이터의 참조 경로를 가져옵니다.
        DatabaseReference memoToUpdateRef = memoRef.child("/BeforeData/profile");

        // 수정할 데이터의 값을 Map 객체로 만듭니다.
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", updatedUser.getAge());
        updates.put("height", updatedUser.getHeight());
        updates.put("weight", updatedUser.getWeight());

        // 해당 데이터의 참조 경로에 updateChildren() 메소드를 호출하여 값을 수정합니다.
        memoToUpdateRef.updateChildren(updates);
    }
}