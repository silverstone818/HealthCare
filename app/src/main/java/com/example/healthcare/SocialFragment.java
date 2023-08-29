package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SocialFragment extends ListFragment {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference memoRef;
    private DatabaseReference usersRef;
    private CustomAdapter2 adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference("users"); // "users"는 실제 데이터베이스 경로에 맞게 수정해주세요
        adapter = new CustomAdapter2(getContext(), new ArrayList<>());
        adapter.clear();
        for(int i = 1; i <= 10; i++){
            mFirebaseDatabase.getReference("memos/users/usersID"+i)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            UserData userList = dataSnapshot.getValue(UserData.class);
                            if (userList != null) {
                                String date = userList.getEmail();
                                String name = userList.getName();

                                adapter.add(new users(name, date));
                                adapter.notifyDataSetChanged();
                            }
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

        ListView list = view.findViewById(android.R.id.list);

        list.setAdapter(adapter);

        return view;
    }
}

class users {
    private String name;
    private String email;

    public users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class UserData {
    private String name;
    private String email;

    public UserData() {
        // Default constructor required for Firebase
    }

    public UserData(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class CustomAdapter2 extends ArrayAdapter<users> {

    public CustomAdapter2(@NonNull Context context, List<users> exerciseItems) {
        super(context, 0, exerciseItems);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item2, parent, false);
        }

        users currentItem = getItem(position);

        TextView exerciseName = convertView.findViewById(R.id.exercise_name1);
        TextView exerciseDate = convertView.findViewById(R.id.exercise_email1);

        exerciseName.setText(currentItem.getName());
        exerciseDate.setText(currentItem.getEmail());

        return convertView;
    }
}