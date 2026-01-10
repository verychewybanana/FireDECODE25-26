package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.limelightvision.LLResult;

@TeleOp(name="Combined TeleOp Auto-Aim", group="Linear Opmode")
public class AutoAimTeleOp extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;

    // --- TUNING VARIABLES (Edit these to tune your robot) ---

    // Auto-Aim PID (Driver 1)
    final double TURN_P = 0.03;  // Proportional gain: How hard to turn per degree of error
    final double MAX_AUTO_TURN = 0.5; // Cap turn speed for safety

    // Auto-Shoot Physics (Driver 2)
    final double TARGET_VOLTAGE = 12.0; // The voltage we tuned the robot at
    // Equation: Power = BASE + (MULTIPLIER * ty)
    final double DISTANCE_BASE_POWER = 0.45; // Power at 0 degrees offset
    final double DISTANCE_SCALAR = 0.02;     // How much power to add per degree of vertical offset

    // --- STATE VARIABLES ---

    // Driver 1
    boolean autoAimEnabled = false;
    boolean wasGamepad1A_Pressed = false;

    // Driver 2
    boolean autoSpeedEnabled = false;
    boolean wasGamepad2A_Pressed = false;

    // Outtake Toggles
    // B, X, Y manual presets
    final double OUTTAKE_POWER_B = 0.7;
    final double OUTTAKE_POWER_X_MANUAL = 0.35;
    final double OUTTAKE_POWER_Y = 0.57;

    double currentOuttakePower = 0.0;
    boolean wasGamepad2B_Pressed = false;
    boolean wasGamepad2X_Pressed = false;
    boolean wasGamepad2Y_Pressed = false;

    // RPM Logic
    private static final double OUTTAKE_TICKS_PER_REV = 28.0;
    private int lastOuttake1Pos = 0;
    private int lastOuttake2Pos = 0;
    private long lastOuttakeSampleTimeNs = 0;
    private double outtake1Rpm = 0.0;
    private double outtake2Rpm = 0.0;

    @Override
    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);

        // Telemetry Setup
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        // Configure Limelight
        // Important: In Limelight OS, ensure Pipeline 0 is your Red Backboard detector
        HW.limelight.pipelineSwitch(0);
        HW.limelight.start();

        // Reset Encoders
        try {
            HW.outTakeLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            HW.outTakeRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            HW.outTakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            HW.outTakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lastOuttake1Pos = HW.outTakeLeft.getCurrentPosition();
            lastOuttake2Pos = HW.outTakeRight.getCurrentPosition();
            lastOuttakeSampleTimeNs = System.nanoTime();
        } catch (Exception e) {
            telemetry.addData("Error", "Encoder init failed");
        }

        telemetry.addData("Status", "Initialized. Press Start.");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // 1. GET SENSOR DATA
            LLResult result = HW.limelight.getLatestResult();
            double tx = 0; // Horizontal offset (Left/Right)
            double ty = 0; // Vertical offset (Distance)
            boolean targetFound = false;

            if (result != null && result.isValid()) {
                tx = result.getTx();
                ty = result.getTy();
                targetFound = true;
            }

            double batteryVoltage = HW.batteryVoltageSensor.getVoltage();

            // =========================================================
            // DRIVER 1: CHASSIS & AUTO-AIM
            // =========================================================

            // Toggle A Logic (Auto-Aim)
            if (gamepad1.a && !wasGamepad1A_Pressed) {
                autoAimEnabled = !autoAimEnabled;
            }
            wasGamepad1A_Pressed = gamepad1.a;

            double axial = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x * 1.1;
            double yaw = gamepad1.right_stick_x;

            // Auto-Aim Override
            if (autoAimEnabled) {
                if (targetFound) {
                    // Simple P-Controller: Turn against the error (tx)
                    // If tx is positive (target to right), we turn right (positive yaw)
                    // Note: You might need to flip the sign (-) depending on motor config
                    double turnPower = -tx * TURN_P;

                    // Clamp power
                    turnPower = Math.max(-MAX_AUTO_TURN, Math.min(MAX_AUTO_TURN, turnPower));

                    yaw = turnPower; // Override manual yaw
                    telemetry.addData("Driver 1", "AUTOAIMING ON");
                } else {
                    // Target not seen, maybe rumble controller?
                    telemetry.addData("Driver 1", "AUTOAIMING SEARCHING...");
                }
            }

            // Mecanum Calculations
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            // Normalize
            double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));
            if (max > 1.0) {
                leftFrontPower /= max; rightFrontPower /= max;
                leftBackPower /= max; rightBackPower /= max;
            }

            // Slow mode
            if (!gamepad1.right_bumper) {
                leftFrontPower /= 2; rightFrontPower /= 2;
                leftBackPower /= 2; rightBackPower /= 2;
            }

            HW.frontLeftMotor.setPower(leftFrontPower);
            HW.frontRightMotor.setPower(rightFrontPower);
            HW.backLeftMotor.setPower(leftBackPower);
            HW.backRightMotor.setPower(rightBackPower);


            // =========================================================
            // DRIVER 2: SYSTEMS & AUTO-SHOOT
            // =========================================================

            // Toggle A Logic (Auto-Speed Mode)
            if (gamepad2.a && !wasGamepad2A_Pressed) {
                autoSpeedEnabled = !autoSpeedEnabled;
                // Reset power when toggling mode to prevent accidents
                currentOuttakePower = 0.0;
            }
            wasGamepad2A_Pressed = gamepad2.a;

            // Logic Split: Auto Mode vs Manual Mode
            if (autoSpeedEnabled) {
                telemetry.addData("Driver 2", "MOTORSPEED ADJUSTING ON");

                // 1. Calculate Ideal Power based on Distance (ty)
                // Linear regression: As ty increases (target higher in view -> closer), what does power do?
                // Usually: Further away = lower ty = more power needed.
                double calculatedPower = DISTANCE_BASE_POWER + (ty * DISTANCE_SCALAR);

                // 2. Adjust for Voltage
                // If battery is low (10V), we need MORE power than at 12V.
                // Multiplier = 12 / 10 = 1.2x boost
                double voltageMultiplier = TARGET_VOLTAGE / batteryVoltage;
                double finalAutoPower = calculatedPower * voltageMultiplier;

                // Clamp to safe range (0 to 1)
                finalAutoPower = Math.min(1.0, Math.max(0.0, finalAutoPower));

                telemetry.addData("Auto Calc", "Dist(ty): %.2f | Volt: %.2f | Pwr: %.2f", ty, batteryVoltage, finalAutoPower);

                // X Button Logic (Toggle firing in Auto Mode)
                if (gamepad2.x && !wasGamepad2X_Pressed) {
                    if (currentOuttakePower > 0) currentOuttakePower = 0;
                    else currentOuttakePower = finalAutoPower;
                }

                // If the motor is currently running, keep updating the power dynamically!
                if (currentOuttakePower > 0) {
                    currentOuttakePower = finalAutoPower;
                }

            } else {
                // MANUAL MODE (Old Logic)
                if (gamepad2.b && !wasGamepad2B_Pressed) {
                    currentOuttakePower = (currentOuttakePower == OUTTAKE_POWER_B) ? 0 : OUTTAKE_POWER_B;
                }
                if (gamepad2.x && !wasGamepad2X_Pressed) {
                    currentOuttakePower = (currentOuttakePower == OUTTAKE_POWER_X_MANUAL) ? 0 : OUTTAKE_POWER_X_MANUAL;
                }
                if (gamepad2.y && !wasGamepad2Y_Pressed) {
                    currentOuttakePower = (currentOuttakePower == OUTTAKE_POWER_Y) ? 0 : OUTTAKE_POWER_Y;
                }
            }

            // Save Button States
            wasGamepad2X_Pressed = gamepad2.x;
            wasGamepad2B_Pressed = gamepad2.b;
            wasGamepad2Y_Pressed = gamepad2.y;

            // Apply Powers
            HW.outTakeLeft.setPower(currentOuttakePower);
            HW.outTakeRight.setPower(currentOuttakePower);

            // Intake Logic (Unchanged)
            double intakePower = 0;
            if (gamepad2.left_bumper) intakePower = -0.4;
            else if (gamepad2.right_bumper) intakePower = -0.75;
            else if (gamepad2.right_trigger > 0) intakePower = 0.4;
            else if (gamepad2.left_trigger > 0) intakePower = 0.2;
            HW.intakeMotor.setPower(intakePower);

            // Mid Motor (Manual A override removed since A is now Mode Toggle)
            // If you still need Mid Motor control, assign it to Dpad or another button.
            HW.midMotor.setPower(0);

            // =========================================================
            // TELEMETRY & RPM
            // =========================================================

            // RPM Calculation
            long now = System.nanoTime();
            double dt = (now - lastOuttakeSampleTimeNs) / 1e9;
            if (dt > 0.05) {
                int pos1 = HW.outTakeLeft.getCurrentPosition();
                int pos2 = HW.outTakeRight.getCurrentPosition();
                outtake1Rpm = ((pos1 - lastOuttake1Pos) / OUTTAKE_TICKS_PER_REV / dt) * 60.0;
                outtake2Rpm = ((pos2 - lastOuttake2Pos) / OUTTAKE_TICKS_PER_REV / dt) * 60.0;
                lastOuttake1Pos = pos1; lastOuttake2Pos = pos2;
                lastOuttakeSampleTimeNs = now;
            }

            telemetry.addData("Outtake Pwr", "%.2f", currentOuttakePower);
            telemetry.addData("Vision", "Target: %b | tx: %.2f", targetFound, tx);
            telemetry.update();
        }

        HW.limelight.stop();
    }
}