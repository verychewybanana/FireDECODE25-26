package org.firstinspires.ftc.teamcode.pedroPathing.autos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name="Straight Test 2-Stage (Patched)", group="Pedro Pathing")
public class StraightTest extends LinearOpMode {

    private static final double STAGE1_POWER = 0.10;
    private static final double STAGE2_POWER = 0.05;

    private static final double FINAL_DISTANCE = 12.0;
    private static final double STAGE1_DISTANCE = 10.5;

    private static final double X_TOL_IN = 0.30; // accept when within this of target
    private static final double Y_STOP_IN = 2.0;
    private static final double TIMEOUT_SEC = 6.0;

    @Override
    public void runOpMode() {

        Follower follower = Constants.createFollower(hardwareMap);

        DcMotorEx lf = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx lb = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx rf = hardwareMap.get(DcMotorEx.class, "rightFront");
        DcMotorEx rb = hardwareMap.get(DcMotorEx.class, "rightBack");

        lf.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        lb.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rb.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        lf.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        telemetry.setAutoClear(true);
        telemetry.addLine("Straight Test 2-Stage (Patched)");
        telemetry.addData("Stage1 ->", STAGE1_DISTANCE);
        telemetry.addData("Stage2 ->", FINAL_DISTANCE);
        telemetry.addData("Tol X (in)", X_TOL_IN);
        telemetry.addData("MaxPwr stage1", STAGE1_POWER);
        telemetry.addData("MaxPwr stage2", STAGE2_POWER);
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        double startTime = getRuntime();

        Pose startPose = new Pose(0, 0, 0);
        Pose midPose   = new Pose(STAGE1_DISTANCE, 0, 0);
        Pose finalPose = new Pose(FINAL_DISTANCE, 0, 0);

        follower.setStartingPose(startPose);
        follower.update();

        // --- Encoder baseline / step ticks ---
        int lf0 = lf.getCurrentPosition();
        int lb0 = lb.getCurrentPosition();
        int rf0 = rf.getCurrentPosition();
        int rb0 = rb.getCurrentPosition();

        int lastLf = lf0, lastLb = lb0, lastRf = rf0, lastRb = rb0;

        int peakStepLF = 0, peakStepLB = 0, peakStepRF = 0, peakStepRB = 0;

        // --- Power tracking per stage (so we can prove stage2 ran) ---
        double maxAbsStage1 = 0;
        double maxAbsStage2 = 0;

        // --- Simple backwards/hunt detection (small threshold since you said it only backs a little) ---
        double lastX = follower.getPose().getX();
        int backwardsCount = 0;

        // =======================
        // STAGE 1
        // =======================

        PathChain stage1 = follower.pathBuilder()
                .addPath(new BezierLine(startPose, midPose))
                .setConstantHeadingInterpolation(0)
                .build();

        follower.followPath(stage1);

        while (opModeIsActive()) {

            if (getRuntime() - startTime > TIMEOUT_SEC) {
                telemetry.clearAll();
                telemetry.addLine("TIMEOUT STOP (STAGE 1)");
                telemetry.update();
                break;
            }

            follower.update();
            Pose p = follower.getPose();

            // Exit criteria: reached X target (within tolerance)
            if (p.getX() >= STAGE1_DISTANCE - X_TOL_IN) break;

            // Track step ticks
            int lfPos = lf.getCurrentPosition();
            int lbPos = lb.getCurrentPosition();
            int rfPos = rf.getCurrentPosition();
            int rbPos = rb.getCurrentPosition();

            int stepLF = lfPos - lastLf;
            int stepLB = lbPos - lastLb;
            int stepRF = rfPos - lastRf;
            int stepRB = rbPos - lastRb;

            lastLf = lfPos; lastLb = lbPos;
            lastRf = rfPos; lastRb = rbPos;

            peakStepLF = Math.max(peakStepLF, Math.abs(stepLF));
            peakStepLB = Math.max(peakStepLB, Math.abs(stepLB));
            peakStepRF = Math.max(peakStepRF, Math.abs(stepRF));
            peakStepRB = Math.max(peakStepRB, Math.abs(stepRB));

            // Clamp stage1
            clamp(lf, lb, rf, rb, STAGE1_POWER);
            maxAbsStage1 = Math.max(maxAbsStage1, maxMag(lf, lb, rf, rb));

            // Safety Y
            if (Math.abs(p.getY()) > Y_STOP_IN) {
                telemetry.clearAll();
                telemetry.addLine("Y SAFETY STOP (STAGE 1)");
                telemetry.addData("Pose", "X %.2f Y %.2f H %.1f",
                        p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
                telemetry.update();
                break;
            }

            // Backwards/hunt detection (needs a few consecutive drops to avoid false triggers)
            if (p.getX() < lastX - 0.05) backwardsCount++;
            else backwardsCount = 0;

            if (backwardsCount >= 5) { // ~5 loops * 20ms = 100ms of backwards
                telemetry.clearAll();
                telemetry.addLine("HUNT/BACKWARDS STOP (STAGE 1)");
                telemetry.addData("Pose", "X %.2f Y %.2f H %.1f",
                        p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
                telemetry.update();
                break;
            }
            lastX = p.getX();

            telemetry.addData("Stage", "1");
            telemetry.addData("Pose", "X %.2f Y %.2f", p.getX(), p.getY());
            telemetry.update();

            sleep(20);
        }

        // =======================
        // STAGE 2 (CREEP)
        // =======================

        // Reset backwards detector for stage2
        backwardsCount = 0;
        lastX = follower.getPose().getX();

        Pose stage2Start = follower.getPose();

        PathChain stage2 = follower.pathBuilder()
                .addPath(new BezierLine(stage2Start, finalPose))
                .setConstantHeadingInterpolation(0)
                .build();

        telemetry.clearAll();
        telemetry.addLine("STAGE 2 STARTING (CREEP)");
        telemetry.update();
        sleep(250);

        follower.followPath(stage2);

        while (opModeIsActive()) {

            if (getRuntime() - startTime > TIMEOUT_SEC) {
                telemetry.clearAll();
                telemetry.addLine("TIMEOUT STOP (STAGE 2)");
                telemetry.update();
                break;
            }

            follower.update();
            Pose p = follower.getPose();

            // Exit criteria: reached final X (within tolerance)
            if (p.getX() >= FINAL_DISTANCE - X_TOL_IN) break;

            // Clamp stage2
            clamp(lf, lb, rf, rb, STAGE2_POWER);
            maxAbsStage2 = Math.max(maxAbsStage2, maxMag(lf, lb, rf, rb));

            // Safety Y
            if (Math.abs(p.getY()) > Y_STOP_IN) {
                telemetry.clearAll();
                telemetry.addLine("Y SAFETY STOP (STAGE 2)");
                telemetry.addData("Pose", "X %.2f Y %.2f H %.1f",
                        p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
                telemetry.update();
                break;
            }

            // Backwards/hunt detection (small back-and-forth is ok; we only stop if it persists)
            if (p.getX() < lastX - 0.05) backwardsCount++;
            else backwardsCount = 0;

            if (backwardsCount >= 6) { // allow a tiny back wiggle
                telemetry.clearAll();
                telemetry.addLine("HUNT/BACKWARDS STOP (STAGE 2)");
                telemetry.addData("Pose", "X %.2f Y %.2f H %.1f",
                        p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
                telemetry.update();
                break;
            }
            lastX = p.getX();

            telemetry.addData("Stage", "2");
            telemetry.addData("Pose", "X %.2f Y %.2f", p.getX(), p.getY());
            telemetry.update();

            sleep(20);
        }

        stopAll(lf, lb, rf, rb);

        // =======================
        // DONE SCREEN
        // =======================

        while (opModeIsActive() && !gamepad1.b) {

            follower.update(); // make pose current
            Pose p = follower.getPose();

            int dLF = lf.getCurrentPosition() - lf0;
            int dLB = lb.getCurrentPosition() - lb0;
            int dRF = rf.getCurrentPosition() - rf0;
            int dRB = rb.getCurrentPosition() - rb0;

            telemetry.addLine("DONE (press B to exit)");
            telemetry.addData("Final Pose", "X %.2f Y %.2f H %.1fdeg",
                    p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
            telemetry.addData("Final dTicks", "LF %d LB %d RF %d RB %d",
                    dLF, dLB, dRF, dRB);
            telemetry.addData("Peak stepTicks", "LF %d LB %d RF %d RB %d",
                    peakStepLF, peakStepLB, peakStepRF, peakStepRB);
            telemetry.addData("MaxAbsPwr Stage1", "%.2f", maxAbsStage1);
            telemetry.addData("MaxAbsPwr Stage2", "%.2f", maxAbsStage2);
            telemetry.update();

            sleep(50);
        }
    }

    private void clamp(DcMotorEx lf, DcMotorEx lb, DcMotorEx rf, DcMotorEx rb, double max) {
        double maxMag = maxMag(lf, lb, rf, rb);
        if (maxMag > max && maxMag > 1e-6) {
            double scale = max / maxMag;
            lf.setPower(lf.getPower() * scale);
            lb.setPower(lb.getPower() * scale);
            rf.setPower(rf.getPower() * scale);
            rb.setPower(rb.getPower() * scale);
        }
    }

    private double maxMag(DcMotorEx lf, DcMotorEx lb, DcMotorEx rf, DcMotorEx rb) {
        return Math.max(
                Math.max(Math.abs(lf.getPower()), Math.abs(lb.getPower())),
                Math.max(Math.abs(rf.getPower()), Math.abs(rb.getPower()))
        );
    }

    private void stopAll(DcMotorEx lf, DcMotorEx lb, DcMotorEx rf, DcMotorEx rb) {
        lf.setPower(0);
        lb.setPower(0);
        rf.setPower(0);
        rb.setPower(0);
    }
}