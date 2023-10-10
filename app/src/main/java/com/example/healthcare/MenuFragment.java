package com.example.healthcare;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import at.grabner.circleprogress.CircleProgressView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    private CircleProgressView circleProgressView;
    private Button StatBtn;
    private String stat;
    private float progressValue;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseDatabase mFirebaseDatabase;


    public MenuFragment() {
    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private Button btn_logout;
    private Button btn_withdrawal;
    private Button btn_profile;
    private Button btn_record;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        User user = new User();


        //내정보
        btn_profile = view.findViewById(R.id.btn_proflie);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            Intent intent = null;
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    intent = new Intent(getActivity(), ProfileActivity.class);
                }
                startActivity(intent);
            }
        });

        //최근 운동 기록
        btn_record = view.findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    intent = new Intent(getActivity(), RecordListActivity.class);
                }
                startActivity(intent);
            }
        });

        // 원형 프로그래스 바 설정 뷰
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

                                    // BMI 계산
                                    float weight = Float.parseFloat(user.getWeight());
                                    float height = Float.parseFloat(user.getHeight());
                                    String sex = user.getSex();

                                    progressValue = (float) (weight / Math.pow(height / 100, 2));
                                    String stat = calculateStat(sex, progressValue);

                                    circleProgressView = view.findViewById(R.id.cpb_circlebar);
                                    Button Stat = view.findViewById(R.id.StatBtn);
                                    Stat.setText(stat);
                                    circleProgressView.setValue(progressValue);
                                    updateProgressColors(stat);
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    // 변경된 데이터 처리
                                    User user = dataSnapshot.getValue(User.class);

                                    // BMI 계산
                                    float weight = Float.parseFloat(user.getWeight());
                                    float height = Float.parseFloat(user.getHeight());
                                    String sex = user.getSex();

                                    progressValue = (float) (weight / Math.pow(height / 100, 2));
                                    String stat = calculateStat(sex, progressValue);

                                    circleProgressView = view.findViewById(R.id.cpb_circlebar);
                                    Button Stat = view.findViewById(R.id.StatBtn);
                                    Stat.setText(stat);
                                    circleProgressView.setValue(progressValue);
                                    updateProgressColors(stat);
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
                    mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid()).child("/BeforeData")
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    User user = dataSnapshot.getValue(User.class);

                                    // BMI 계산
                                    float weight = Float.parseFloat(user.getWeight());
                                    float height = Float.parseFloat(user.getHeight());
                                    String sex = user.getSex();

                                    progressValue = (float) (weight / Math.pow(height / 100, 2));
                                    String stat = calculateStat(sex, progressValue);

                                    circleProgressView = view.findViewById(R.id.cpb_circlebar);
                                    Button Stat = view.findViewById(R.id.StatBtn);
                                    Stat.setText(stat);
                                    circleProgressView.setValue(progressValue);
                                    updateProgressColors(stat);
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    // 변경된 데이터 처리
                                    User user = dataSnapshot.getValue(User.class);

                                    // BMI 계산
                                    float weight = Float.parseFloat(user.getWeight());
                                    float height = Float.parseFloat(user.getHeight());
                                    String sex = user.getSex();

                                    progressValue = (float) (weight / Math.pow(height / 100, 2));
                                    String stat = calculateStat(sex, progressValue);

                                    circleProgressView = view.findViewById(R.id.cpb_circlebar);
                                    Button Stat = view.findViewById(R.id.StatBtn);
                                    Stat.setText(stat);
                                    circleProgressView.setValue(progressValue);
                                    updateProgressColors(stat);
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
                // 처리할 오류가 있으면 여기에 작성
            }
        });

        //로그아웃
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                new AlertDialog.Builder(view.getContext())
                        .setMessage("로그아웃 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mFirebaseAuth.signOut();
                                GoogleSignIn.getClient(getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                                Intent intent = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                    intent = new Intent(getActivity(), AuthActivity.class);
                                }
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
            }
        });

        //회원탈퇴
        btn_withdrawal = view.findViewById(R.id.btn_withdrawal);
        btn_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage("정말 회원탈퇴 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(view.getContext())
                                        .setMessage("탈퇴하시면 정보는 전부 삭제됩니다.\n정말 삭제하시겠습니까?")
                                        .setCancelable(false)
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which){


                                                mFirebaseDatabase.getReference("memos/" + mFirebaseAuth.getUid()+"/BeforeData")
                                                        .addChildEventListener(new ChildEventListener() {
                                                            @Override
                                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                                Intent intent = null;
                                                                User user = dataSnapshot.getValue(User.class);

                                                                Log.d("FCM_TEST", Long.toString(user.getCount()));
                                                                mFirebaseDatabase.getReference("memos/users/usersID" + user.getCount() + "/userData").removeValue();
                                                                mFirebaseDatabase.getReference("memos/" + mFirebaseUser.getUid()).removeValue();
                                                                mFirebaseAuth.getCurrentUser().delete();
                                                                GoogleSignIn.getClient(getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                                    intent = new Intent(getActivity(), AuthActivity.class);
                                                                }
                                                                startActivity(intent);
                                                                getActivity().finish();
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
                                        })
                                        .setNegativeButton("아니오", null)
                                        .show();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
            }
        });
        return view;
    }

    // 원형 프로그래스 바 색상 변경값
    private void updateProgressColors(String Stat) {
        int barColor;

        if (Stat.equalsIgnoreCase("저체중")) {
            barColor = Color.parseColor("#FFFF00");
        } else if (Stat.equalsIgnoreCase("정상체중")) {
            barColor = Color.parseColor("#00FF00");
        } else if (Stat.equalsIgnoreCase("과체중")) {
            barColor = Color.parseColor("#FFA500");
        } else if (Stat.equalsIgnoreCase("경도비만")) {
            barColor = Color.parseColor("#800080");
        } else if (Stat.equalsIgnoreCase("고도비만")) {
            barColor = Color.parseColor("#FF0000");
        } else if (Stat.equalsIgnoreCase("고위험군")) {
            barColor = Color.parseColor("#FF6666");
        } else {
            barColor = Color.parseColor("#FFFFFF");
        }

        circleProgressView.setBarColor(barColor);
    }

    // 성별과 progressValue를 기반으로 Stat을 계산하는 메서드
    private String calculateStat(String sex, float progressValue) {
        if ("남".equals(sex)) {
            if (progressValue < 20) {
                return "저체중";
            } else if (progressValue >= 20 && progressValue <= 24.9) {
                return "정상체중";
            } else if (progressValue >= 25 && progressValue <= 29.9) {
                return "과체중";
            } else if (progressValue >= 30 && progressValue <= 34.9) {
                return "경도비만";
            } else if (progressValue >= 35 && progressValue <= 39.9) {
                return "고도비만";
            } else {
                return "고위험군";
            }
        } else {
            if (progressValue < 18.5) {
                return "저체중";
            } else if (progressValue >= 18.5 && progressValue <= 23.9) {
                return "정상체중";
            } else if (progressValue >= 24 && progressValue <= 29.9) {
                return "과체중";
            } else if (progressValue >= 30 && progressValue <= 34.9) {
                return "경도비만";
            } else if (progressValue >= 35 && progressValue <= 39.9) {
                return "고도비만";
            } else {
                return "고위험군";
            }
        }
    }
}