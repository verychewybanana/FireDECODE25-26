package org.firstinspires.ftc.teamcode.ColorSensing; // Use your team's package name

import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name = "Vision Tuner")
public class VisionTuningOpMode extends LinearOpMode {

    private VisionPortal visionPortal;
    private GreenPurpleProcessor greenPurpleProcessor;

    @Override
    public void runOpMode() {
        greenPurpleProcessor = new GreenPurpleProcessor();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(320, 240))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(greenPurpleProcessor)
                .enableLiveView(true)
                .build();

        telemetry.addData("Status", "Tuning... Press (A) to save, (B) to stream.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Keep the OpMode running
            telemetry.addData("I am", "running.");

            // Get the color of the center pixel
            double[] pixel = greenPurpleProcessor.getCenterPixelColor();

            if (pixel != null) {
                telemetry.addData("-", "--- COLOR PICKER ---");
                telemetry.addData("POINT CAMERA AT", "YOUR OBJECT");
                telemetry.addData("H (Color)", "%.0f", pixel[0]);
                telemetry.addData("S (Sat)",   "%.0f", pixel[1]);
                telemetry.addData("V (Value)", "%.0f", pixel[2]);
                telemetry.addData("-", "--------------------");
            }
            telemetry.update();
        }
    }
}