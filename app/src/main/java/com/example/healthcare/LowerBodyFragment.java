package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.healthcare.Guide.SquatsGuideActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LowerBodyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LowerBodyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LowerBodyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LowerBodyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LowerBodyFragment newInstance(String param1, String param2) {
        LowerBodyFragment fragment = new LowerBodyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 레이아웃에서 버튼 객체를 가져옵니다.
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lower_body, container, false);

        ImageButton button = view.findViewById(R.id.btn_squats); // Fragment에서 버튼 객체를 가져옵니다.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null; // 다른 Activity로 전환하는 Intent 객체를 생성합니다.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    intent = new Intent(getActivity(), SquatsGuideActivity.class);
                }
                startActivity(intent); // Intent를 실행하여 다른 Activity로 전환합니다.
            }
        });

        return view;

    }
}