package com.example.healthcare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private ArrayList<Boolean> goodPose, waist_banding;
    private ArrayList<Integer> contract;
    private ArrayList<Boolean> Tension;
    private String fb = "";
    private int Health;
    private BarChart barChart, barChart2;
    private String[] label = {"이완", "수축", "긴장", "균형", "종합"}, APPS = new String[5];
    private TextView feedback;
    private TextView feedbackBalance;
    private TextView feedbackTension;
    private TextView feedbackContract;
    private TextView feedbackMaxAngle;

    private Button btn_result;
    private Button btn_share;

    private static final String TAG1 = "AngleTest";
    private int big = 0, small = 0, waist = 0, tension = 0, good = 0;

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

        Health = intent.getIntExtra("Health", 0);
        num = intent.getIntExtra("num", 0);
        maxAngle = (ArrayList<Double>) intent.getSerializableExtra("maxAngle");
        goodPose = (ArrayList<Boolean>) intent.getSerializableExtra("goodPose");
        waist_banding = (ArrayList<Boolean>) intent.getSerializableExtra("waist_banding");
        Tension = (ArrayList<Boolean>) intent.getSerializableExtra("Tension");
        contract = (ArrayList<Integer>) intent.getSerializableExtra("contract");

        feedback = (TextView) findViewById(R.id.feedback);
        feedbackBalance = (TextView) findViewById(R.id.feedbackBalance);
        feedbackTension = (TextView) findViewById(R.id.feedbackTension);
        feedbackContract = (TextView) findViewById(R.id.feedbackContract);
        feedbackMaxAngle = (TextView) findViewById(R.id.feedbackMaxAngle);

        btn_result = (Button) findViewById(R.id.btn_result);
        btn_share = (Button) findViewById(R.id.btn_share);

        Log.d(TAG1, "result"+contract.toString());


        //스쿼트 개수 점수
        normalizedNum = (float) num / 12 * 20;

        switch (Health){
            case 1:
                SqurtsScore();
                Squrts();
                break;

            default:
                break;
        }

        Graph1();
        Graph2();


        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public void Graph1(){
        ArrayList<BarEntry> entry_chart = new ArrayList<>(); // 데이터를 담을 Arraylist
        List<BarEntry> entries = new ArrayList<>();

        barChart = (BarChart) findViewById(R.id.chart);

        barChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart.setTouchEnabled(false); // 터치 유무
        barChart.getLegend().setEnabled(false); // Legend는 차트의 범례
        barChart.setExtraOffsets(10f, 0f, 40f, 0f);

        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X 축 데이터 표시 위치

        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0); // 최솟값
        axisLeft.setAxisMaximum(20); // 최댓값
        axisLeft.setGranularity(1f); // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false); // label 삭제

        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        YAxis axisRight = barChart.getAxisRight();
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

        result = (MaxAnglePercentage + contractPercentage + TensionPercentage + goodPosePercentage + normalizedNum) / 8;

        entry_chart.add(new BarEntry(1, (int)MaxAnglePercentage)); //entry_chart1에 좌표 데이터를 담는다.
        entry_chart.add(new BarEntry(2, (int)contractPercentage));
        entry_chart.add(new BarEntry(3, (int)TensionPercentage));
        entry_chart.add(new BarEntry(4, (int)goodPosePercentage));
        entry_chart.add(new BarEntry(5, (int)result));

        entries.add(entry_chart.get(0));
        entries.add(entry_chart.get(1));
        entries.add(entry_chart.get(2));
        entries.add(entry_chart.get(3));
        entries.add(entry_chart.get(4));

        BarDataSet barDataSet = new BarDataSet(entries, label.toString());


        // 새로운 IntegerValueFormatter 생성
        IntegerValueFormatter integerValueFormatter = new IntegerValueFormatter();

        // BarDataSet에 IntegerValueFormatter를 적용
        barDataSet.setValueFormatter(integerValueFormatter);

        barDataSet.setDrawIcons(false);
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor("#66767676")); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet.setValueTextSize(15f);
        barData.addDataSet(barDataSet); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
        barData.setBarWidth(0.5f);

        barChart.setData(barData); // 차트에 위의 DataSet 을 넣는다.
        barChart.invalidate(); // 차트 업데이트
        barChart.setTouchEnabled(false); // 차트 터치 불가능하게
    }

    public void Graph2(){
        ArrayList<BarEntry> entry_chart = new ArrayList<>(); // 데이터를 담을 Arraylist
        List<BarEntry> entries = new ArrayList<>();

        barChart2 = (BarChart) findViewById(R.id.chart2);

        barChart2.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart2.setTouchEnabled(false); // 터치 유무
        barChart2.getLegend().setEnabled(false); // Legend는 차트의 범례
        barChart2.setExtraOffsets(10f, 0f, 40f, 0f);

        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        XAxis xAxis = barChart2.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X 축 데이터 표시 위치

        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
        YAxis axisLeft = barChart2.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0); // 최솟값
        axisLeft.setAxisMaximum(12); // 최댓값
        axisLeft.setGranularity(1f); // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false); // label 삭제

        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        YAxis axisRight = barChart2.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false); // label 삭제
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);


        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return APPS[(int)value - 1];
            }
        });

        barChart2.animateY(1000);
        barChart2.animateX(1000);

        BarData barData = new BarData(); // 차트에 담길 데이터

        entry_chart.add(new BarEntry(1, (int)big)); //entry_chart1에 좌표 데이터를 담는다.
        entry_chart.add(new BarEntry(2, (int)small));
        entry_chart.add(new BarEntry(3, (int)waist));
        entry_chart.add(new BarEntry(4, (int)tension));
        entry_chart.add(new BarEntry(5, (int)good));

        entries.add(entry_chart.get(0));
        entries.add(entry_chart.get(1));
        entries.add(entry_chart.get(2));
        entries.add(entry_chart.get(3));
        entries.add(entry_chart.get(4));

        BarDataSet barDataSet = new BarDataSet(entries, APPS.toString());
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String.valueOf((int) value)) + "회";
            }
        });

        // 새로운 IntegerValueFormatter 생성
        IntegerValueFormatter integerValueFormatter = new IntegerValueFormatter();

        // BarDataSet에 IntegerValueFormatter를 적용
        barDataSet.setValueFormatter(integerValueFormatter);

        barDataSet.setDrawIcons(false);
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor("#66767676")); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet.setValueTextSize(15f);
        barData.addDataSet(barDataSet); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
        barData.setBarWidth(0.5f);

        barChart2.setData(barData); // 차트에 위의 DataSet 을 넣는다.
        barChart2.invalidate(); // 차트 업데이트
        barChart2.setTouchEnabled(false); // 차트 터치 불가능하게
    }

    public void SqurtsScore(){
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

        if(MaxAnglePercentage <= 5){
            feedbackMaxAngle.setText("가동범위가 너무 대체적으로 적습니다. 보통 가동범위가 적으신 분들은 근육의 유연성이 적어서 그렇습니다.\n운동이 끝난 후 스트레칭을 해주며 근육을 늘려주세요. 가동범위가 점차 늘어날 것입니다.");
        }
        else if(MaxAnglePercentage <= 10){
            feedbackMaxAngle.setText("가동범위가 적습니다. 한동작 한동작 천천히 가동범위를 늘려보면서 각각의 횟수마다 퀄리티를 높혀주세요. 좋은 효과가 나올 것입니다.");
        }
        else if(MaxAnglePercentage <= 15){
            feedbackMaxAngle.setText("대체적으로 안정적입니다.");
        }
        else if(MaxAnglePercentage <= 20){
            feedbackMaxAngle.setText("훌륭합니다.");
        }

        if(contractPercentage <= 5){
            feedbackContract.setText("근력 부족이 느껴집니다. 일어날 때 힘을 폭발시키듯이 빠르게 올라와주세요.");
        }
        else if(contractPercentage <= 10){
            feedbackContract.setText("중량 이시면 훌륭합니다. 맨몸일 경우 조금 더 일어날 때 힘을 넣어주세요.");
        }
        else if(contractPercentage <= 15){
            feedbackContract.setText("대체적으로 안정적입니다.");
        }
        else if(contractPercentage <= 20){
            feedbackContract.setText("훌륭합니다!");
        }

        if(TensionPercentage <= 5){
            feedbackTension.setText("긴장이 너무 많이 풀립니다. 부상 주의하세요.");
        }
        else if(TensionPercentage <= 10){
            feedbackTension.setText("중간중간 쉬어가는 것이 느껴집니다.");
        }
        else if(TensionPercentage <= 15){
            feedbackTension.setText("대체적으로 안정적입니다.");
        }
        else if(TensionPercentage <= 20){
            feedbackTension.setText("훌륭합니다!");
        }

        if(goodPosePercentage <= 5){
            feedbackBalance.setText("심각합니다. 기초 체력 부족일 가능성이 있으니 런닝과, 플랭크 등을 통하여 코어와 체력을 키우세요!");
        }
        else if(goodPosePercentage <= 10){
            feedbackBalance.setText("불안정할 때가 많습니다. 밑에 분석결과를 참조해주세요.");
        }
        else if(goodPosePercentage <= 15){
            feedbackBalance.setText("대체적으로 안정적입니다.");
        }
        else if(goodPosePercentage <= 20){
            feedbackBalance.setText("훌륭합니다!");
        }

    }

    public void Squrts(){
        APPS[0] = "과한 동작";
        APPS[1] = "작은 동작";
        APPS[2] = "허리 말림";
        APPS[3] = "긴장 풀림";
        APPS[4] = "좋은 자세";

        for(int i = 0; i < num; i++){
            if(maxAngle.get(i) < 56){
                big++;
            }
            else if (maxAngle.get(i) > 85){
                small++;
            }
            if (waist_banding.get(i) == false){
                waist++;
            }
            if (Tension.get(i) == false){
                tension++;
            }
            if(goodPose.get(i) == true){
                good++;
            }
        }

        if(waist >= good / 2){
            fb += "\n허리가 휘거나 굽습니다. 또는 상체가 자주 앞으로 숙여집니다.\n이것은 체중의 무게가 앞쪽으로 실려있어 그렇습니다. 스쿼트를 할 때 항상 무게 중심을 뒷꿈치에 잡습니다.\n" +
                    "간단한 팁을 알려드리자면 엉덩이와 복부에 힘을 준 상태에서 천천히 내려가세요.\n내려간 이후 엉덩이를 앞으로 내미는 동시에 일어납니다.\n그렇게 된다면 수월하게 동작이 이루어 지고 허리가 덜 굽게 될 것입니다.\n";
        }
        if(big + small >= num / 2){
            fb += "\n대부분의 자세에서 가동범위가 크거나 작습니다.\n가동범위가 클 경우 무릎에 가해지는 부하가 심해집니다.\n작을 경우에는 스트렝스가 적어 허벅지에 자극이 적을 것입니다.\n" +
                    "영상촬영을 통해 가동범위가 어떤지 확인하며 모범자세와의 차이점을 확인하세요.\n몸 전체가 아닌 어느 한 부분이 잘려서 찍힌거나 촬영 각도가 문제 있을 시 다시 측정해주십시오.\n";
        }
        if(tension >= num / 2){
            fb += "\n일어났을 때 다리를 완전히 펴버리면 근육의 긴장이 풀리게 됩니다.\n긴장이 풀릴 시 다음 동작 수행에서 부상 위험과 관절 통증, 근육 스트렝스 저하 등 문제점이 생깁니다.\n일어날 때 무릎은 살짝 굽힌 상태를 유지한다는 느낌으로 일어나 주세요.\n";
        }
        if(good > waist + big + small){
            fb += "\n대체적으로 자세는 괜찮습니다. 다만 완벽하지는 않기에 데이터를 토대로 모범자세를 의식하며 계속 정진하세요!!\n";
        }
        if(num < 12){
            fb += "\n전체 횟수가 아직 12개 이상이 되지 않는 다는 것은 기초 근력이 부족하다는 것입니다.\n꾸준히 정진하여 12개를 채우는 것을 목표로 잡습니다.\n만약 중량이시면 횟수에 근접해질 시 중량을 5kg 단위로 늘려주세요.\n";
        }
        if(good == 12){
            fb += "\n자세가 완벽합니다! 이대로 꾸준히 정진해주세요!! 다음 운동도 파이팅!\n";
        }

        feedback.setText(fb);
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    @Override
    public void onBackPressed() {}

}

class IntegerValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value);
    }
}