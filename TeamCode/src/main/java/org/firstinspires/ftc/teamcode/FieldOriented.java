package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

// --- FIELD CENTRIC IMPORTS ---
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

// --- AUTO-AIM IMPORTS START ---
import android.util.Size;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.ColorSensing.GreenPurpleProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
// --- AUTO-AIM IMPORTS END ---

/**
 * FIELD ORIENTED CONTROL
 * The robot moves relative to the field, not itself.
 * Pushing the stick forward always moves the robot away from the driver.
 */

@TeleOp(name="Field Oriented TeleOp", group="Linear Opmode")
public class FieldOriented extends LinearOpMode {

    // --- AUTO-AIM VARIABLES START ---
    private VisionPortal visionPortal;
    private GreenPurpleProcessor greenPurpleProcessor; // Your pipeline class

    // You MUST tune these values
    private static final int CAMERA_WIDTH = 640;
    private static final int CENTER_X = CAMERA_WIDTH / 2;
    private static final double PIXEL_TOLERANCE = 20;
    private static final double TURN_POWER = 0.3; // Power for auto-aiming
    // --- AUTO-AIM VARIABLES END ---

    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;

    // Declare the IMU
    private IMU imu;

    public final double leftRightServoSpeed = 0.01;
    public final double backRightMultiplier = 1.1;
    public boolean isStrafing = false;

    // Variable to track the motor's ON/OFF state
    boolean isOuttakeOn = false;
    boolean wasBumperPressed = false;

    // --- NEW OUTTAKE POWER VARIABLES ---
    public final double OUTTAKE_POWER_A = 0.25;
    public final double OUTTAKE_POWER_B = 0.7;
    public final double OUTTAKE_POWER_X = 0.35;
    public final double OUTTAKE_POWER_Y = 0.57;

    private double currentOuttakePower = 0.0;

    // Variables to track *previous* button states for edge detection
    boolean wasGamepad2A_Pressed = false;
    boolean wasGamepad2B_Pressed = false;
    boolean wasGamepad2X_Pressed = false;
    boolean wasGamepad2Y_Pressed = false;
    // --- END NEW OUTTAKE POWER VARIABLES ---

    @Override
    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);

        // --- IMU INITIALIZATION (Field Oriented) ---
        // Retrieve the IMU from the hardware map
