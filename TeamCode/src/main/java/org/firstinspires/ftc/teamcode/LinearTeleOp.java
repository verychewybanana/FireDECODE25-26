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
    private static final int CAMERA_WIDTH = 640;
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

    @Override

    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();

        // --- AUTO-AIM INITIALIZATION START ---
        greenPurpleProcessor = new GreenPurpleProcessor();
        visionPortal = new VisionPortal.Builder()
                .setCamera(this.hardwareMap.get(WebcamName.class, "Webcam 1")) // "Webcam 1" must match your config
                .setCameraResolution(new Size(CAMERA_WIDTH, 480))
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

            double GRB = 0;
            // 0 = white 702, 631, 628
            // 1 = Green 210, 120,170
            //2 = Purple 290, 287, 360
            // 3 = Yellow 248, 308, 146

            /*
            if (HW.color.green() <= 752 && HW.color.green()>=562 && HW.color.red() <= 681 && HW.color.red() >= 581 && HW.color.blue()<=678 && HW.color.blue()>=578 ){
                HW.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
            } if (HW.color.green() <= 260 && HW.color.green()>=160 && HW.color.red() <= 170 && HW.color.red() >= 70 && HW.color.blue()<=170 && HW.color.blue()>=70) {
                HW.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
            }
            if (HW.color.green() <= 340 && HW.color.green()>=240 && HW.color.red() <= 340 && HW.color.red() >= 240 && HW.color.blue()<=410 && HW.color.blue()>=310 ){
                HW.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.VIOLET);
            }
            if (HW.color.green() <= 298 && HW.color.green()>=198 && HW.color.red() <= 358 && HW.color.red() >= 258 && HW.color.blue()<=196 && HW.color.blue()>=96 ){
                HW.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW);
            }
*/

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
                        // This logic respects your mecanum yaw calculations
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
                // --- 2. MANUAL DRIVE LOGIC (Your Original Code) ---
                // This runs when 'A' is NOT pressed

                // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
                double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
                double lateral = gamepad1.left_stick_x * 1.1;
                double yaw = gamepad1.right_stick_x;

//            if (lateral >= 0.5) isStrafing = true;


                double axial2 = -gamepad2.left_stick_y;
//            double lateral2 =  gamepad2.left_stick_x * 1.1;
                double yaw2 = gamepad2.right_stick_x;


                // Combine the joystick requests for each axis-motion to determine each wheel's power.
                // Set up a variable for each drive wheel to save the power level for telemetry.
                double leftFrontPower = axial + lateral + yaw;
                double rightFrontPower = axial - lateral - yaw;
                double leftBackPower = axial - lateral + yaw;
                double rightBackPower = axial + lateral - yaw;


                // Normalize the values so no wheel power exceeds 100%
                // This ensures that the robot maintains the desired motion.
                max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
                max = Math.max(max, Math.abs(leftBackPower));
                max = Math.max(max, Math.abs(rightBackPower));
                i = gamepad1.right_trigger;
                if (max > 1) {
                    leftFrontPower /= max;
                    rightFrontPower /= max;
                    leftBackPower /= max;
                    rightBackPower /= max;
//                axial2 /=max;
                    i /= max;
//                (lateral2)/=max;
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
            double outTakeMotorPower;
            double yaw2 = gamepad2.right_stick_x; // This line was in your original code

            if (gamepad2.left_bumper) {
                intakeMotorPower = 0.4;
            } else {
                intakeMotorPower = 0.0;
            }

            if (gamepad2.right_bumper) {
                outTakeMotorPower = 0.2;
            } else
                outTakeMotorPower = 0;

            yaw2 = yaw2 / 1.5;

            // Send calculated power to non-drive motors
            HW.intakeMotor.setPower(intakeMotorPower + 0.4);
            HW.outTakeMotor.setPower(outTakeMotorPower + 0.4);

            // Show the elapsed game time
            telemetry.addData("Status", "Run Time: " + runtime.toString());


            /*
            telemetry.addData("LED GREEN", HW.color.green());
            telemetry.addData("LED red", HW.color.red());
            telemetry.addData("LED blue", HW.color.blue());
            telemetry.addData("LED ARGB", HW.color.argb());
*/
            telemetry.update(); // Update all telemetry
/*
        }
    }
}
*/
        }

        // --- AUTO-AIM CLEANUP START ---
        // This runs once after the OpMode is stopped
        visionPortal.close();
        // --- AUTO-AIM CLEANUP END ---
    }
}