package com.example.healthcare.java.Health;

import android.graphics.PointF;
import android.speech.tts.TextToSpeech;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PushUps implements HealthKind{
    private int numAnglesInRange = 0;
    private int numPushUp = 0;
    private double maxAngle = 0;
    private double temp = 120.0;
    private double leftAngle = 0;
    private double rightAngle = 0;
    private double waistAngle = 0;
    private double contract;
    private TextToSpeech tts;
    private int MnumAnglesInRange;
    private boolean isPushUp = false;
    private boolean waist_banding = false;
    private boolean goodPose = false;
    private boolean Tension = false;
    private double timeAsDoubleTemp;

    public TextToSpeech getTts() { return tts; }

    public int getNumAnglesInRange() { return numAnglesInRange; }

    public void setNumAnglesInRange(int numAnglesInRange) { this.numAnglesInRange = numAnglesInRange; }

    public int getNum() {
        return numPushUp;
    }

    public void setNum(int numPushUp) {
        this.numPushUp = numPushUp;
    }

    public double getMaxAngle() { return maxAngle; }

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

    public void setMnumAnglesInRange(int mnumAnglesInRange) { MnumAnglesInRange = mnumAnglesInRange; }

    public boolean isState() { return isPushUp; }

    public void setState(boolean pushUp) { isPushUp = pushUp; }

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

    public void waistBending(Pose pose){
        PointF leftshoulder = null;
        PointF rightshoulder = null;
        PointF leftHip = null;
        PointF rightHip = null;

        // 2. 어깨 중심점과 엉덩이 중심점을 계산합니다.
        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) != null) {
            leftshoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER) != null) {
            rightshoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_HIP) != null) {
            leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_HIP) != null) {
            rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition();
        }

        if (leftHip != null && leftshoulder != null
                && rightHip != null && rightshoulder != null) {

            PointF hipCenter = new PointF((leftHip.x + rightHip.x) / 2, (leftHip.y + rightHip.y) / 2);
            PointF shoulderCenter = new PointF((leftshoulder.x + rightshoulder.x) / 2, (leftshoulder.y + rightshoulder.y) / 2);

            // 3. 엉덩이 중심점, 골반 중심점 및 어깨 중심점 사이의 각도를 계산합니다.
            waistAngle = calculateAngle(hipCenter, shoulderCenter, new PointF(shoulderCenter.x, shoulderCenter.y - 1));
        }
    }

    public void onHealthAngle(Pose pose) {
        PointF leftshoulder = null;
        PointF leftElbow = null;
        PointF leftWrist = null;

        PointF rightshoulder = null;
        PointF rightElbow = null;
        PointF rightWrist = null;

        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) != null) {
            leftshoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW) != null) {
            leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_WRIST) != null) {
            leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER) != null) {
            rightshoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW) != null) {
            rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST) != null) {
            rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition();
        }

        double leftAngle = calculateAngle(leftshoulder, leftElbow, leftWrist);
        double rightAngle = calculateAngle(rightshoulder, rightElbow, rightWrist);

        boolean sm = false;
        double allAngle = Math.min(leftAngle, rightAngle);

        // 새로운 스쿼트마다 상태 초기화
        if (allAngle == 0) {
            isPushUp = false;
            waist_banding = false;
            numAnglesInRange = 0;
        }

        if (leftshoulder != null && leftElbow != null && leftWrist != null
                && rightshoulder != null && rightElbow != null && rightWrist != null) {

            waistBending(pose);

            if (allAngle != 0 && allAngle <= 130) {
                numAnglesInRange++;
                if (numAnglesInRange >= 24 && !isPushUp) {  // 푸쉬업 체크
                    tts.speak("Down!!", TextToSpeech.QUEUE_FLUSH, null, null);
                    if(waistAngle >= 170 && waistAngle <= 225){
                        waist_banding = true;
                    }else{
                        waist_banding = false;
                    }
                    if((int)temp < (int)allAngle){
                        isPushUp = true;
                    }else{
                        temp = allAngle;
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat dataFormat = new SimpleDateFormat("ss.SSS");
                        String getTime = dataFormat.format(date);
                        timeAsDoubleTemp = Double.parseDouble(getTime);
                    }
                }
            }

            if (allAngle > 150) {
                if(isPushUp){
                    numPushUp++;
                    maxAngle = temp;
                    temp = 120;

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dataFormat = new SimpleDateFormat("ss.SSS");
                    String getTime = dataFormat.format(date);
                    double timeAsDouble = Double.parseDouble(getTime);

                    if(timeAsDouble < timeAsDoubleTemp){
                        contract = (timeAsDouble + 60) - timeAsDoubleTemp;
                    }
                    else{
                        contract = timeAsDouble - timeAsDoubleTemp;
                    }

                    if(waistAngle >= 170 && waistAngle <= 200){
                        waist_banding = true;
                    }else{
                        waist_banding = false;
                    }
                    if(waist_banding == false){
                        //허리가 굽었을 때
                        tts.speak("허리를 펴주세요.", TextToSpeech.QUEUE_FLUSH, null, null);
                        goodPose = false;

                    }
                    else if(maxAngle > 85){
                        //조금 구부렸을 때
                        tts.speak("조금 더 구부려주세요.", TextToSpeech.QUEUE_FLUSH, null, null);
                        goodPose = false;

                    }
                    else if(maxAngle < 56){
                        //너무 많이 구부렸을 때
                        tts.speak("너무 많이 구부리셨어요.", TextToSpeech.QUEUE_FLUSH, null, null);
                        goodPose = false;
                    }
                    else if(maxAngle >= 56 && maxAngle <= 85 && waist_banding == true){
                        //좋은 자세 일 때
                        tts.speak("좋은 자세 입니다.", TextToSpeech.QUEUE_FLUSH, null, null);
                        goodPose = true;
                    }

                }
                isPushUp = false;
                sm = false;
                numAnglesInRange = 0;
            }

            if (allAngle <= 160){
                Tension = true;
            }else{
                Tension = false;
            }
        }
    }

    private static double calculateAngle(PointF shoulder, PointF elbow, PointF wrist) {
        if (shoulder == null || elbow == null || wrist == null) {
            return 0;
        }
        double angle = Math.toDegrees(Math.atan2(shoulder.y - elbow.y, shoulder.x - elbow.x)
                - Math.atan2(wrist.y - elbow.y, wrist.x - elbow.x));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }
}
