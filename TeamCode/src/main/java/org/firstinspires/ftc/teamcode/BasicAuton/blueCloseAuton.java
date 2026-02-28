package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous(name = "Blue Close Auton", group = "Autonomous")
public class blueCloseAuton extends LinearOpMode {

    private FireHardwareMap HW;

    double ticksPerInch = 42.2;
    double ticksPerInchStrafe = 50.6;

    @Override
    public void runOpMode() {

        HW = new FireHardwareMap(this.hardwareMap);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveTargets(0, 0, 0, 0);
        setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();
        if (!opModeIsActive()) return;

        HW.outtakeMotor.setPower(0.59);

        strafe(8);           // was -8
        drive(-30, 0.75);

        //pulseShoot(1600, 0.6, 200, 300);
        startIntakeAndMid(0.6);
        sleep(250);
        stopIntakeAndMid();
        sleep(300);
        //HW.outtakeMotor.setPower(0.54);
        startIntakeAndMid(0.6);
        sleep(400);
        stopIntakeAndMid();
        sleep(300);
        HW.outtakeMotor.setPower(0.62);
        startIntakeAndMid(0.7);
        sleep(1000);
        HW.outtakeMotor.setPower(0);

        HW.outtakeMotor.setPower(0);

        drive(-10, 0.5);

        turn(-48.5);         // was 48.5

        strafe(-8.5);        // was 8.5

        startIntakeAndMid(0.6);
        drive(30, 0.7);
        drive(15, 0.4);
        sleep(750);
        reverseIntakeAndMid();
        sleep(350);
        stopIntakeAndMid();

        drive(-37, 0.75);
        HW.outtakeMotor.setPower(0.63);
        turn(47);            // was -47
        sleep(500);

        //pulseShoot(1600, 0.6, 200, 300);
        drive(5, 0.75);
        startIntakeAndMid(0.6);
        sleep(250);
        stopIntakeAndMid();
        sleep(300);
        //HW.outtakeMotor.setPower(0.59);
        startIntakeAndMid(0.6);
        sleep(400);
        stopIntakeAndMid();
        sleep(300);
        HW.outtakeMotor.setPower(0.65);
        startIntakeAndMid(0.7);
        sleep(1300);
        HW.outtakeMotor.setPower(0);

        drive(-10, 0.4);
        turn(-51.5);         // was 51.5

        strafe(-20.35);      // was 20.35

        startIntakeAndMid(0.6);
        drive(48, 0.7);
        drive(10, 0.4);
        sleep(1250);
        reverseIntakeAndMid();
        sleep(350);
        stopIntakeAndMid();

        drive(-35, 0.75);
        strafe(30);          // was -30

        HW.outtakeMotor.setPower(0.62);
        turn(58);            // was -58
        sleep(500);

        //pulseShoot(1500, 0.6, 200, 300);
        //drive(3, 0.75);
        startIntakeAndMid(0.6);
        sleep(250);
        stopIntakeAndMid();
        sleep(300);
        //HW.outtakeMotor.setPower(0.58);
        startIntakeAndMid(0.6);
        sleep(400);
        stopIntakeAndMid();
        sleep(300);
        HW.outtakeMotor.setPower(0.62);
        startIntakeAndMid(0.7);
        sleep(1100);
        HW.outtakeMotor.setPower(0);

        setDrivePower(0);
        drive(-10, 0.75);

        strafe(-10);         // was 10
        turn(-50);           // was 50
    }

    // ===================== DRIVE =====================

    private void drive(double inches, double power) {
        int ticks = (int) (inches * ticksPerInch);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveTargets(ticks, ticks, ticks, ticks);
        setDrivePower(power);
        waitForDrive();
        setDrivePower(0);
    }

    private void strafe(double inches) {
        int ticks = (int) (inches * ticksPerInchStrafe);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveTargets(ticks, -ticks, -ticks, ticks);
        setDrivePower(0.7);
        waitForDrive();
        setDrivePower(0);
    }

