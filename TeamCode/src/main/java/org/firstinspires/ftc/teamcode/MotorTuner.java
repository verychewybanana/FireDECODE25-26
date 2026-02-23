package org.firstinspires.ftc.teamcode;
/*
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;

@TeleOp(name="Power Tuner (Calibration)", group="Tuning")
public class MotorTuner extends LinearOpMode {

    private FireHardwareMap HW = null;
    double currentPower = 0.5; // Start at 50% power

    // Button edge detection
    boolean wasA = false;
    boolean wasB = false;
    boolean wasX = false;
    boolean wasY = false;

    @Override
    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);

        // Start Limelight to see the "ty" value while tuning
        HW.limelight.pipelineSwitch(0);
        HW.limelight.start();

        telemetry.addData("Status", "Ready to Tune");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // --- INPUTS ---
            boolean isA = gamepad1.a;
            boolean isB = gamepad1.b;
            boolean isX = gamepad1.x;
            boolean isY = gamepad1.y;

            // +0.05
            if (isA && !wasA) {
                currentPower += 0.05;
            }
            // -0.05
            if (isB && !wasB) {
                currentPower -= 0.05;
            }
            // +0.025
            if (isX && !wasX) {
                currentPower += 0.025;
            }
            // -0.025
            if (isY && !wasY) {
                currentPower -= 0.025;
            }

            // Save state for next loop
            wasA = isA; wasB = isB; wasX = isX; wasY = isY;

            // Safety Clamp (0% to 100%)
            if (currentPower > 1.0) currentPower = 1.0;
            if (currentPower < 0.0) currentPower = 0.0;

            // --- MOTORS ---
            HW.outTakeLeft.setPower(currentPower);
            HW.outTakeRight.setPower(currentPower);

            // --- LIMELIGHT DATA ---
            LLResult result = HW.limelight.getLatestResult();
            double ty = 0;
            if (result != null && result.isValid()) {
                ty = result.getTy();
            }

            // --- TELEMETRY ---
            telemetry.addData(">> MOTOR POWER", "%.3f", currentPower);
            telemetry.addData(">> LIMELIGHT TY", "%.3f", ty);
            telemetry.addLine("\nControls:");
            telemetry.addLine("A: +0.05 | B: -0.05");
            telemetry.addLine("X: +0.025| Y: -0.025");
            telemetry.update();
        }
        HW.limelight.stop();
    }
}

 */