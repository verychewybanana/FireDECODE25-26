package org.firstinspires.ftc.teamcode.ColorSensing; // Use your team's package name

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
    // !! MUST BE TUNED FOR YOUR SPECIFIC CAMERA AND LIGHTING !!
    // Green
    public static Scalar GREEN_LOWER = new Scalar(35, 100, 50);
    public static Scalar GREEN_UPPER = new Scalar(85, 255, 255);
    // Purple
    public static Scalar PURPLE_LOWER = new Scalar(125, 50, 50);
    public static Scalar PURPLE_UPPER = new Scalar(160, 255, 255);

    // --- Image Processing Mats ---
    private Mat hsvMat = new Mat();
    private Mat greenMask = new Mat();
    private Mat purpleMask = new Mat();
    private Mat combinedMask = new Mat();
    private Mat hierarchy = new Mat();

    // --- Results ---
    // We use 'volatile' to ensure OpModes see the latest value
    private volatile Rect bestRect = new Rect();
    private volatile int targetX = -1;
    private volatile double targetArea = 0;

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        // This is called when the pipeline is first initialized
        // We don't need to do anything here, but we must implement the method
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        // This is the main processing method, called for every camera frame

        // 1. Convert the frame from RGB to HSV
        Imgproc.cvtColor(frame, hsvMat, Imgproc.COLOR_RGB2HSV);

        // 2. Create binary masks for green and purple
        Core.inRange(hsvMat, GREEN_LOWER, GREEN_UPPER, greenMask);
//        Core.inRange(hsvMat, PURPLE_LOWER, PURPLE_UPPER, purpleMask);
//
//        // 3. Combine the two masks into one
//        Core.add(greenMask, purpleMask, combinedMask);

        // 4. (Optional) Clean up the mask with morphological operations
        // Erode shrinks the white areas, Dilation expands them.
        // This helps remove small noise speckles
        Imgproc.erode(combinedMask, combinedMask, new Mat(), new Point(-1, -1), 2);
        Imgproc.dilate(combinedMask, combinedMask, new Mat(), new Point(-1, -1), 2);

        // 5. Find contours (outlines of the white areas)
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(combinedMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 6. Find the largest contour
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

        // 7. Store the results
        if (objectFound) {
            bestRect = currentBestRect;
            targetArea = maxArea;
            targetX = bestRect.x + (bestRect.width / 2);
        } else {
            // No object found, reset our values
            bestRect = new Rect(); // Empty rectangle
            targetArea = 0;
            targetX = -1;
        }

        // Return the original frame (we'll draw on it in onDrawFrame)
        return greenMask;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        // This method is called after processFrame.
        // We use it to draw debugging information on the Driver Station stream.

        // Create a Paint object for drawing
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4 * scaleCanvasDensity); // Scale line width for screen density

        // Check if we found an object
        if (targetArea > 0) {
            // We must explicitly create an android.graphics.Rect for the canvas
            // We also cast the 'long' from Math.round() to an 'int'
            android.graphics.Rect scaledRect = new android.graphics.Rect(
                    (int) Math.round(bestRect.x * scaleBmpPxToCanvasPx),
                    (int) Math.round(bestRect.y * scaleBmpPxToCanvasPx),
                    (int) Math.round((bestRect.x + bestRect.width) * scaleBmpPxToCanvasPx),
                    (int) Math.round((bestRect.y + bestRect.height) * scaleBmpPxToCanvasPx)
            );

            // Draw the rectangle on the canvas
            canvas.drawRect(scaledRect, paint);
        }
    }

    // --- Public methods for OpModes to get results ---

    /**
     * Gets the x-coordinate of the center of the detected object.
     * @return X-coordinate (in pixels) or -1 if no object is found.
     */
    public int getTargetX() {
        return targetX;
    }

    /**
     * Gets the area of the detected object.
     * @return Area (in pixels) or 0 if no object is found.
     */
    public double getTargetArea() {
        return targetArea;
    }
}