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
    Right bumper  - intake in faster (reverse)
    Left trigger  - mid motor out slowly (reverse)
    Right trigger - mid motor in faster
    A/B/X/Y       - outtake power toggles
    */

    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;

    public final double leftRightServoSpeed = 0.01;
    public final double backRightMultiplier = 1.1;
    public boolean isStrafing = false;

    // --- OUTTAKE POWER PRESETS ---
    public final double OUTTAKE_POWER_A = 0.55; // Low power
    public final double OUTTAKE_POWER_X = 0.57; // Mid power
    public final double OUTTAKE_POWER_Y = 0.58; // Mid-high power
    public final double OUTTAKE_POWER_B = 0.59; // High power

    private double currentOuttakePower = 0.0; // Current power state

    // Edge detection for gamepad2 buttons
    boolean wasGamepad2A_Pressed = false;
    boolean wasGamepad2B_Pressed = false;
    boolean wasGamepad2X_Pressed = false;
    boolean wasGamepad2Y_Pressed = false;

    // --- OUTTAKE RPM STUFF ---
    // goBILDA Yellow Jacket 6000 RPM, 1:1 ratio → 28 ticks per rev
    private static final double OUTTAKE_TICKS_PER_REV = 28.0;

    private int lastOuttake1Pos = 0;
    private int lastOuttake2Pos = 0;
    private long lastOuttakeSampleTimeNs = 0;

    private double outtake1Rpm = 0.0;
    private double outtake2Rpm = 0.0;

    @Override
    public void runOpMode() {

        // Initialize hardware
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

            // --- DRIVE (gamepad1) ---

            // Axial: forward/back, Lateral: strafe, Yaw: rotate
            double axial   = gamepad1.left_stick_y;
            double lateral = -gamepad1.left_stick_x * 1.1;
            double yaw     = gamepad1.right_stick_x;

            // Combine joystick requests for mecanum drive.
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            // Normalize so no wheel power exceeds 100%
            double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }

            // Half speed unless right bumper held (can change what you divide by)
            if (!gamepad1.right_bumper) {
                leftFrontPower  /= 1.6;
                rightFrontPower /= 1.6;
                leftBackPower   /= 1.6;
                rightBackPower  /= 1.6;
            }

            // Apply drive powers
            HW.frontLeftMotor.setPower(leftFrontPower);
            HW.frontRightMotor.setPower(rightFrontPower);
            HW.backLeftMotor.setPower(leftBackPower);
            HW.backRightMotor.setPower(rightBackPower);

            telemetry.addData("Mode", "Manual Control");
            telemetry.addData("Front L/R", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  L/R", "%4.2f, %4.2f", leftBackPower, rightBackPower);

            // --- INTAKE + MID + OUTTAKE (gamepad2) ---

            double intakeMotorPower = 0.0;
            double midMotorPower    = 0.0;
            double outTakeMotorLeftPower;
            double outTakeMotorRightPower;

            // Intake + mid control
            // bumpers: intake, triggers: mid motor
            if (gamepad2.left_bumper) {
                // intake in slowly
                midMotorPower = -0.5;
                intakeMotorPower = -0.75;
            } else if (gamepad2.right_bumper) {
                // intake out faster (reverse)
                midMotorPower = -0.5;
                //intakeMotorPower = 0.0;
            } else if (gamepad2.right_trigger > 0.1) {
                // mid in
                midMotorPower = 0.15;
                intakeMotorPower = 0.0;
            } else if (gamepad2.left_trigger > 0.1) {
                // mid out
                midMotorPower = 0.0;
                intakeMotorPower = 0.4;
            } else {
                intakeMotorPower = 0.0;
                midMotorPower = 0.0;
            }

            // --- Outtake 4-button toggle logic ---

            boolean isGamepad2A_Pressed = gamepad2.a;
            boolean isGamepad2B_Pressed = gamepad2.b;
            boolean isGamepad2X_Pressed = gamepad2.x;
            boolean isGamepad2Y_Pressed = gamepad2.y;

            // A toggle
            if (isGamepad2A_Pressed && !wasGamepad2A_Pressed) {
                if (currentOuttakePower == OUTTAKE_POWER_A) {
                    currentOuttakePower = 0.0;
                } else {
                    currentOuttakePower = OUTTAKE_POWER_A;
                }
            }

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

            // Save button states for edge detection
            wasGamepad2A_Pressed = isGamepad2A_Pressed;
            wasGamepad2B_Pressed = isGamepad2B_Pressed;
            wasGamepad2X_Pressed = isGamepad2X_Pressed;
            wasGamepad2Y_Pressed = isGamepad2Y_Pressed;

            // Same outtake power to both motors
            outTakeMotorLeftPower  = currentOuttakePower;
            outTakeMotorRightPower = currentOuttakePower;

            // Apply intake / mid / outtake powers
            HW.intakeMotor.setPower(intakeMotorPower);
            HW.midMotor.setPower(midMotorPower);
            HW.outTakeLeft.setPower(outTakeMotorLeftPower);
            HW.outTakeRight.setPower(outTakeMotorRightPower);

            // --- OUTTAKE RPM UPDATE ---

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
            telemetry.addData("Mid Power", "%.2f", midMotorPower);
            telemetry.addData("Outtake Power", "%.2f", currentOuttakePower);
            telemetry.addData("Outtake1 RPM", "%.0f", outtake1Rpm);
            telemetry.addData("Outtake2 RPM", "%.0f", outtake2Rpm);
            telemetry.addData("Δ RPM", "%.0f", (outtake1Rpm - outtake2Rpm));

            telemetry.update();
        }
    }
}
