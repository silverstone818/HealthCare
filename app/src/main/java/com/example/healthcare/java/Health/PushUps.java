package com.example.healthcare.java.Health;

import android.graphics.PointF;
import android.speech.tts.TextToSpeech;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PushUps implements HealthKind {
    private int numAnglesInRange = 0;
    private int numPushups;
    private int numPushUps = 0;
    private double maxAngle = 0;
    private double temp = 120.0;
    private double leftAngle = 0;
    private double rightAngle = 0;
    private double waistAngle = 0;
    private double contract;
    private double contractionTime = 0;
    private TextToSpeech tts;
    private int MnumAnglesInRange;
    private boolean isPushUp;
    private boolean isPushup = false;
    private boolean waist_banding = false;
    private boolean goodPose = false;
    private boolean Tension = false;
    private double timeAsDoubleTemp;

    public TextToSpeech getTts() {
        return tts;
    }

    public int getNumAnglesInRange() {
        return numAnglesInRange;
    }

    public void setNumAnglesInRange(int numAnglesInRange) {
        this.numAnglesInRange = numAnglesInRange;
    }

    public int getNum() {
        return numPushUps;
    }

    public void setNum(int numPushUps) {
        this.numPushUps = numPushUps;
    }

    public double getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getLeftAngle() {
        return leftAngle;
    }

    public void setLeftAngle(double leftAngle) {
        this.leftAngle = leftAngle;
    }

    public double getRightAngle() {
        return rightAngle;
    }

    public void setRightAngle(double rightAngle) {
        this.rightAngle = rightAngle;
    }

    public double getWaistAngle() {
        return waistAngle;
    }

    public void setWaistAngle(double waistAngle) {
        this.waistAngle = waistAngle;
    }

    public double getContract() {
        return contract;
    }

    public void setContract(double contract) {
        this.contract = contract;
    }

    public int getMnumAnglesInRange() {
        return MnumAnglesInRange;
    }

    public void setMnumAnglesInRange(int mnumAnglesInRange) {
        MnumAnglesInRange = mnumAnglesInRange;
    }

    public boolean isState() {
        return isPushUp;
    }

    public void setState(boolean pushUp) {
        isPushUp = pushUp;
    }

    public boolean isWaist_banding() {
        return waist_banding;
    }

    public void setWaist_banding(boolean waist_banding) {
        this.waist_banding = waist_banding;
    }

    public boolean isGoodPose() {
        return goodPose;
    }

    public void setGoodPose(boolean goodPose) {
        this.goodPose = goodPose;
    }

    public boolean isTension() {
        return Tension;
    }

    public void setTension(boolean tension) {
        Tension = tension;
    }

    public void setTts(TextToSpeech tts) {
        this.tts = tts;
    }

    public void waistBending(Pose pose) {
        PointF leftShoulder = null;
        PointF rightShoulder = null;
        PointF leftHip = null;
        PointF rightHip = null;

        // 2. 어깨 중심점과 엉덩이 중심점을 계산합니다.
        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) != null) {
            leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER) != null) {
            rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_HIP) != null) {
            leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_HIP) != null) {
            rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition();
        }

        if (leftHip != null && leftShoulder != null && rightHip != null && rightShoulder != null) {
            PointF hipCenter = new PointF((leftHip.x + rightHip.x) / 2, (leftHip.y + rightHip.y) / 2);
            PointF shoulderCenter = new PointF((leftShoulder.x + rightShoulder.x) / 2, (leftShoulder.y + rightShoulder.y) / 2);

            // 3. 엉덩이 중심점, 골반 중심점 및 어깨 중심점 사이의 각도를 계산합니다.
            waistAngle = calculateAngle(hipCenter, shoulderCenter, new PointF(shoulderCenter.x, shoulderCenter.y - 1));
        }
    }

    public void onHealthAngle(Pose pose) {
        PointF leftShoulder = null;
        PointF leftElbow = null;
        PointF leftWrist = null;

        PointF rightShoulder = null;
        PointF rightElbow = null;
        PointF rightWrist = null;

        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) != null) {
            leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW) != null) {
            leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_WRIST) != null) {
            leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
        }

        if (pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER) != null) {
            rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW) != null) {
            rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST) != null) {
            rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition();
        }

        leftAngle = calculateAngle(leftShoulder, leftElbow, leftWrist);
        rightAngle = calculateAngle(rightShoulder, rightElbow, rightWrist);

        double allAngle = Math.max(leftAngle, rightAngle);

        // 새로운 푸시업마다 상태 초기화
        if (allAngle == 0) {
            isPushup = false;
            waist_banding = false;
            numAnglesInRange = 0;
        }

        if (leftShoulder != null && leftElbow != null && leftWrist != null
                && rightShoulder != null && rightElbow != null && rightWrist != null) {

            // 4. 팔 굽혀 펴기 동작 감지를 위한 각도 계산
            if (allAngle != 0 && allAngle >= 160) {
                numAnglesInRange++;
                if (numAnglesInRange >= 12 && !isPushup) {  // 푸시업 체크
                    tts.speak("Down!", TextToSpeech.QUEUE_FLUSH, null, null);
                    isPushup = true;
                }
            }

            if (allAngle < 90) {
                if (isPushup) {
                    numPushups++;
                    isPushup = false;

                    // 푸시업 각도와 푸시업 시간 기록
                    if (maxAngle < allAngle) {
                        maxAngle = allAngle;
                    }
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dataFormat = new SimpleDateFormat("ss.SSS");
                    String getTime = dataFormat.format(date);
                    double timeAsDouble = Double.parseDouble(getTime);

                    if (timeAsDouble < timeAsDoubleTemp) {
                        contractionTime = (timeAsDouble + 60) - timeAsDoubleTemp;
                    } else {
                        contractionTime = timeAsDouble - timeAsDoubleTemp;
                    }

                    // 푸시업 자세 분석
                    if (maxAngle < 135) {
                        // 푸시업 자세가 너무 낮을 때
                        tts.speak("Raise your body higher.", TextToSpeech.QUEUE_FLUSH, null, null);
                        goodPose = false;
                    } else if (maxAngle > 155) {
                        // 푸시업 자세가 너무 높을 때
                        tts.speak("Lower your body more.", TextToSpeech.QUEUE_FLUSH, null, null);
                        goodPose = false;
                    } else if (maxAngle >= 135 && maxAngle <= 155) {
                        // 좋은 푸시업 자세일 때
                        tts.speak("Good push-up!", TextToSpeech.QUEUE_FLUSH, null, null);
                        goodPose = true;
                    }
                }
                numAnglesInRange = 0;
            }
        }
    }

    private static double calculateAngle(PointF point1, PointF point2, PointF point3) {
        if (point1 == null || point2 == null || point3 == null) {
            return 0;
        }

        double angle = Math.toDegrees(Math.atan2(point3.y - point2.y, point3.x - point2.x)
                - Math.atan2(point1.y - point2.y, point1.x - point2.x));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }
}



