package org.firstinspires.ftc.teamcode.pedroPathing.localization;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp(name = "Localization Test", group = "Pedro Pathing")
public class LocalizationTest extends LinearOpMode {

    private Follower follower;

    private DcMotorEx leftFront, leftBack, rightFront, rightBack;

    // Baseline encoder ticks for "zeroing" without STOP_AND_RESET_ENCODER
    private int baseLF, baseLB, baseRF, baseRB;
    private boolean hasBaseline = false;

    @Override
    public void runOpMode() {
        follower = Constants.createFollower(hardwareMap);

        leftFront  = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack   = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBack  = hardwareMap.get(DcMotorEx.class, "rightBack");

        // Easier to push / smoother test
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        leftBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        rightBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        // Ensure motors accept direct power commands (when we drive)
        leftFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        follower.setStartingPose(new Pose(0, 0, 0));
        follower.update();

        telemetry.addLine("Localization Test (Pedro)");
        telemetry.addLine("Default = PUSH TEST (motors off)");
        telemetry.addLine("Hold RIGHT BUMPER = JOYSTICK DRIVE (manual mecanum)");
        telemetry.addLine("Press A = capture baseline ticks + zero pose (safe reset)");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Safe reset: capture baseline ticks (do NOT reset motor encoders)
            if (gamepad1.a) {
                baseLF = leftFront.getCurrentPosition();
                baseLB = leftBack.getCurrentPosition();
                baseRF = rightFront.getCurrentPosition();
                baseRB = rightBack.getCurrentPosition();
                hasBaseline = true;

                follower.setStartingPose(new Pose(0, 0, 0));
                follower.update(); // sync immediately
            }

            // --- Drive motors manually only when RB is held ---
            if (gamepad1.right_bumper) {
                double axial   = -gamepad1.left_stick_y;       // forward
                double lateral =  gamepad1.left_stick_x * 1.1; // strafe
                double yaw     =  gamepad1.right_stick_x * 0.3;      // rotate

                double lf = axial + lateral + yaw;
                double rf = axial - lateral - yaw;
                double lb = axial - lateral + yaw;
                double rb = axial + lateral - yaw;

                // Normalize
                double max = Math.max(Math.abs(lf), Math.abs(rf));
                max = Math.max(max, Math.abs(lb));
                max = Math.max(max, Math.abs(rb));
                if (max > 1.0) {
                    lf /= max; rf /= max; lb /= max; rb /= max;
                }

                leftFront.setPower(lf);
                rightFront.setPower(rf);
                leftBack.setPower(lb);
                rightBack.setPower(rb);
            } else {
                // PUSH TEST mode: motors off
                leftFront.setPower(0);
                rightFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
            }

            // Update Pedro localization
            follower.update();
            Pose pose = follower.getPose();

            // Raw ticks
            int lfTicks = leftFront.getCurrentPosition();
            int lbTicks = leftBack.getCurrentPosition();
            int rfTicks = rightFront.getCurrentPosition();
            int rbTicks = rightBack.getCurrentPosition();

            // Delta ticks since baseline
            int dLF = hasBaseline ? (lfTicks - baseLF) : 0;
            int dLB = hasBaseline ? (lbTicks - baseLB) : 0;
            int dRF = hasBaseline ? (rfTicks - baseRF) : 0;
            int dRB = hasBaseline ? (rbTicks - baseRB) : 0;

            // Useful aggregate for straight forward pushes
            double avgForwardTicks = (dLF + dLB + dRF + dRB) / 4.0;

            telemetry.addData("Mode", gamepad1.right_bumper ? "JOYSTICK DRIVE" : "PUSH TEST");
            telemetry.addData("Baseline set", hasBaseline);

            telemetry.addLine("=== RAW ENCODER TICKS ===");
            telemetry.addData("LF", lfTicks);
            telemetry.addData("LB", lbTicks);
            telemetry.addData("RF", rfTicks);
            telemetry.addData("RB", rbTicks);

            telemetry.addLine("=== DELTA TICKS (press A to zero) ===");
            telemetry.addData("dLF", dLF);
            telemetry.addData("dLB", dLB);
            telemetry.addData("dRF", dRF);
            telemetry.addData("dRB", dRB);
            telemetry.addData("avgForwardTicks", "%.1f", avgForwardTicks);

            telemetry.addLine("=== POSE ===");
            telemetry.addData("X", "%.2f", pose.getX());
            telemetry.addData("Y", "%.2f", pose.getY());
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.getHeading()));

            telemetry.update();
        }
    }
}