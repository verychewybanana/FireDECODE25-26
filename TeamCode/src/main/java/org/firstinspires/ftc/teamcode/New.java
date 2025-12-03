package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 **/


@TeleOp(name="New TeleOp", group="Linear Opmode")
public class New extends LinearOpMode {

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

            // --- 2. MANUAL DRIVE LOGIC ---
            // This runs all the time now

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


            // --- Your other logic (intake, etc.) ---
            // This code is outside the if/else, so it runs all the time
            double intakeMotorPower;
            double outTake1Power;
            double outTake2Power;

            yaw2 = gamepad2.right_stick_x;
            // x = outake 1
            // y = outake 2
            // a = outake 3
            // b = all of them at once

            if (gamepad2.x)
                outTake1Power = 0.8;
            else
                outTake1Power = 0;

            if (gamepad2.y)
                outTake2Power = 0.8;
            else
                outTake2Power = 0;

            if (gamepad2.b) {
                outTake1Power = 0.8;
                outTake2Power = 0.8;
            } else {
                outTake1Power = 0;
                outTake2Power = 0;
            }

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


            yaw2 = yaw2 / 1.5;

            // Send calculated power to non-drive motors
            HW.intakeMotor.setPower(intakeMotorPower);
            HW.outTake1.setPower(outTake1Power) ;
            HW.outTake2.setPower(outTake2Power);

            // Show the elapsed game time
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.update(); // Update all telemetry
        }
    }
}