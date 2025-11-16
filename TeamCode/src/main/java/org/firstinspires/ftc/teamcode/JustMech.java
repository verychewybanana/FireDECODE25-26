package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// All imports for Vision, Camera, and ElapsedTime have been removed
// as they are no longer needed.

/**
 * This is a simplified TeleOp, modified to ONLY control
 * the intake and outtake mechanisms based on the user's request.
 *
 * All driving and auto-aim code has been removed.
 *
 * --- CONTROLS (Gamepad 2) ---
 * Left Bumper:  Intake (Speed 1: 0.4)
 * Right Bumper: Intake (Speed 2: 0.8)
 * Right Trigger: Reverse Intake (Speed 1: -0.4)
 * Left Trigger:  Reverse Intake (Speed 2: -0.2)
 *
 * A: Toggle Outtake Power (0.25)
 * B: Toggle Outtake Power (0.7)
 * X: Toggle Outtake Power (0.35)
 * Y: Toggle Outtake Power (0.57)
 */

@TeleOp(name="JustMech", group="Linear Opmode") // Renamed for clarity
public class JustMech extends LinearOpMode {

    // Declare OpMode members.
    private FireHardwareMap HW = null;

    // --- OUTTAKE POWER VARIABLES ---
    // (From your original code)
    public final double OUTTAKE_POWER_A = 0.25; // Low power
    public final double OUTTAKE_POWER_B = 0.7; // Medium-low power
    public final double OUTTAKE_POWER_X = 0.35; // Medium-high power
    public final double OUTTAKE_POWER_Y = 0.57;  // Full power

    private double currentOuttakePower = 0.0; // Tracks the current power state

    // Variables to track *previous* button states for edge detection
    boolean wasGamepad2A_Pressed = false;
    boolean wasGamepad2B_Pressed = false;
    boolean wasGamepad2X_Pressed = false;
    boolean wasGamepad2Y_Pressed = false;
    // --- END NEW OUTTAKE POWER VARIABLES ---

    @Override
    public void runOpMode() {
        // Initialize the hardware map
        HW = new FireHardwareMap(this.hardwareMap);

        // Initialize FTC Dashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();

        // All Auto-Aim initialization has been removed.

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.addData("Controls", "Gamepad 2 for Intake/Outtake");
        telemetry.update();

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // All Auto-Aim and Drive logic has been removed.

            // --- Intake and Outtake Logic ---
            double intakeMotorPower;
            double outTakeMotorLeftPower;
            double outTakeMotorRightPower;

            // --- Intake Logic (from your original code) ---
            if (gamepad2.left_bumper) {
                intakeMotorPower = 0.4;
            } else if (gamepad2.right_bumper) {
                intakeMotorPower = 0.8;
            } else if (gamepad2.right_trigger > 0) {
                intakeMotorPower = -0.4;
            } else if (gamepad2.left_trigger > 0) {
                intakeMotorPower = -0.2;
            } else {
                intakeMotorPower = 0;
            }

            // --- Outtake Logic with 4-Button Toggle (from your original code) ---

            // 1. Get current button states from gamepad2
            boolean isGamepad2A_Pressed = gamepad2.a;
            boolean isGamepad2B_Pressed = gamepad2.b;
            boolean isGamepad2X_Pressed = gamepad2.x;
            boolean isGamepad2Y_Pressed = gamepad2.y;

            // 2. Check for new presses (edge detection) and set power
            // This logic toggles the power on/off for each button.
            // Pressing a new button overrides the old power.

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
            // --- End of Outtake Logic ---

            // Send calculated power to the motors
            HW.intakeMotor.setPower(intakeMotorPower);
            HW.outTakeMotorLeft.setPower(outTakeMotorLeftPower);
            HW.outTakeMotorRight.setPower(outTakeMotorRightPower);

            // Show telemetry
            telemetry.addData("Intake Power", intakeMotorPower);
            telemetry.addData("Outtake Power", currentOuttakePower);
            telemetry.update(); // Update all telemetry

        } // end while(opModeIsActive())

        // All Auto-Aim cleanup logic has been removed.

    } // end runOpMode()
} // end class