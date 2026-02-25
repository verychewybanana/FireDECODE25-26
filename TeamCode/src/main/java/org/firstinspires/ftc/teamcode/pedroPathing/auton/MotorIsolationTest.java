package org.firstinspires.ftc.teamcode.pedroPathing.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name="Motor Isolation Test", group="Pedro Pathing")
public class MotorIsolationTest extends LinearOpMode {

    private static final double PWR = 0.40;
    private static final long RUN_MS = 1400;

    @Override
    public void runOpMode() {
        DcMotorEx lf = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx lb = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx rf = hardwareMap.get(DcMotorEx.class, "rightFront");
        DcMotorEx rb = hardwareMap.get(DcMotorEx.class, "rightBack");

        lf.setDirection(DcMotor.Direction.FORWARD);
        lb.setDirection(DcMotor.Direction.FORWARD);
        rf.setDirection(DcMotor.Direction.REVERSE);
        rb.setDirection(DcMotor.Direction.REVERSE);

        lf.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        lb.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rb.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.setAutoClear(true);
        telemetry.addLine("Motor Isolation Test");
        telemetry.addLine("Press START. Press X to run next motor.");
        telemetry.addLine("Watch which wheel spins + read delta ticks.");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        testOne("LF", lf, lb, rf, rb);
        testOne("LB", lb, lf, rf, rb);
        testOne("RF", rf, lf, lb, rb);
        testOne("RB", rb, lf, lb, rf);

        telemetry.addLine("DONE");
        telemetry.update();

        while (opModeIsActive()) idle();
    }

    private void testOne(String name, DcMotorEx active, DcMotorEx m2, DcMotorEx m3, DcMotorEx m4) {
        telemetry.addLine("Ready: " + name);
        telemetry.addLine("Press X to run");
        telemetry.update();
        while (opModeIsActive() && !gamepad1.x) idle();
        while (opModeIsActive() && gamepad1.x) idle(); // wait for release

        int a0 = active.getCurrentPosition();
        int b0 = m2.getCurrentPosition();
        int c0 = m3.getCurrentPosition();
        int d0 = m4.getCurrentPosition();

        active.setPower(PWR);
        sleep(RUN_MS);
        active.setPower(0);

        int da = active.getCurrentPosition() - a0;
        int db = m2.getCurrentPosition() - b0;
        int dc = m3.getCurrentPosition() - c0;
        int dd = m4.getCurrentPosition() - d0;

        telemetry.addLine("Ran: " + name);
        telemetry.addData("Delta active", da);
        telemetry.addData("Delta others", "%d  %d  %d", db, dc, dd);
        telemetry.update();

        sleep(500);
    }
}