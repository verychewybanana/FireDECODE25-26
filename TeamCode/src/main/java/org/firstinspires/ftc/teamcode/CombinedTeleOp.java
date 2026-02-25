package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Controls:
 * Gamepad 1 (Driver):
 *   Left stick        - Drive (axial + lateral)
 *   Right stick       - Rotate (yaw)
 *   Right bumper      - Full speed (default is reduced speed)
 *
 * Gamepad 2 (Operator):
 *   Right trigger     - Intake in + mid motors in
 *   Left trigger      - Intake in only (no mid)
 *   Dpad left/right   - Reverse intake + mid (eject)
 *   Left bumper       - Mid motors only (no intake)
 *   A                 - Outtake low power toggle
 *   X                 - Outtake mid power toggle
 *   Y                 - Outtake mid-high power toggle
 *   B                 - Outtake high power toggle
 *   Dpad up/down      - Fine tune outtake power (trim)
 */

@TeleOp(name="Combined TeleOp", group="Linear Opmode")
public class CombinedTeleOp extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;

    // --- OUTTAKE POWER PRESETS ---
    public final double OUTTAKE_POWER_A = 0.2;
    public final double OUTTAKE_POWER_X = 0.4;
    public final double OUTTAKE_POWER_Y = 0.7;
    public final double OUTTAKE_POWER_B = 0.8;

    private double currentOuttakePower = 0.0;

    // --- OUTTAKE TRIM ---
    private double outtakeTrim = 0.0;
    private static final double OUTTAKE_TRIM_STEP = 0.01;
    private static final double OUTTAKE_TRIM_MIN  = -0.20;
    private static final double OUTTAKE_TRIM_MAX  =  0.20;

    // Edge detection
    boolean wasGamepad2A = false;
    boolean wasGamepad2B = false;
    boolean wasGamepad2X = false;
    boolean wasGamepad2Y = false;
    boolean wasDpadUp    = false;
    boolean wasDpadDown  = false;

    @Override
    public void runOpMode() {

        HW = new FireHardwareMap(this.hardwareMap);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {

            // -------------------------------------------------------
            // DRIVE (Gamepad 1)
            // -------------------------------------------------------
            double axial   =  -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x * 1.1;
            double yaw     =  gamepad1.right_stick_x;

            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            // Normalize
            double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));
            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }

            // Default reduced speed, hold right bumper for full speed
            if (!gamepad1.right_bumper) {
                leftFrontPower  /= 1.6;
                rightFrontPower /= 1.6;
                leftBackPower   /= 1.6;
                rightBackPower  /= 1.6;
            }

            HW.leftFront.setPower(leftFrontPower);
            HW.rightFront.setPower(rightFrontPower);
            HW.leftBack.setPower(leftBackPower);
            HW.rightBack.setPower(rightBackPower);

            // -------------------------------------------------------
            // INTAKE + MID MOTORS (Gamepad 2)
            // -------------------------------------------------------
            double intakePower = 0.0;
            double midPower    = 0.0;

            if (gamepad2.right_trigger > 0.1) {
                // Fast intake - intake + mid motors in
                intakePower = -0.75;
                midPower    = -0.5;
            } else if (gamepad2.right_bumper) {
                // Slow intake - intake + mid motors in slowly
                intakePower = -0.4;
                midPower    = -0.3;
            } else if (gamepad2.left_trigger > 0.1) {
                // Fast eject - intake + mid motors out
                intakePower = 0.75;
                midPower    = 0.5;
            } else if (gamepad2.left_bumper) {
                // Slow eject - intake + mid motors out slowly
                intakePower = 0.4;
                midPower    = 0.3;
            } else {
                intakePower = 0.0;
                midPower    = 0.0;
            }

            HW.intakeMotor.setPower(intakePower);
            HW.midMotor1.setPower(midPower);
            HW.midMotor2.setPower(midPower);

            // -------------------------------------------------------
            // OUTTAKE TRIM (Gamepad 2 Dpad Up/Down)
            // -------------------------------------------------------
            if (gamepad2.dpad_up && !wasDpadUp)   outtakeTrim += OUTTAKE_TRIM_STEP;
            if (gamepad2.dpad_down && !wasDpadDown) outtakeTrim -= OUTTAKE_TRIM_STEP;
            outtakeTrim = Math.max(OUTTAKE_TRIM_MIN, Math.min(OUTTAKE_TRIM_MAX, outtakeTrim));
            wasDpadUp   = gamepad2.dpad_up;
            wasDpadDown = gamepad2.dpad_down;

            // -------------------------------------------------------
            // OUTTAKE TOGGLES (Gamepad 2 A/B/X/Y)
            // -------------------------------------------------------
            if (gamepad2.a && !wasGamepad2A) {
                outtakeTrim = 0.0;
                currentOuttakePower = (currentOuttakePower == OUTTAKE_POWER_A) ? 0.0 : OUTTAKE_POWER_A;
            }
            if (gamepad2.b && !wasGamepad2B) {
                outtakeTrim = 0.0;
                currentOuttakePower = (currentOuttakePower == OUTTAKE_POWER_B) ? 0.0 : OUTTAKE_POWER_B;
            }
            if (gamepad2.x && !wasGamepad2X) {
                outtakeTrim = 0.0;
                currentOuttakePower = (currentOuttakePower == OUTTAKE_POWER_X) ? 0.0 : OUTTAKE_POWER_X;
            }
            if (gamepad2.y && !wasGamepad2Y) {
                outtakeTrim = 0.0;
                currentOuttakePower = (currentOuttakePower == OUTTAKE_POWER_Y) ? 0.0 : OUTTAKE_POWER_Y;
            }

            wasGamepad2A = gamepad2.a;
            wasGamepad2B = gamepad2.b;
            wasGamepad2X = gamepad2.x;
            wasGamepad2Y = gamepad2.y;

            double trimmedOuttake = Math.max(-1.0, Math.min(1.0, currentOuttakePower + outtakeTrim));
            HW.outtakeMotor.setPower(trimmedOuttake);

            // -------------------------------------------------------
            // TELEMETRY
            // -------------------------------------------------------
            telemetry.addData("Status",       "Run Time: " + runtime.toString());
            telemetry.addData("Drive L/R Front", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Drive L/R Back",  "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.addData("Intake Power",    "%.2f", intakePower);
            telemetry.addData("Mid Power",       "%.2f", midPower);
            telemetry.addData("Outtake Power",   "%.2f", currentOuttakePower);
            telemetry.addData("Outtake Trim",    "%.2f", outtakeTrim);
            telemetry.addData("Outtake Cmd",     "%.2f", trimmedOuttake);
            telemetry.update();
        }
    }
}