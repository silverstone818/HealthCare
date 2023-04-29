package com.example.healthcare.java.Health;

import android.speech.tts.TextToSpeech;

import com.google.mlkit.vision.pose.Pose;

public interface HealthKind {

    TextToSpeech getTts();
    int getNumAnglesInRange();

    void setNumAnglesInRange(int numAnglesInRange);

    int getNum();

    void setNum(int numSquats);

    double getMaxAngle();

    void setMaxAngle(double maxAngle);

    double getTemp();

    void setTemp(double temp);

    double getLeftAngle();

    void setLeftAngle(double leftAngle);

    double getRightAngle();

    void setRightAngle(double rightAngle);

    double getWaistAngle();

    void setWaistAngle(double waistAngle);

    int getContract();

    void setContract(int contract);

    int getMnumAnglesInRange();

    void setMnumAnglesInRange(int mnumAnglesInRange);

    boolean isState();

    void setState(boolean squat);

    boolean isWaist_banding();

    void setWaist_banding(boolean waist_banding);

    boolean isGoodPose();

    void setGoodPose(boolean goodPose);

    boolean isTension();

    void setTension(boolean tension);

    void setTts(TextToSpeech tts);

    void waistBending(Pose pose);

    void onHealthAngle(Pose pose);
}
