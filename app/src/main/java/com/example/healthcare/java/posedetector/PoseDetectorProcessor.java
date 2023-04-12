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
            Context context,
            PoseDetectorOptionsBase options,
            boolean showInFrameLikelihood,
            boolean visualizeZ,
            boolean rescaleZForVisualization,
            boolean runClassification,
            boolean isStreamMode) {
        super(context);
        this.showInFrameLikelihood = showInFrameLikelihood;
        this.visualizeZ = visualizeZ;
        this.rescaleZForVisualization = rescaleZForVisualization;
        detector = PoseDetection.getClient(options);
        this.runClassification = runClassification;
        this.isStreamMode = isStreamMode;
        this.context = context;
        classificationExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void stop() {
        super.stop();
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
    private double temp = 0;

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


                            Paint whitePaint = new Paint();
                            whitePaint.setColor(Color.WHITE);
                            whitePaint.setStyle(Paint.Style.STROKE);
                            whitePaint.setStrokeWidth(4.0f);
                            whitePaint.setTextSize(50f);

                            double leftAngle = calculateAngle(leftHip, leftKnee, leftAnkle);
                            double rightAngle = calculateAngle(rightHip, rightKnee, rightAnkle);
                            double allAngle = Math.min(leftAngle, rightAngle);
                            if (leftHip != null && leftKnee != null && leftAnkle != null
                                    && rightHip != null && rightKnee != null && rightAnkle != null) {


                                // 새로운 스쿼트마다 개수 초기화
                                if (allAngle == 0) {
                                    isSquat = false;
                                    numSquats = 0;
                                    maxAngle = 0;
                                }

                                if (allAngle != 0 && allAngle <= 90) {
                                    numAnglesInRange++;
                                    if (numAnglesInRange >= 8 && !isSquat) {  // 스쿼트 체크
                                        isSquat = true;
                                        temp = Math.min(leftAngle, rightAngle);
                                    }
                                }

                                if (allAngle > 150) {
                                    if(isSquat) numSquats++;
                                    isSquat = false;
                                    maxAngle = temp;
                                    numAnglesInRange = 0;
                                }
                            }

                            canvas.drawText("Left angle: " + leftAngle, 20, 200, whitePaint);
                            canvas.drawText("Right angle: " + rightAngle, 20, 250, whitePaint);
                            canvas.drawText("Max angle: " + maxAngle, 20, 300, whitePaint);
                            canvas.drawText("Num squats: " + numSquats, 20, 350, whitePaint);
                            canvas.drawText("numAnglesInRange: " + numAnglesInRange, 20, 400, whitePaint);
                        }
                    }
                });
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

