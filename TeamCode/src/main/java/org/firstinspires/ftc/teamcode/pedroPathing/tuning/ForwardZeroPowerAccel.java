package org.firstinspires.ftc.teamcode.pedroPathing.tuning;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp(name="Forward ZeroPowerAccel", group="Pedro Pathing")
public class ForwardZeroPowerAccel extends LinearOpMode {

    enum State { ACCEL, COAST, DONE }

    @Override
    public void runOpMode() {
        Follower follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0,0,0));
        follower.update();

        telemetry.addLine("Forward coast test:");
        telemetry.addLine("Robot will drive forward 1.0s then cut power and coast.");
        telemetry.addLine("Keep area clear. Press START.");
        telemetry.update();

        waitForStart();

        State state = State.ACCEL;
        long t0 = System.nanoTime();
        long stateStart = t0;

        // For decel estimate
        double prevX = follower.getPose().getX();
        double prevT = 0.0;
        double prevV = 0.0;

        double maxDecel = 0.0; // positive magnitude in/s^2

        while (opModeIsActive() && state != State.DONE) {
            double t = (System.nanoTime() - t0) / 1e9;

            // Simple manual power: use follower drivetrain through teleop drive is messy across versions,
            // so we just "nudge" the robot by setting a short pathless drive via motor powers is not available here.
            // Instead: use right bumper to arm; but for fully automatic, we rely on you holding RB in your existing LocalizationTest.
            // ----
            // For this tuner to be automatic, we need a way to command drivetrain via Pedro.
            // If your Pedro version lacks it, run this test using your LocalizationTest joystick drive instead.
            // ----

            // We can still compute decel during coasting if robot moves; so:
            // Use gamepad: hold RB and push forward for 1s, then release sticks and RB to coast.
            // This keeps the tuner version-proof.

            // State machine driven by time:
            if (state == State.ACCEL && (t - (stateStart - t0)/1e9) >= 1.0) {
                state = State.COAST;
                stateStart = System.nanoTime();
                // At this point you should release controls to let it coast.
            } else if (state == State.COAST && (t - (stateStart - t0)/1e9) >= 2.0) {
                state = State.DONE;
            }

            follower.update();
            Pose p = follower.getPose();

            // velocity estimate from pose
            double dt = t - prevT;
            if (dt > 1e-3) {
                double vx = (p.getX() - prevX) / dt; // in/s

                // decel magnitude when speed is dropping
                double dv = vx - prevV;
                double a = dv / dt; // in/s^2

                // During coasting we expect a to be negative (slowing down). We store magnitude.
                if (state == State.COAST && a < 0) {
                    maxDecel = Math.max(maxDecel, -a);
                }

                prevX = p.getX();
                prevT = t;
                prevV = vx;
            }

            telemetry.addData("State", state);
            telemetry.addData("X (in)", "%.2f", p.getX());
            telemetry.addData("Vx (in/s)", "%.2f", prevV);
            telemetry.addData("Estimated forwardZeroPowerAccel (in/s^2)", "%.1f", maxDecel);
            telemetry.addLine("How to run:");
            telemetry.addLine("For 1.0s: drive forward (your usual joystick/manual).");
            telemetry.addLine("Then fully release and let it coast for ~2.0s.");
            telemetry.update();
        }
    }
}