package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import android.util.Size;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.ColorSensing.GreenPurpleProcessor;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name="Automated TeleOpp", group="Linear Opmode")
public class AutomatedTeleOp extends LinearOpMode {

    // --- VISION VARIABLES ---
    private VisionPortal visionPortal;
    private GreenPurpleProcessor greenPurpleProcessor;
    private static final int CAMERA_WIDTH = 640;
    private static final int CENTER_X = CAMERA_WIDTH / 2;

    // --- AUTO CONSTANTS ---
    private static final double TURN_P = 0.003; // Proportional control for smoother turning
    private static final double PIXEL_TOLERANCE = 10;

    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;

    // Outtake State
    private double currentOuttakePower = 0.0;
    // Button edge detection variables
    boolean wasGamepad2A = false;
    boolean wasGamepad2B = false;
    boolean wasGamepad2X = false;
    boolean wasGamepad2Y = false;

    // Preset Powers
    public final double POWER_OFF = 0.0;
    public final double POWER_LOW = 0.25;
    public final double POWER_MID = 0.35;
    public final double POWER_HIGH = 0.57;

    @Override
    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();

        // --- VISION INIT ---
        greenPurpleProcessor = new GreenPurpleProcessor();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(CAMERA_WIDTH, 480))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(greenPurpleProcessor)
                .enableLiveView(true)
                .build();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // ---------------------------------------------------------
            // 1. READ INPUTS & SENSORS
            // ---------------------------------------------------------

            // Driver 1 (Chassis)
            double axial = -gamepad1.left_stick_y;  // Forward/Back
            double lateral = -gamepad1.left_stick_x * 1.1; // Strafe
            double yawInput = gamepad1.right_stick_x; // Manual Turn
            boolean isAutoAiming = gamepad1.a; // Hold A to aim

            // Vision Data
            int targetX = greenPurpleProcessor.getTargetX();
            double targetArea = greenPurpleProcessor.getTargetArea();

            // ---------------------------------------------------------
            // 2. CALCULATE CHASSIS POWER (HYBRID MODE)
            // ---------------------------------------------------------

            double yawPower = 0;

            if (isAutoAiming && targetX != -1) {
                // --- AUTO AIM MODE ---
                // Only active if button held AND target seen

                // Calculate error (how far off center are we?)
                double error = targetX - CENTER_X;

                // Simple P-Controller for smoother turning
                // Instead of just 0.3 or -0.3, we turn proportional to the error
                yawPower = error * TURN_P;

                // Cap the power so it doesn't spin too crazy fast
                yawPower = Math.max(-0.4, Math.min(0.4, yawPower));

                // Deadband: If we are close enough, stop turning
                if (Math.abs(error) < PIXEL_TOLERANCE) {
                    yawPower = 0;
                }

                telemetry.addData("Mode", "AUTO-LOCK");
                telemetry.addData("Err", error);

                // --- AUTOMATIC DISTANCE & SHOOTER CALCULATION ---
                // This is where you would add your math later!
                // Example:
                // double calculatedPower = calculateShooterPower(targetArea);
                // currentOuttakePower = calculatedPower;

            } else {
                // --- MANUAL MODE ---
                // Standard driver control
                yawPower = yawInput;
                telemetry.addData("Mode", "MANUAL");
            }

            // Mix the powers (Mecanum Math)
            // Notice we use 'yawPower' which is either from stick OR from auto-aim
            double leftFront  = axial + lateral + yawPower;
            double rightFront = axial - lateral - yawPower;
            double leftBack   = axial - lateral + yawPower;
            double rightBack  = axial + lateral - yawPower;

            // Normalize speeds
            double max = Math.max(Math.abs(leftFront), Math.abs(rightFront));
            max = Math.max(max, Math.abs(leftBack));
            max = Math.max(max, Math.abs(rightBack));

            // Slow Mode (Right Bumper)
            double speedMultiplier = gamepad1.right_bumper ? 0.5 : 1.0;

            if (max > 1.0) {
                leftFront /= max;
                rightFront /= max;
                leftBack /= max;
                rightBack /= max;
            }

            // Apply powers
            HW.frontLeftMotor.setPower(leftFront * speedMultiplier);
            HW.frontRightMotor.setPower(rightFront * speedMultiplier);
            HW.backLeftMotor.setPower(leftBack * speedMultiplier);
            HW.backRightMotor.setPower(rightBack * speedMultiplier);


            // ---------------------------------------------------------
            // 3. ATTACHMENTS (INTAKE / OUTTAKE)
            // ---------------------------------------------------------

            // Intake
            if (gamepad2.left_bumper) HW.intakeMotor.setPower(0.25);
            else if (gamepad2.right_bumper) HW.intakeMotor.setPower(0.5);
            else if (gamepad2.right_trigger > 0) HW.intakeMotor.setPower(-0.4);
            else if (gamepad2.left_trigger > 0) HW.intakeMotor.setPower(-0.2);
            else HW.intakeMotor.setPower(0);

            // Outtake (Toggle Logic)
            if (gamepad2.a && !wasGamepad2A) toggleOuttake(POWER_LOW);
            if (gamepad2.b && !wasGamepad2B) toggleOuttake(POWER_MID); // changed to match your logic
            if (gamepad2.x && !wasGamepad2X) toggleOuttake(POWER_MID);
            if (gamepad2.y && !wasGamepad2Y) toggleOuttake(POWER_HIGH);

            // Update previous button states
            wasGamepad2A = gamepad2.a;
            wasGamepad2B = gamepad2.b;
            wasGamepad2X = gamepad2.x;
            wasGamepad2Y = gamepad2.y;


            telemetry.addData("Target Area", "%.0f", targetArea);
            telemetry.addData("Shooter Power", "%.2f", currentOuttakePower);
            telemetry.update();
        }

        visionPortal.close();
    }

    // Helper function to keep the switch logic clean
    private void toggleOuttake(double targetPower) {
        if (currentOuttakePower == targetPower) {
            currentOuttakePower = POWER_OFF;
        } else {
            currentOuttakePower = targetPower;
        }
    }
}