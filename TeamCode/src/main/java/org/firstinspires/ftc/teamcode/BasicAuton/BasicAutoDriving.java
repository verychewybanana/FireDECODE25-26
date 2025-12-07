package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class BasicAutoDriving {

    private final LinearOpMode opMode;

    public DcMotor frontLeftMotor = null;
    public DcMotor frontRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor backRightMotor = null;

    // TODO: re-tune these for this robot!
    double ticksPerCentimeterDrive  = 17.8;
    double ticksPerCentimeterStrafe = 21.3;
    double ticksPerDegree           = 12;

    int error = 8;

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

        // Use encoders
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set initial target and switch to RUN_TO_POSITION
        frontLeftMotor.setTargetPosition(0);
        frontRightMotor.setTargetPosition(0);
        backLeftMotor.setTargetPosition(0);
        backRightMotor.setTargetPosition(0);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Default power
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

    // Drive forward/backward in cm (positive = forward based on motor directions)
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

    // Strafe left/right in cm (positive = right if your mecanum directions match)
    public void strafe(double cm) {
        int flTarget = frontLeftMotor.getCurrentPosition()  - (int) (cm * ticksPerCentimeterStrafe);
        int frTarget = frontRightMotor.getCurrentPosition() + (int) (cm * ticksPerCentimeterStrafe);
        int blTarget = backLeftMotor.getCurrentPosition()   + (int) (cm * ticksPerCentimeterStrafe);
        int brTarget = backRightMotor.getCurrentPosition()  - (int) (cm * ticksPerCentimeterStrafe);

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

    // Turn in place (positive degrees = one direction, tune as needed)
    public void turn(double degrees) {
        int flTarget = frontLeftMotor.getCurrentPosition()  + (int) (degrees * ticksPerDegree);
        int frTarget = frontRightMotor.getCurrentPosition() - (int) (degrees * ticksPerDegree);
        int blTarget = backLeftMotor.getCurrentPosition()   + (int) (degrees * ticksPerDegree);
        int brTarget = backRightMotor.getCurrentPosition()  - (int) (degrees * ticksPerDegree);

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
}
