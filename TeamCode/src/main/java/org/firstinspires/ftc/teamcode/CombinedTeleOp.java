package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                    Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise   Right-joystick Right and Left
 *
 **/

@TeleOp(name="Combined TeleOp", group="Linear Opmode")
public class CombinedTeleOp extends LinearOpMode {

    /*
    Controls for gamepad2
    Left bumper   - intake in slowly
    Right bumper  - intake in faster
    Left trigger  - intake out slowly (reverse)
    Right trigger - intake out faster (reverse)
    A/B/X/Y       - outtake power toggles
    */

    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;

    public final double leftRightServoSpeed = 0.01;
    public final double backRightMultiplier = 1.1;
    public boolean isStrafing = false;

    // --- OUTTAKE POWER PRESETS ---
    public final double OUTTAKE_POWER_A = 0.25; // Low power
    public final double OUTTAKE_POWER_B = 0.7;  // Higher power
    public final double OUTTAKE_POWER_X = 0.35; // Medium power
    public final double OUTTAKE_POWER_Y = 0.57; // Medium-high power

    private double currentOuttakePower = 0.0; // Current power state

    // Edge detection for gamepad2 buttons
    boolean wasGamepad2A_Pressed = false;
    boolean wasGamepad2B_Pressed = false;
    boolean wasGamepad2X_Pressed = false;
    boolean wasGamepad2Y_Pressed = false;

    // --- OUTTAKE RPM STUFF (from Outtake Mech TeleOp) ---
    // goBILDA Yellow Jacket 6000 RPM, 1:1 ratio → 28 ticks per rev
    private static final double OUTTAKE_TICKS_PER_REV = 28.0;

    private int lastOuttake1Pos = 0;
    private int lastOuttake2Pos = 0;
    private long lastOuttakeSampleTimeNs = 0;

    private double outtake1Rpm = 0.0;
    private double outtake2Rpm = 0.0;

