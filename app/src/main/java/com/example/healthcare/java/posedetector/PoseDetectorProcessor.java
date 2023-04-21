/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.healthcare.java.posedetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.healthcare.GraphicOverlay;
import com.example.healthcare.java.VisionProcessorBase;
import com.example.healthcare.java.posedetector.classification.PoseClassifierProcessor;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** A processor to run pose detector. */
public class PoseDetectorProcessor
        extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification> {
    private static final String TAG = "PoseDetectorProcessor";

    private final PoseDetector detector;

    private final boolean showInFrameLikelihood;
    private final boolean visualizeZ;
    private final boolean rescaleZForVisualization;
    private final boolean runClassification;
    private final boolean isStreamMode;
    private boolean isTtsInitialized = false;
    private final Context context;
    private final Executor classificationExecutor;

    private PoseClassifierProcessor poseClassifierProcessor;
    /** Internal class to hold Pose and classification results. */
    protected static class PoseWithClassification {
        private final Pose pose;
        private final List<String> classificationResult;

        public PoseWithClassification(Pose pose, List<String> classificationResult) {
            this.pose = pose;
            this.classificationResult = classificationResult;
        }

        public Pose getPose() {
            return pose;
        }

        public List<String> getClassificationResult() {
            return classificationResult;
        }
    }

    public PoseDetectorProcessor(
            Context context1,
            Context context2,
            PoseDetectorOptionsBase options,
            boolean showInFrameLikelihood,
            boolean visualizeZ,
            boolean rescaleZForVisualization,
            boolean runClassification,
            boolean isStreamMode) {
        super(context1);
        initializeTextToSpeech(context2);
        this.showInFrameLikelihood = showInFrameLikelihood;
        this.visualizeZ = visualizeZ;
        this.rescaleZForVisualization = rescaleZForVisualization;
        detector = PoseDetection.getClient(options);
        this.runClassification = runClassification;
        this.isStreamMode = isStreamMode;
        this.context = context1;
        classificationExecutor = Executors.newSingleThreadExecutor();
    }


    @Override
    public void stop() {
        super.stop();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        detector.close();
    }

    @Override
    protected Task<PoseWithClassification> detectInImage(InputImage image) {
        return detector
                .process(image)
                .continueWith(
                        classificationExecutor,
                        task -> {
                            Pose pose = task.getResult();
                            List<String> classificationResult = new ArrayList<>();
                            if (runClassification) {
                                if (poseClassifierProcessor == null) {
                                    poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                                }
                                classificationResult = poseClassifierProcessor.getPoseResult(pose);
                            }
                            return new PoseWithClassification(pose, classificationResult);
                        });
    }

    @Override
    protected Task<PoseWithClassification> detectInImage(MlImage image) {
        Task<PoseWithClassification> poseWithClassificationTask = detector
                .process(image)
                .continueWith(
                        classificationExecutor,
                        task -> {
                            Pose pose = task.getResult();
                            List<String> classificationResult = new ArrayList<>();
                            if (runClassification) {
                                if (poseClassifierProcessor == null) {
                                    poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                                }
                                classificationResult = poseClassifierProcessor.getPoseResult(pose);
                            }
                            return new PoseWithClassification(pose, classificationResult);
                        });
        return poseWithClassificationTask;
    }
    private int numAnglesInRange = 0;
    private boolean isSquat = false;
    private int numSquats = 0;
    private double maxAngle = 0;
    private double temp = 120.0;

    private double leftAngle = 0;
    private double rightAngle = 0;
    private double waistAngle = 0;
    private boolean waist_banding = false;

    private TextToSpeech tts;

    public int getNumSquats() {
        return numSquats;
    }

    public double getMaxAngle() {
        return maxAngle;
    }

    public boolean isWaist_banding() {
        return waist_banding;
    }

    private void initializeTextToSpeech(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // 언어를 선택합니다.
                    tts.setLanguage(Locale.KOREAN);
                    isTtsInitialized = true;
                }
            }
        });
    }

    @Override
    protected void onSuccess(
            @NonNull PoseWithClassification poseWithClassification,
            @NonNull GraphicOverlay graphicOverlay) {
        Pose pose = poseWithClassification.getPose();
        List<String> classificationResult = poseWithClassification.getClassificationResult();

        graphicOverlay.add(
                new PoseGraphic(
                        graphicOverlay,
                        poseWithClassification.pose,
                        showInFrameLikelihood,
                        visualizeZ,
                        rescaleZForVisualization,
                        poseWithClassification.classificationResult){

                    public void draw(Canvas canvas) {
                        super.draw(canvas);
                        if (pose != null) {
                            if(isTtsInitialized){
                                tts.speak("인식되었습니다. 정확한 판단을 위해 동작 1번에서 1초 대기 후 동작 2번을 시행해주세요.", TextToSpeech.QUEUE_FLUSH, null, null);
                                isTtsInitialized = false;
                            }

                            Paint whitePaint = new Paint();
                            whitePaint.setColor(Color.WHITE);
                            whitePaint.setStyle(Paint.Style.STROKE);
                            whitePaint.setStrokeWidth(4.0f);
                            whitePaint.setTextSize(50f);

                            onSqurtsAngle(pose);

                            canvas.drawText("Left angle: " + leftAngle, 20, 200, whitePaint);
                            canvas.drawText("Right angle: " + rightAngle, 20, 250, whitePaint);
                            canvas.drawText("Max angle: " + maxAngle, 20, 300, whitePaint);
                            canvas.drawText("Num squats: " + numSquats, 20, 350, whitePaint);
                            canvas.drawText("numAnglesInRange: " + numAnglesInRange, 20, 400, whitePaint);
                            canvas.drawText("Waist angle: " + waistAngle, 20, 450, whitePaint);
                            canvas.drawText("Waist banding: " + waist_banding, 20, 500, whitePaint);
                            canvas.drawText("Waist Angle: " + waistAngle, 20, 550, whitePaint);

                        }

                    }
                });
    }


    public void waistBending(Pose pose){
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

        if (leftHip != null && leftShoulder != null
                && rightHip != null && rightShoulder != null) {

            PointF hipCenter = new PointF((leftHip.x + rightHip.x) / 2, (leftHip.y + rightHip.y) / 2);
            PointF shoulderCenter = new PointF((leftShoulder.x + rightShoulder.x) / 2, (leftShoulder.y + rightShoulder.y) / 2);

// 3. 엉덩이 중심점, 골반 중심점 및 어깨 중심점 사이의 각도를 계산합니다.
            waistAngle = calculateAngle(hipCenter, shoulderCenter, new PointF(shoulderCenter.x, shoulderCenter.y - 1));
        }
    }

    public void onSqurtsAngle(Pose pose){
        PointF leftHip = null;
        PointF leftKnee = null;
        PointF leftAnkle = null;

        PointF rightHip = null;
        PointF rightKnee = null;
        PointF rightAnkle = null;


        if (pose.getPoseLandmark(PoseLandmark.LEFT_HIP) != null) {
            leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_KNEE) != null) {
            leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE) != null) {
            leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).getPosition();
        }

        if (pose.getPoseLandmark(PoseLandmark.RIGHT_HIP) != null) {
            rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE) != null) {
            rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition();
        }
        if (pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE) != null) {
            rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).getPosition();
        }
        leftAngle = calculateAngle(leftHip, leftKnee, leftAnkle);
        rightAngle = calculateAngle(rightHip, rightKnee, rightAnkle);

        double allAngle = Math.min(leftAngle, rightAngle);
        // 새로운 스쿼트마다 개수 초기화
        if (allAngle == 0) {
            isSquat = false;
            waist_banding = false;
        }

        if (leftHip != null && leftKnee != null && leftAnkle != null
                && rightHip != null && rightKnee != null && rightAnkle != null) {

            waistBending(pose);

            if (allAngle != 0 && allAngle <= 130) {
                numAnglesInRange++;
                if (numAnglesInRange >= 20 && !isSquat) {  // 스쿼트 체크
                    if(waistAngle >= 170 && waistAngle <= 225){
                        waist_banding = true;
                    }else{
                        waist_banding = false;
                    }
                    if((int)temp < (int)allAngle){
                        isSquat = true;
                    }else{
                        temp = allAngle;
                    }
                }
            }

            if (allAngle > 150) {
                if(isSquat){
                    numSquats++;
                    maxAngle = temp;
                    temp = 120;
                    if(waistAngle >= 170 && waistAngle <= 200){
                        waist_banding = true;
                    }else{
                        waist_banding = false;
                    }
                    if(waist_banding == false){
                        //허리가 굽었을 때
                        tts.speak("허리를 펴주세요.", TextToSpeech.QUEUE_FLUSH, null, null);

                    }
                    else if(maxAngle > 85){
                        //조금 구부렸을 때
                        tts.speak("조금 더 구부려주세요.", TextToSpeech.QUEUE_FLUSH, null, null);

                    }
                    else if(maxAngle < 56){
                        //너무 많이 구부렸을 때
                        tts.speak("너무 많이 구부리셨어요.", TextToSpeech.QUEUE_FLUSH, null, null);

                    }
                    else if(maxAngle >= 56 && maxAngle <= 85 && waist_banding == true){
                        //좋은 자세 일 때
                        tts.speak("좋은 자세 입니다.", TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                }
                isSquat = false;

                numAnglesInRange = 0;
            }
        }
    }


    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Pose detection failed!", e);
    }

    @Override
    protected boolean isMlImageEnabled(Context context) {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true;
    }

    private static double calculateAngle(PointF hip, PointF knee, PointF ankle) {
        if (hip == null || knee == null || ankle == null) {
            return 0;
        }
        double angle = Math.toDegrees(Math.atan2(hip.y - knee.y, hip.x - knee.x)
                - Math.atan2(ankle.y - knee.y, ankle.x - knee.x));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

}

