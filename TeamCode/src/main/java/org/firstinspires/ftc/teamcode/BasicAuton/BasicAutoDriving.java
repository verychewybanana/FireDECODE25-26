package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class BasicAutoDriving {

    private final LinearOpMode opMode;

    public DcMotor frontLeftMotor = null;
    public DcMotor frontRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor backRightMotor = null;

    // Tuning variables
    double ticksPerCentimeterDrive  = 17.8;
    double ticksPerCentimeterStrafe = 21.3;
    double ticksPerDegree           = 12;

    public BasicAutoDriving(LinearOpMode opMode,
                            DcMotor frontLeft,
                            DcMotor frontRight,
                            DcMotor backLeft,
                            DcMotor backRight) {

        this.opMode = opMode;

        frontLeftMotor  = frontLeft;
        frontRightMotor = frontRight;
        backLeftMotor   = backLeft;
        backRightMotor  = backRight;

        // Reset encoders
        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Standard setup (Auton normally uses encoders)
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeftMotor.setTargetPosition(0);
        frontRightMotor.setTargetPosition(0);
        backLeftMotor.setTargetPosition(0);
        backRightMotor.setTargetPosition(0);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftMotor.setPower(0.5);
        frontRightMotor.setPower(0.5);
        backLeftMotor.setPower(0.5);
        backRightMotor.setPower(0.5);
    }

    private boolean anyBusy() {
        return frontLeftMotor.isBusy()
                || frontRightMotor.isBusy()
                || backLeftMotor.isBusy()
                || backRightMotor.isBusy();
    }

    // ORIGINAL ENCODER DRIVE (Keep this!)
    public void drive(double cm) {
        int flTarget = frontLeftMotor.getCurrentPosition()  + (int) (cm * ticksPerCentimeterDrive);
        int frTarget = frontRightMotor.getCurrentPosition() + (int) (cm * ticksPerCentimeterDrive);
        int blTarget = backLeftMotor.getCurrentPosition()   + (int) (cm * ticksPerCentimeterDrive);
        int brTarget = backRightMotor.getCurrentPosition()  + (int) (cm * ticksPerCentimeterDrive);

        frontLeftMotor.setTargetPosition(flTarget);
        frontRightMotor.setTargetPosition(frTarget);
        backLeftMotor.setTargetPosition(blTarget);
        backRightMotor.setTargetPosition(brTarget);

        frontLeftMotor.setPower(0.5);
        frontRightMotor.setPower(0.5);
        backLeftMotor.setPower(0.5);
        backRightMotor.setPower(0.5);

        while (opMode.opModeIsActive() && anyBusy()) {
            opMode.idle();
        }
    }

    // --- NEW TIME-BASED DRIVE METHOD ---
    public void driveTime(double power, long timeMs) {
        // 1. Force motors to raw power mode (ignores encoders)
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // 2. Set Power
        frontLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backLeftMotor.setPower(power);
        backRightMotor.setPower(power);

        // 3. Wait
        opMode.sleep(timeMs);

        // 4. Stop
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    // ORIGINAL STRAFE
    public void strafe(double cm) {
        int flTarget = frontLeftMotor.getCurrentPosition()  + (int) (cm * ticksPerCentimeterStrafe);
        int frTarget = frontRightMotor.getCurrentPosition() + (int) (cm * ticksPerCentimeterStrafe * -1);
        int blTarget = backLeftMotor.getCurrentPosition()   + (int) (cm * ticksPerCentimeterStrafe * -1);
        int brTarget = backRightMotor.getCurrentPosition()  + (int) (cm * ticksPerCentimeterStrafe);

        frontLeftMotor.setTargetPosition(flTarget);
        frontRightMotor.setTargetPosition(frTarget);
        backLeftMotor.setTargetPosition(blTarget);
        backRightMotor.setTargetPosition(brTarget);

        frontLeftMotor.setPower(0.25);
        frontRightMotor.setPower(0.25);
        backLeftMotor.setPower(0.25);
        backRightMotor.setPower(0.25);

        while (opMode.opModeIsActive() && anyBusy()) {
            opMode.idle();
        }
    }

    // ORIGINAL TURN
    public void turn(double degrees) {
        double p = degrees * ticksPerDegree;

        int flTarget = frontLeftMotor.getCurrentPosition()  - (int) p;
        int frTarget = frontRightMotor.getCurrentPosition() + (int) p;
        int blTarget = backLeftMotor.getCurrentPosition()   - (int) p;
        int brTarget = backRightMotor.getCurrentPosition()  + (int) p;

        frontLeftMotor.setTargetPosition(flTarget);
        frontRightMotor.setTargetPosition(frTarget);
        backLeftMotor.setTargetPosition(blTarget);
        backRightMotor.setTargetPosition(brTarget);

        frontLeftMotor.setPower(0.25);
        frontRightMotor.setPower(0.25);
        backLeftMotor.setPower(0.25);
        backRightMotor.setPower(0.25);

        while (opMode.opModeIsActive() && anyBusy()) {
            opMode.idle();
        }
    }
}