    @Override
    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);

        // Telemetry to BOTH Driver Hub + Dashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        // Init encoders for RPM on outtakes
        try {
            HW.outTakeLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            HW.outTakeRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            HW.outTakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            HW.outTakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            lastOuttake1Pos = HW.outTakeLeft.getCurrentPosition();
            lastOuttake2Pos = HW.outTakeRight.getCurrentPosition();
            lastOuttakeSampleTimeNs = System.nanoTime();
        } catch (Exception e) {
            telemetry.addData("Outtake Encoders", "Init failed: %s", e.getMessage());
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // MAIN LOOP
        while (opModeIsActive()) {
            double max;
            double i = 0.0;

            // --- DRIVE (from New TeleOp, gamepad1) ---

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial = gamepad1.left_stick_y;            // forward/back
            double lateral = -gamepad1.left_stick_x * 1.1;    // strafe
            double yaw = gamepad1.right_stick_x;              // rotation

            double axial2 = -gamepad2.left_stick_y;           // (not used, kept for feel)
            double yaw2   = gamepad2.right_stick_x;           // (not used, kept so behavior matches)

            // Combine joystick requests for mecanum drive.
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            // Normalize so no wheel power exceeds 100%
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));
            i = gamepad1.right_trigger;
            if (max > 1) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
                i /= max;
                yaw2 /= max;
            }

            // Half speed unless right bumper held (same as New)
            if (!gamepad1.right_bumper) {
                leftFrontPower  /= 2;
                rightFrontPower /= 2;
                leftBackPower   /= 2;
                rightBackPower  /= 2;
            }

            // Apply drive powers
            HW.frontLeftMotor.setPower(leftFrontPower);
            HW.frontRightMotor.setPower(rightFrontPower);
            HW.backLeftMotor.setPower(leftBackPower);
            HW.backRightMotor.setPower(rightBackPower);

            telemetry.addData("Mode", "Manual Control");
            telemetry.addData("Front left/right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/right", "%4.2f, %4.2f", leftBackPower, rightBackPower);

            // --- INTAKE + OUTTAKE (from Outtake Mech TeleOp, gamepad2) ---

            double intakeMotorPower;
            double outTakeMotorLeftPower;
            double outTakeMotorRightPower;

            // Intake control
            if (gamepad2.left_bumper) {
                intakeMotorPower = 0.4;
            } else if (gamepad2.right_bumper) {
                intakeMotorPower = 0.75;
            } else if (gamepad2.right_trigger > 0) {
                intakeMotorPower = -0.4;
            } else if (gamepad2.left_trigger > 0) {
                intakeMotorPower = -0.2;
            } else {
                intakeMotorPower = 0;
            }

            // --- Outtake 4-button toggle logic ---

            boolean isGamepad2A_Pressed = gamepad2.a;
            boolean isGamepad2B_Pressed = gamepad2.b;
            boolean isGamepad2X_Pressed = gamepad2.x;
            boolean isGamepad2Y_Pressed = gamepad2.y;

            /* //A toggle for outtake
            if (isGamepad2A_Pressed && !wasGamepad2A_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_A) {
                    currentOuttakePower = 0.0;
                } else {
                    currentOuttakePower = OUTTAKE_POWER_A;
                }
            } */

            // --- MID MOTOR CONTROL (gamepad2 A) ---
            double midMotorPower;

            if (gamepad2.a) {
                midMotorPower = -0.8;
            } else {
                midMotorPower = 0.0;
            }

            HW.midMotor.setPower(midMotorPower);

            // B toggle
            if (isGamepad2B_Pressed && !wasGamepad2B_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_B) {
                    currentOuttakePower = 0.0;
                } else {
                    currentOuttakePower = OUTTAKE_POWER_B;
                }
            }

            // X toggle
            if (isGamepad2X_Pressed && !wasGamepad2X_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_X) {
                    currentOuttakePower = 0.0;
                } else {
                    currentOuttakePower = OUTTAKE_POWER_X;
                }
            }

            // Y toggle
            if (isGamepad2Y_Pressed && !wasGamepad2Y_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_Y) {
                    currentOuttakePower = 0.0;
                } else {
                    currentOuttakePower = OUTTAKE_POWER_Y;
                }
            }

            // Save button states
            wasGamepad2A_Pressed = isGamepad2A_Pressed;
            wasGamepad2B_Pressed = isGamepad2B_Pressed;
            wasGamepad2X_Pressed = isGamepad2X_Pressed;
            wasGamepad2Y_Pressed = isGamepad2Y_Pressed;

            // Same outtake power to both motors
            outTakeMotorLeftPower  = currentOuttakePower;
            outTakeMotorRightPower = currentOuttakePower;

            HW.intakeMotor.setPower(intakeMotorPower);
            HW.outTakeLeft.setPower(outTakeMotorLeftPower);
            HW.outTakeRight.setPower(outTakeMotorRightPower);

            // --- OUTTAKE RPM UPDATE (from Outtake Mech TeleOp) ---

            long now = System.nanoTime();
            double dt = (now - lastOuttakeSampleTimeNs) / 1_000_000_000.0; // seconds

            if (dt > 0.05) { // ~20 Hz update
                int pos1 = HW.outTakeLeft.getCurrentPosition();
                int pos2 = HW.outTakeRight.getCurrentPosition();

                double revs1 = (pos1 - lastOuttake1Pos) / OUTTAKE_TICKS_PER_REV;
                double revs2 = (pos2 - lastOuttake2Pos) / OUTTAKE_TICKS_PER_REV;

                outtake1Rpm = (revs1 / dt) * 60.0;
                outtake2Rpm = (revs2 / dt) * 60.0;

                lastOuttake1Pos = pos1;
                lastOuttake2Pos = pos2;
                lastOuttakeSampleTimeNs = now;
            }

            // --- TELEMETRY ---

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Intake Power", "%.2f", intakeMotorPower);
            telemetry.addData("Outtake Power", "%.2f", currentOuttakePower);
            telemetry.addData("Outtake1 RPM", "%.0f", outtake1Rpm);
            telemetry.addData("Outtake2 RPM", "%.0f", outtake2Rpm);
            telemetry.addData("Δ RPM", "%.0f", (outtake1Rpm - outtake2Rpm));
            telemetry.addData("Intake Power", "%.2f", intakeMotorPower);


            telemetry.update();
        }
    }
}