    private void turn(double degrees) {
        setDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        HW.imu.resetYaw();
        double target = -degrees;
        double absDeg = Math.abs(degrees);
        double turnPower = 0.6;
        double rampZone = absDeg * 0.33;
        double coarseTolerance = 1.0;
        double fineTolerance = 0.3;
        long timeout = (long)(absDeg * 33) + 1000;
        long startTime = System.currentTimeMillis();

        while (opModeIsActive() && (System.currentTimeMillis() - startTime) < timeout) {
            double current = HW.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double error = target - current;

            if (Math.abs(error) < coarseTolerance) break;

            double power;
            if (Math.abs(error) > rampZone) {
                power = turnPower;
            } else {
                power = Math.max(0.15, Math.abs(error) / rampZone * turnPower);
            }

            if (error > 0) {
                HW.leftFront.setPower(-power);
                HW.rightFront.setPower(power);
                HW.leftBack.setPower(-power);
                HW.rightBack.setPower(power);
            } else {
                HW.leftFront.setPower(power);
                HW.rightFront.setPower(-power);
                HW.leftBack.setPower(power);
                HW.rightBack.setPower(-power);
            }

            idle();
        }

        setDrivePower(0);
        sleep(250);

        startTime = System.currentTimeMillis();
        while (opModeIsActive() && (System.currentTimeMillis() - startTime) < 1500) {
            double current = HW.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double error = target - current;

            if (Math.abs(error) < fineTolerance) break;

            double nudgePower = 0.12;

            if (error > 0) {
                HW.leftFront.setPower(-nudgePower);
                HW.rightFront.setPower(nudgePower);
                HW.leftBack.setPower(-nudgePower);
                HW.rightBack.setPower(nudgePower);
            } else {
                HW.leftFront.setPower(nudgePower);
                HW.rightFront.setPower(-nudgePower);
                HW.leftBack.setPower(nudgePower);
                HW.rightBack.setPower(-nudgePower);
            }

            idle();
        }

        setDrivePower(0);
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void waitForDrive() {
        while (opModeIsActive()
                && HW.leftFront.isBusy()
                && HW.rightFront.isBusy()
                && HW.leftBack.isBusy()
                && HW.rightBack.isBusy()) {
            idle();
        }
    }

    // ===================== MECHANISMS =====================

    // Pulses intake/mid to feed one ball at a time, letting outtake recover between shots
    private void pulseShoot(long totalMs, double feedPower, long feedMs, long recoveryMs) {
        long startTime = System.currentTimeMillis();
        while (opModeIsActive() && (System.currentTimeMillis() - startTime) < totalMs) {
            startIntakeAndMid(feedPower);
            sleep(feedMs);
            stopIntakeAndMid();
            if ((System.currentTimeMillis() - startTime) < totalMs) {
                sleep(recoveryMs);
            }
        }
        stopIntakeAndMid();
    }

    private void startIntakeAndMid(double power) {
        HW.intakeMotor.setPower(-power * 1.5);
        HW.midMotor1.setPower(-power);
        HW.midMotor2.setPower(-power);
    }

    private void reverseIntakeAndMid() {
        HW.intakeMotor.setPower(0.35);
        HW.midMotor1.setPower(0.175);
        HW.midMotor2.setPower(0.175);
    }

    private void stopIntakeAndMid() {
        HW.intakeMotor.setPower(0);
        HW.midMotor1.setPower(0);
        HW.midMotor2.setPower(0);
    }

    // ===================== UTILITY =====================

    private void setDriveMode(DcMotor.RunMode mode) {
        HW.leftFront.setMode(mode);
        HW.rightFront.setMode(mode);
        HW.leftBack.setMode(mode);
        HW.rightBack.setMode(mode);
    }

    private void setDriveTargets(int fl, int fr, int bl, int br) {
        HW.leftFront.setTargetPosition(fl);
        HW.rightFront.setTargetPosition(fr);
        HW.leftBack.setTargetPosition(bl);
        HW.rightBack.setTargetPosition(br);
        setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void setDrivePower(double power) {
        HW.leftFront.setPower(power);
        HW.rightFront.setPower(power);
        HW.leftBack.setPower(power);
        HW.rightBack.setPower(power);
    }
}
/*

 */