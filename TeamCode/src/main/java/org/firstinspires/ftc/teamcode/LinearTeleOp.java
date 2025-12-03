package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

// --- AUTO-AIM IMPORTS START ---
import android.util.Size;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.ColorSensing.GreenPurpleProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
// --- AUTO-AIM IMPORTS END ---

/**
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 **/


@TeleOp(name="Robot Oriented TeleOpp", group="Linear Opmode")
public class LinearTeleOp extends LinearOpMode {

    // --- AUTO-AIM VARIABLES START ---
    private VisionPortal visionPortal;
    private GreenPurpleProcessor greenPurpleProcessor; // Your pipeline class

    // You MUST tune these values
    private static final int CAMERA_WIDTH = 640; // I changed this back to 640 (standard), 160 is very small
    private static final int CENTER_X = CAMERA_WIDTH / 2;
    private static final double PIXEL_TOLERANCE = 20;
    private static final double TURN_POWER = 0.3; // Power for auto-aiming
    // --- AUTO-AIM VARIABLES END ---


    /*
    Controls for gamepad2
    Dpad down - lower slides
    Dpad up - raise slides to high level
    Dpad right - raise slides to mid level
    Y - open door
    B - close door
    A - tilt box to scoring position
    X - return box to 0
    Left bumper - toggle separator
    Right bumper - toggle hook servo up or down
    Left joystick y - spin intake wheels
    Right joystick x - actuator motor
*/


    //ServoImplEx servo;
    //PwmControl.PwmRange range = new PwmControl.PwmRange(533,2425);
    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;

    public final double leftRightServoSpeed = 0.01;
    public final double backRightMultiplier = 1.1;
    public boolean isStrafing = false;

    // Variable to track the motor's ON/OFF state
    boolean isOuttakeOn = false;

    // Variable to track the *previous* state of the button for edge detection
    // Variable to track the *previous* state of the button for edge detection
    boolean wasBumperPressed = false;

    // --- NEW OUTTAKE POWER VARIABLES ---
    // Feel free to tune these power levels
    public final double OUTTAKE_POWER_A = 0.25; // Low power
    public final double OUTTAKE_POWER_B = 0.7; // Medium-low power
    public final double OUTTAKE_POWER_X = 0.35; // Medium-high power
    public final double OUTTAKE_POWER_Y = 0.57;  // Full power (from your original code)

    private double currentOuttakePower = 0.0; // Tracks the current power state

    // Variables to track *previous* button states for edge detection
    boolean wasGamepad2A_Pressed = false;
    boolean wasGamepad2B_Pressed = false;
    boolean wasGamepad2X_Pressed = false;
    boolean wasGamepad2Y_Pressed = false;
    // --- END NEW OUTTAKE POWER VARIABLES ---

    @Override

    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();

