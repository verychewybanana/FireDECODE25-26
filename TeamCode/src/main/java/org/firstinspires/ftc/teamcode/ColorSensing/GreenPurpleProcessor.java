package org.firstinspires.ftc.teamcode.ColorSensing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class GreenPurpleProcessor implements VisionProcessor {

    // --- HSV Color Ranges ---
    // Start with these "wide" values for Green, then tune them down.
    public static Scalar GREEN_LOWER = new Scalar(0, 0, 0);  // 0, 93, 83
    public static Scalar GREEN_UPPER = new Scalar(255, 255, 255);  // 105, 208, 194


    // Keep Purple separate for now
    public static Scalar PURPLE_LOWER = new Scalar(125, 50, 50);
    public static Scalar PURPLE_UPPER = new Scalar(160, 255, 255);

    // --- Image Processing Mats ---
    private Mat hsvMat = new Mat();
    private Mat greenMask = new Mat();
    private Mat purpleMask = new Mat();
    private Mat combinedMask = new Mat();
    private Mat hierarchy = new Mat();

    // --- Results ---
    private volatile Rect bestRect = new Rect();
    private volatile int targetX = -1;
    private volatile double targetArea = 0;

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        // 1. Convert the frame from RGB to HSV
        Imgproc.cvtColor(frame, hsvMat, Imgproc.COLOR_RGB2HSV);

        // 2. Create binary masks
        Core.inRange(hsvMat, GREEN_LOWER, GREEN_UPPER, greenMask);

        // --- TUNING ONE COLOR TRICK ---
        // Since we are tuning GREEN right now, we just copy green directly to combined.
        // When you want to add purple back, you will uncomment the PURPLE lines below.

        greenMask.copyTo(combinedMask);

        // Core.inRange(hsvMat, PURPLE_LOWER, PURPLE_UPPER, purpleMask);
        // Core.add(greenMask, purpleMask, combinedMask); // Use this when doing BOTH colors

        // 3. Clean up the mask
        Imgproc.erode(combinedMask, combinedMask, new Mat(), new Point(-1, -1), 2);
        Imgproc.dilate(combinedMask, combinedMask, new Mat(), new Point(-1, -1), 2);

        // 4. Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(combinedMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 5. Find the largest contour
        double maxArea = 0;
        Rect currentBestRect = new Rect();
        boolean objectFound = false;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                currentBestRect = Imgproc.boundingRect(contour);
                objectFound = true;
            }
        }

        // 6. Store the results
        if (objectFound) {
            bestRect = currentBestRect;
            targetArea = maxArea;
            targetX = bestRect.x + (bestRect.width / 2);
        } else {
            bestRect = new Rect();
            targetArea = 0;
            targetX = -1;
        }

        return frame;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4 * scaleCanvasDensity);

        if (targetArea > 0) {
            android.graphics.Rect scaledRect = new android.graphics.Rect(
                    (int) Math.round(bestRect.x * scaleBmpPxToCanvasPx),
                    (int) Math.round(bestRect.y * scaleBmpPxToCanvasPx),
                    (int) Math.round((bestRect.x + bestRect.width) * scaleBmpPxToCanvasPx),
                    (int) Math.round((bestRect.y + bestRect.height) * scaleBmpPxToCanvasPx)
            );
            canvas.drawRect(scaledRect, paint);
        }
    }

    public int getTargetX() {
        return targetX;
    }

    // Add this helper method to your processor class
    public double[] getCenterPixelColor() {
        // hsvMat is the Mat where we converted the frame to HSV
        if (hsvMat.empty()) return new double[]{0, 0, 0};

        // Get the center pixel
        int centerX = hsvMat.cols() / 2;
        int centerY = hsvMat.rows() / 2;

        // Get the pixel value (returns an array like [H, S, V])
        return hsvMat.get(centerY, centerX);
    }

    public double getTargetArea() {
        return targetArea;
    }
}
