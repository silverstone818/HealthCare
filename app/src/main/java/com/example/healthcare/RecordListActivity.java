package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecordListActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference memoRef;
    private ValueEventListener valueEventListener;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot profileSnapshot = dataSnapshot.child("profile");
                    if (profileSnapshot != null) {
                        Together_group_list tgList = profileSnapshot.getValue(Together_group_list.class);
                        if (tgList != null) {
                            String date = tgList.getDate();
                            String name = tgList.getName();
                            adapter.clear();
                            adapter.add(new ExerciseItem(name, date));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("AngleTest", "Failed to get data from Firebase.");
            }
        };

        memoRef = mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid()).child("/record");
        memoRef.addValueEventListener(valueEventListener);

        ListView list = findViewById(R.id.listView1);

        adapter = new CustomAdapter(this, new ArrayList<ExerciseItem>());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExerciseItem selectedItem = adapter.getItem(position);
                Intent intent = new Intent(RecordListActivity.this, RecordActivity.class);
                intent.putExtra("Name", adapter.getItem(position).getName());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        memoRef.removeEventListener(valueEventListener);
    }
}


class ExerciseItem {
    private String name;
    private String date;

    public ExerciseItem(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

class CustomAdapter extends ArrayAdapter<ExerciseItem> {

    public CustomAdapter(@NonNull Context context, List<ExerciseItem> exerciseItems) {
        super(context, 0, exerciseItems);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        ExerciseItem currentItem = getItem(position);

        TextView exerciseName = convertView.findViewById(R.id.exercise_name);
        TextView exerciseDate = convertView.findViewById(R.id.exercise_date);

        exerciseName.setText("운동 이름: " + currentItem.getName());
        exerciseDate.setText("날짜: " + currentItem.getDate());

        return convertView;
    }
}
