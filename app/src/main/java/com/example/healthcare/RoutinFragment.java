package com.example.healthcare;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class RoutinFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private WebView webView;

    public RoutinFragment() {
    }

    public static RoutinFragment newInstance(String param1, String param2) {
        RoutinFragment fragment = new RoutinFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routin, container, false);

        // WebView 초기화를 합니다.
        webView = view.findViewById(R.id.webView); // 여기서 수정: view.findViewById를 사용하여 view 내부에서 webView를 찾습니다.
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // JavaScript 사용 설정
        webView.setWebChromeClient(new WebChromeClient()); // 웹뷰를 사용하여 동영상을 재생하기 위해 WebChromeClient 설정

        // YouTube 동영상 ID를 가져와서 아래와 같이 설정합니다.
        String videoId = "YOUR_YOUTUBE_VIDEO_ID"; // 여기서 수정: 원하는 YouTube 동영상의 ID로 변경합니다.
        String embedUrl = "https://www.youtube.com/watch?v=" + videoId;

        String html = "<iframe width=\"100%\" height=\"100%\" src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe>";
        webView.loadData(html, "text/html", "utf-8");

        return view;
    }
}