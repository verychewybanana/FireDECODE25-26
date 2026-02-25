package org.firstinspires.ftc.teamcode.pedroPathing.tuning;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp(name="Lateral ZeroPowerAccel", group="Pedro Pathing")
public class LateralZeroPowerAccel extends LinearOpMode {

    enum State { ACCEL, COAST, DONE }

    @Override
    public void runOpMode() {
        Follower follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0,0,0));
        follower.update();

        telemetry.addLine("Strafe coast test:");
        telemetry.addLine("For 1.0s: strafe right under manual control, then release to coast.");
        telemetry.update();

        waitForStart();

        State state = State.ACCEL;
        long t0 = System.nanoTime();
        long stateStart = t0;

        double prevY = follower.getPose().getY();
        double prevT = 0.0;
        double prevV = 0.0;

        double maxDecel = 0.0; // positive magnitude in/s^2

        while (opModeIsActive() && state != State.DONE) {
            double t = (System.nanoTime() - t0) / 1e9;

            if (state == State.ACCEL && (t - (stateStart - t0)/1e9) >= 1.0) {
                state = State.COAST;
                stateStart = System.nanoTime();
            } else if (state == State.COAST && (t - (stateStart - t0)/1e9) >= 2.0) {
                state = State.DONE;
            }

            follower.update();
            Pose p = follower.getPose();

            double dt = t - prevT;
            if (dt > 1e-3) {
                double vy = (p.getY() - prevY) / dt; // in/s
                double dv = vy - prevV;
                double a = dv / dt;

                if (state == State.COAST && a < 0) {
                    maxDecel = Math.max(maxDecel, -a);
                }

                prevY = p.getY();
                prevT = t;
                prevV = vy;
            }

            telemetry.addData("State", state);
            telemetry.addData("Y (in)", "%.2f", p.getY());
            telemetry.addData("Vy (in/s)", "%.2f", prevV);
            telemetry.addData("Estimated lateralZeroPowerAccel (in/s^2)", "%.1f", maxDecel);
            telemetry.addLine("How to run:");
            telemetry.addLine("For 1.0s: strafe right manually.");
            telemetry.addLine("Then fully release and let it coast for ~2.0s.");
            telemetry.update();
        }
    }
}