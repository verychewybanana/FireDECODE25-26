package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous(name = "Tuning Constants", group = "Autonomous")
public class TuneConstants extends LinearOpMode {

    private FireHardwareMap HW;

    double ticksPerInch       = 42.2;
    double ticksPerInchStrafe = 50.6;  // tune this

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

        // Uncomment one at a time to tune:
        //drive(-30);
        //turn(45);
        strafe(12);  // positive = right, negative = left
    }

    // ===================== DRIVE =====================

    private void drive(double inches) {
        int ticks = (int) (inches * ticksPerInch);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveTargets(ticks, ticks, ticks, ticks);
        setDrivePower(0.5);
        waitForDrive();
        setDrivePower(0);
    }

    private void strafe(double inches) {
        int ticks = (int) (inches * ticksPerInchStrafe);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Mecanum strafe: FL+, FR-, BL-, BR+
        setDriveTargets(ticks, -ticks, -ticks, ticks);
        setDrivePower(0.4);
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