        // --- AUTO-AIM INITIALIZATION START ---
        greenPurpleProcessor = new GreenPurpleProcessor();
        visionPortal = new VisionPortal.Builder()
                .setCamera(this.hardwareMap.get(WebcamName.class, "Webcam 1")) // "Webcam 1" must match your config
                .setCameraResolution(new Size(CAMERA_WIDTH, 480)) // Standard resolution
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG) // Added MJPEG for speed!
                .addProcessor(greenPurpleProcessor)
                .enableLiveView(true) // Show stream on Driver Station
                .build();
        // --- AUTO-AIM INITIALIZATION END ---


        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.addData("Last updated: ", "");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;
            double i = 0.0;
            int hook = 0;

            // --- AUTO-AIM LOGIC START ---
            // Check if the auto-aim button (e.g., 'A' on gamepad1) is being HELD down
            if (gamepad1.a) {
                // --- 1. AUTO-AIM LOGIC ---
                int currentTargetX = greenPurpleProcessor.getTargetX();

                if (currentTargetX == -1) {
                    // No object found. Stop the robot.
                    HW.frontLeftMotor.setPower(0);
                    HW.frontRightMotor.setPower(0);
                    HW.backLeftMotor.setPower(0);
                    HW.backRightMotor.setPower(0);
                    telemetry.addData("Mode", "Auto-Aim: No Target");

                } else {
                    // Object is found. Calculate the error.
                    double error = currentTargetX - CENTER_X;

                    telemetry.addData("Mode", "Auto-Aim: Target Acquired!");
                    telemetry.addData("Target X", currentTargetX);
                    telemetry.addData("Error", error);

                    // Check if we are already centered
                    if (Math.abs(error) <= PIXEL_TOLERANCE) {
                        // We are centered. Stop spinning.
                        HW.frontLeftMotor.setPower(0);
                        HW.frontRightMotor.setPower(0);
                        HW.backLeftMotor.setPower(0);
                        HW.backRightMotor.setPower(0);

                    } else {
                        // We need to spin.
                        if (error > 0) {
                            // Spin Right (positive yaw)
                            telemetry.addData("Action", "Spinning Right");
                            HW.frontLeftMotor.setPower(TURN_POWER);
                            HW.frontRightMotor.setPower(-TURN_POWER);
                            HW.backLeftMotor.setPower(TURN_POWER);
                            HW.backRightMotor.setPower(-TURN_POWER);
                        } else {
                            // Spin Left (negative yaw)
                            telemetry.addData("Action", "Spinning Left");
                            HW.frontLeftMotor.setPower(-TURN_POWER);
                            HW.frontRightMotor.setPower(TURN_POWER);
                            HW.backLeftMotor.setPower(-TURN_POWER);
                            HW.backRightMotor.setPower(TURN_POWER);
                        }
                    }
                }

            } else {
                // --- 2. MANUAL DRIVE LOGIC ---
                // This runs when 'A' is NOT pressed

                // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
                // We flip the y-axis value to invert front/back controls
                double axial = -gamepad1.left_stick_y;

                // Pushing stick right (positive) now maps to chassis-left power (negative)
                double lateral = -gamepad1.left_stick_x * 1.1;
                // Yaw (rotation) remains the same
                double yaw = gamepad1.right_stick_x;

                double axial2 = -gamepad2.left_stick_y;
                double yaw2 = gamepad2.right_stick_x;

                // Combine the joystick requests for each axis-motion to determine each wheel's power.
                double leftFrontPower = axial + lateral + yaw;
                double rightFrontPower = axial - lateral - yaw;
                double leftBackPower = axial - lateral + yaw;
                double rightBackPower = axial + lateral - yaw;

                // Normalize the values so no wheel power exceeds 100%
                max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
                max = Math.max(max, Math.abs(leftBackPower));
                max = Math.max(max, Math.abs(rightBackPower));
                i = gamepad1.right_trigger;
                if (max > 1) {
                    leftFrontPower /= max;
                    rightFrontPower /= max;
                    leftBackPower /= max;
                    rightBackPower /= max;
                    i /= max;
                    yaw2 /= max;
                }

                if (gamepad1.right_bumper) {
                    i = i;
                } else {
                    leftFrontPower /= 2;
                    rightFrontPower /= 2;
                    leftBackPower /= 2;
                    rightBackPower /= 2;
                }

                // Send calculated power to wheels
                HW.frontLeftMotor.setPower(leftFrontPower);
                HW.frontRightMotor.setPower(rightFrontPower);
                HW.backLeftMotor.setPower(leftBackPower);
                HW.backRightMotor.setPower(rightBackPower);

                // Add telemetry data for manual mode
                telemetry.addData("Mode", "Manual Control");
                telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
                telemetry.addData("Back left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            }
            // --- AUTO-AIM LOGIC END ---


            // --- Your other logic (intake, etc.) ---
            // This code is outside the if/else, so it runs all the time
            double intakeMotorPower;
            double outTakeMotorLeftPower;
            double outTakeMotorRightPower;

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

            // --- New Outtake Logic with 4-Button Toggle ---

            // 1. Get current button states from gamepad2
            boolean isGamepad2A_Pressed = gamepad2.a;
            boolean isGamepad2B_Pressed = gamepad2.b;
            boolean isGamepad2X_Pressed = gamepad2.x;
            boolean isGamepad2Y_Pressed = gamepad2.y;

            // 2. Check for new presses (edge detection) and set power
            if (isGamepad2A_Pressed && !wasGamepad2A_Pressed) {
                // A was just pressed
                if (currentOuttakePower == OUTTAKE_POWER_A) {
                    currentOuttakePower = 0.0; // Toggle off
                } else {
                    currentOuttakePower = OUTTAKE_POWER_A; // Set to A's power
                }
            }

            if (isGamepad2B_Pressed && !wasGamepad2B_Pressed) {
                // B was just pressed
                if (currentOuttakePower == OUTTAKE_POWER_B) {
                    currentOuttakePower = 0.0; // Toggle off
                } else {
                    currentOuttakePower = OUTTAKE_POWER_B; // Set to B's power
                }
            }

            if (isGamepad2X_Pressed && !wasGamepad2X_Pressed) {
                // X was just pressed
                if (currentOuttakePower == OUTTAKE_POWER_X) {
                    currentOuttakePower = 0.0; // Toggle off
                } else {
                    currentOuttakePower = OUTTAKE_POWER_X; // Set to X's power
                }
            }

            if (isGamepad2Y_Pressed && !wasGamepad2Y_Pressed) {
                // Y was just pressed
                if (currentOuttakePower == OUTTAKE_POWER_Y) {
                    currentOuttakePower = 0.0; // Toggle off
                } else {
                    currentOuttakePower = OUTTAKE_POWER_Y; // Set to Y's power
                }
            }

            // 3. Update "wasPressed" variables for the next loop
            wasGamepad2A_Pressed = isGamepad2A_Pressed;
            wasGamepad2B_Pressed = isGamepad2B_Pressed;
            wasGamepad2X_Pressed = isGamepad2X_Pressed;
            wasGamepad2Y_Pressed = isGamepad2Y_Pressed;

            // 4. Set motor power based on the current state
            outTakeMotorLeftPower = currentOuttakePower;
            outTakeMotorRightPower = currentOuttakePower;

            // --- End of New Outtake Logic ---

            yaw2 = yaw2 / 1.5;

            // Send calculated power to non-drive motors
            HW.intakeMotor.setPower(intakeMotorPower);


            // Show the elapsed game time
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.update(); // Update all telemetry
        }

        // --- AUTO-AIM CLEANUP START ---
        // This runs once after the OpMode is stopped
        visionPortal.close();
        // --- AUTO-AIM CLEANUP END ---
    }
}