//        imu = hardwareMap.get(IMU.class, "imu");
//        // Adjust the parameters below to match your Control Hub mounting position
//        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
//                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
//                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
//        imu.initialize(parameters);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();

        // --- AUTO-AIM INITIALIZATION START ---
        greenPurpleProcessor = new GreenPurpleProcessor();
        visionPortal = new VisionPortal.Builder()
                .setCamera(this.hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(CAMERA_WIDTH, 480))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(greenPurpleProcessor)
                .enableLiveView(true)
                .build();
        // --- AUTO-AIM INITIALIZATION END ---

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            double max;
            double i = 0.0;
            int hook = 0;

            // --- FIELD CENTRIC RESET ---
            // Press Options (Start) to reset heading if drift occurs
            if (gamepad1.options) {
                imu.resetYaw();
            }

            // --- AUTO-AIM LOGIC START ---
            if (gamepad1.a) {
                // --- 1. AUTO-AIM LOGIC (Unchanged) ---
                int currentTargetX = greenPurpleProcessor.getTargetX();

                if (currentTargetX == -1) {
                    HW.frontLeftMotor.setPower(0);
                    HW.frontRightMotor.setPower(0);
                    HW.backLeftMotor.setPower(0);
                    HW.backRightMotor.setPower(0);
                    telemetry.addData("Mode", "Auto-Aim: No Target");
                } else {
                    double error = currentTargetX - CENTER_X;
                    telemetry.addData("Mode", "Auto-Aim: Target Acquired!");
                    telemetry.addData("Target X", currentTargetX);
                    telemetry.addData("Error", error);

                    if (Math.abs(error) <= PIXEL_TOLERANCE) {
                        HW.frontLeftMotor.setPower(0);
                        HW.frontRightMotor.setPower(0);
                        HW.backLeftMotor.setPower(0);
                        HW.backRightMotor.setPower(0);
                    } else {
                        if (error > 0) {
                            HW.frontLeftMotor.setPower(TURN_POWER);
                            HW.frontRightMotor.setPower(-TURN_POWER);
                            HW.backLeftMotor.setPower(TURN_POWER);
                            HW.backRightMotor.setPower(-TURN_POWER);
                        } else {
                            HW.frontLeftMotor.setPower(-TURN_POWER);
                            HW.frontRightMotor.setPower(TURN_POWER);
                            HW.backLeftMotor.setPower(-TURN_POWER);
                            HW.backRightMotor.setPower(TURN_POWER);
                        }
                    }
                }

            } else {
                // --- 2. FIELD ORIENTED DRIVE LOGIC ---

                // Read original joystick inputs
                double y = -gamepad1.left_stick_y; // Axial (Forward is +)
                double x = -gamepad1.left_stick_x * 1.1; // Lateral (Counteract imperfect strafing)
                double rx = gamepad1.right_stick_x; // Yaw

                // Read robot heading from IMU
                double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

                // Rotate the movement direction counter to the bot's rotation
                double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
                double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

                // Assign rotated values to your variables so the rest of the math works
                double axial = rotY;
                double lateral = rotX;
                double yaw = rx;

                // Combine requests
                double leftFrontPower = axial + lateral + yaw;
                double rightFrontPower = axial - lateral - yaw;
                double leftBackPower = axial - lateral + yaw;
                double rightBackPower = axial + lateral - yaw;

                // Normalize
                max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
                max = Math.max(max, Math.abs(leftBackPower));
                max = Math.max(max, Math.abs(rightBackPower));
                i = gamepad1.right_trigger;

                if (max > 1) {
                    leftFrontPower /= max;
                    rightFrontPower /= max;
                    leftBackPower /= max;
                    rightBackPower /= max;
                }

                if (gamepad1.right_bumper) {
                    // Keep speed
                } else {
                    leftFrontPower /= 2;
                    rightFrontPower /= 2;
                    leftBackPower /= 2;
                    rightBackPower /= 2;
                }

                HW.frontLeftMotor.setPower(leftFrontPower);
                HW.frontRightMotor.setPower(rightFrontPower);
                HW.backLeftMotor.setPower(leftBackPower);
                HW.backRightMotor.setPower(rightBackPower);

                telemetry.addData("Mode", "Field Oriented Control");
                telemetry.addData("Heading", "%4.2f rad", botHeading);
            }
            // --- AUTO-AIM LOGIC END ---


            // --- Your other logic (intake, etc.) ---
            double intakeMotorPower;
            double outTakeMotor1Power;
            double outTakeMotor2Power;

            // Note: outTakeMotor3Power was unused in original logic block but defined
            double outTakeMotor3Power;

            double yaw2 = gamepad2.right_stick_x;

            if (gamepad2.left_bumper) {
                intakeMotorPower = 0.25;
            } else if (gamepad2.right_bumper) {
                intakeMotorPower = 0.5;
            }
            else if (gamepad2.right_trigger > 0) {
                intakeMotorPower = -0.4;
            } else if (gamepad2.left_trigger > 0) {
                intakeMotorPower = -0.2;
            }
            else {
                intakeMotorPower = 0;
            }

            // --- Outtake Logic with 4-Button Toggle ---
            boolean isGamepad2A_Pressed = gamepad2.a;
            boolean isGamepad2B_Pressed = gamepad2.b;
            boolean isGamepad2X_Pressed = gamepad2.x;
            boolean isGamepad2Y_Pressed = gamepad2.y;

            if (isGamepad2A_Pressed && !wasGamepad2A_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_A) currentOuttakePower = 0.0;
                else currentOuttakePower = OUTTAKE_POWER_A;
            }
            if (isGamepad2B_Pressed && !wasGamepad2B_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_B) currentOuttakePower = 0.0;
                else currentOuttakePower = OUTTAKE_POWER_B;
            }
            if (isGamepad2X_Pressed && !wasGamepad2X_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_X) currentOuttakePower = 0.0;
                else currentOuttakePower = OUTTAKE_POWER_X;
            }
            if (isGamepad2Y_Pressed && !wasGamepad2Y_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_Y) currentOuttakePower = 0.0;
                else currentOuttakePower = OUTTAKE_POWER_Y;
            }

            wasGamepad2A_Pressed = isGamepad2A_Pressed;
            wasGamepad2B_Pressed = isGamepad2B_Pressed;
            wasGamepad2X_Pressed = isGamepad2X_Pressed;
            wasGamepad2Y_Pressed = isGamepad2Y_Pressed;

            outTakeMotor1Power = currentOuttakePower;
            outTakeMotor2Power = currentOuttakePower;
            outTakeMotor3Power = currentOuttakePower;
            // --- End of New Outtake Logic ---

            yaw2 = yaw2 / 1.5;

            HW.intakeMotor.setPower(intakeMotorPower);
            HW.outTakeLeft.setPower(outTakeMotor1Power);
            HW.outTakeRight.setPower(outTakeMotor2Power);
            //HW.outTake3.setPower(outTakeMotor3Power);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

        visionPortal.close();
    }
}