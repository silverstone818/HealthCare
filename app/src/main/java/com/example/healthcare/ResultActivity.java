package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private int num;
    private ArrayList<Double> maxAngle;
    private ArrayList<Boolean> goodPose;
    private ArrayList<Integer> contract;
    private ArrayList<Boolean> Tension;

    private List<BarEntry> entries1 = new ArrayList<>();
    private List<BarEntry> entries2 = new ArrayList<>();
    private List<BarEntry> entries3 = new ArrayList<>();
    private List<BarEntry> entries4 = new ArrayList<>();
    private List<BarEntry> entries5 = new ArrayList<>();


    private BarChart barChart;
    private XAxis xAxis;
    private YAxis axisLeft, axisRight;
    private String[] label = {"이완", "수축", "긴장", "균형", "종합"};

    private static final String TAG1 = "AngleTest";

    private float normalizedNum;
    private double MaxAnglePercentage;
    private double goodPosePercentage, TensionPercentage, contractPercentage, result;
    private ArrayList<Double> scores = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        num = intent.getIntExtra("num", 0);
        maxAngle = (ArrayList<Double>) intent.getSerializableExtra("maxAngle");
        goodPose = (ArrayList<Boolean>) intent.getSerializableExtra("goodPose");
        Tension = (ArrayList<Boolean>) intent.getSerializableExtra("Tension");
        contract = (ArrayList<Integer>) intent.getSerializableExtra("contract");

        Log.d(TAG1, "result"+contract.toString());


        //스쿼트 개수 점수
        normalizedNum = (float) num / 12 * 20;

        //이완 점수
        double sum = 0;
        if(maxAngle != null){
            // maxAngle 값들에 대한 점수 계산
            for (Double angle : maxAngle) {
                double score;
                if (angle <= 60) {
                    score = 60 - angle;
                } else {
                    score = angle - 60;
                }
                scores.add(score);
            }

            // 점수 평균 계산
            for (double score : scores) {
                sum += score;
            }
            double average = sum / num;

            // 0일 수록 평균이 20, 90일 수록 평균이 0 (10%에서 0% 사이)
            MaxAnglePercentage = 20 - (average / 90 * 20);
        }else{
            MaxAnglePercentage = 0;
        }

        //균형 점수
        if(goodPose != null){
            int countTrue = 0;
            for (boolean isWaistBanding : goodPose) {
                if (isWaistBanding) {
                    countTrue++;
                }
            }
            double percentageTrue = (double) countTrue / num;

            // 0~10% 범위로 변환
            goodPosePercentage = percentageTrue * 20;
        }else{
            goodPosePercentage = 0;
        }

        //긴장 점수
        if(Tension != null){
            int countTrue = 0;
            for (boolean isWaistBanding : Tension) {
                if (isWaistBanding) {
                    countTrue++;
                }
            }
            double percentageTrue = (double) countTrue / num;

            // 0~10% 범위로 변환
            TensionPercentage = percentageTrue * 20;
        }else{
            TensionPercentage = 0;
        }

        //수축 점수
        if(contract != null){
            ArrayList<Double> percentages = new ArrayList<>();

            for (int value : contract) {
                double percentage;
                if (value >= 0 && value <= 5) {
                    percentage = 20;
                } else if (value > 30) {
                    percentage = 0;
                } else {
                    // 원하는 비율에 맞게 값 사이의 비율을 조정하십시오.
                    // 예: 선형 비례를 사용하여 6~30 사이의 값에 대해 계산하려면 다음을 사용하십시오.
                    percentage = 20 - (value - 5) * (20.0 / (30 - 5));
                }
                percentages.add(percentage);
            }
            sum = 0;
            for(double value : percentages){
                sum += value;
            }
            // contract 값을 0~10% 범위로 변환
            contractPercentage = sum / num;
            Log.d(TAG1, "percentage: " + contractPercentage);
        }else{
            contractPercentage = 0;
        }



        ArrayList<BarEntry> entry_chart = new ArrayList<>(); // 데이터를 담을 Arraylist

        barChart = (BarChart) findViewById(R.id.chart);

        barChart.getDescription().setEnabled(false);
        xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);

        axisLeft = barChart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0); // 최솟값
        axisLeft.setAxisMaximum(20); // 최댓값
        axisLeft.setGranularity(1f); // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false);

        axisRight = barChart.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false); // label 삭제
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);


        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return label[(int)value - 1];
            }
        });

        barChart.animateY(1000);
        barChart.animateX(1000);

        BarData barData = new BarData(); // 차트에 담길 데이터

        result = (MaxAnglePercentage + contractPercentage + TensionPercentage + goodPosePercentage + (normalizedNum * 4)) / 8;

        entry_chart.add(new BarEntry(1, (int)MaxAnglePercentage)); //entry_chart1에 좌표 데이터를 담는다.
        entry_chart.add(new BarEntry(2, (int)contractPercentage));
        entry_chart.add(new BarEntry(3, (int)TensionPercentage));
        entry_chart.add(new BarEntry(4, (int)goodPosePercentage));
        entry_chart.add(new BarEntry(5, (int)result));


        entries1.add(entry_chart.get(0));
        BarDataSet barDataSet1 = new BarDataSet(entries1, "이완");

        entries2.add(entry_chart.get(1));
        BarDataSet barDataSet2 = new BarDataSet(entries2, "수축");

        entries3.add(entry_chart.get(2));
        BarDataSet barDataSet3 = new BarDataSet(entries3, "긴장");

        entries4.add(entry_chart.get(3));
        BarDataSet barDataSet4 = new BarDataSet(entries4, "균형");

        entries5.add(entry_chart.get(4));
        BarDataSet barDataSet5 = new BarDataSet(entries5, "종합");

        // 새로운 IntegerValueFormatter 생성
        IntegerValueFormatter integerValueFormatter = new IntegerValueFormatter();

        // BarDataSet에 IntegerValueFormatter를 적용
        barDataSet1.setValueFormatter(integerValueFormatter);
        barDataSet2.setValueFormatter(integerValueFormatter);
        barDataSet3.setValueFormatter(integerValueFormatter);
        barDataSet4.setValueFormatter(integerValueFormatter);
        barDataSet5.setValueFormatter(integerValueFormatter);

        barDataSet1.setColor(Color.BLUE); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet1.setValueTextSize(15f);
        barData.addDataSet(barDataSet1); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        barDataSet2.setColor(Color.GREEN); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet2.setValueTextSize(15f);
        barData.addDataSet(barDataSet2); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        barDataSet3.setColor(Color.YELLOW); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet3.setValueTextSize(15f);
        barData.addDataSet(barDataSet3); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        barDataSet4.setColor(Color.rgb(255, 127, 39)); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet4.setValueTextSize(15f);
        barData.addDataSet(barDataSet4); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        barDataSet5.setColor(Color.RED); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet5.setValueTextSize(15f);
        barData.addDataSet(barDataSet5); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        barChart.setData(barData); // 차트에 위의 DataSet 을 넣는다.

        barChart.invalidate(); // 차트 업데이트
        barChart.setTouchEnabled(false); // 차트 터치 불가능하게

    }
}

class IntegerValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value);
    }
}