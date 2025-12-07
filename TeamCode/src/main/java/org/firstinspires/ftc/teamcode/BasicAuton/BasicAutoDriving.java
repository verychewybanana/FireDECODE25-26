package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class BasicAutoDriving {

    public DcMotor frontLeftMotor = null;
    public DcMotor frontRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor backRightMotor = null;

    private LinearOpMode opMode;

    // TODO: tune these again for this bot
    double ticksPerCentimeterDrive = 17.8;
    double ticksPerCentimeterStrafe = 21.3;
    double ticksPerDegree = 12;

    int error = 8;               // encoder tolerance
    double drivePower = 0.5;     // default power for moves

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

        // Set initial targets
        frontLeftMotor.setTargetPosition(0);
        frontRightMotor.setTargetPosition(0);
        backLeftMotor.setTargetPosition(0);
        backRightMotor.setTargetPosition(0);

        // Switch to RUN_TO_POSITION
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set power
        frontLeftMotor.setPower(drivePower);
        frontRightMotor.setPower(drivePower);
        backLeftMotor.setPower(drivePower);
        backRightMotor.setPower(drivePower);
    }

    // Helper: wait until all motors are close enough to target or no longer busy
    private void waitForMotors() {
        while (opMode.opModeIsActive() &&
                (motorsStillBusy())) {

            opMode.idle();
        }
    }

    private boolean motorsStillBusy() {
        return  Math.abs(frontLeftMotor.getTargetPosition()  - frontLeftMotor.getCurrentPosition())  > error ||
                Math.abs(frontRightMotor.getTargetPosition() - frontRightMotor.getCurrentPosition()) > error ||
                Math.abs(backLeftMotor.getTargetPosition()   - backLeftMotor.getCurrentPosition())   > error ||
                Math.abs(backRightMotor.getTargetPosition()  - backRightMotor.getCurrentPosition())  > error;
    }

    public void drive(double cm) {
        int deltaTicks = (int) (cm * ticksPerCentimeterDrive);

        int frontLeftTargetPosition  = frontLeftMotor.getCurrentPosition()  + deltaTicks;
        int frontRightTargetPosition = frontRightMotor.getCurrentPosition() + deltaTicks;
        int backLeftTargetPosition   = backLeftMotor.getCurrentPosition()   + deltaTicks;
        int backRightTargetPosition  = backRightMotor.getCurrentPosition()  + deltaTicks;

        frontLeftMotor.setTargetPosition(frontLeftTargetPosition);
        frontRightMotor.setTargetPosition(frontRightTargetPosition);
        backLeftMotor.setTargetPosition(backLeftTargetPosition);
        backRightMotor.setTargetPosition(backRightTargetPosition);

        // Make sure we’re in RUN_TO_POSITION and have power
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftMotor.setPower(drivePower);
        frontRightMotor.setPower(drivePower);
        backLeftMotor.setPower(drivePower);
        backRightMotor.setPower(drivePower);

        // Block until it finishes
        waitForMotors();
    }

    public void strafe(double cm) {
        int deltaTicks = (int) (cm * ticksPerCentimeterStrafe);

        int frontLeftTargetPosition  = frontLeftMotor.getCurrentPosition()  - deltaTicks;
        int frontRightTargetPosition = frontRightMotor.getCurrentPosition() + deltaTicks;
        int backLeftTargetPosition   = backLeftMotor.getCurrentPosition()   + deltaTicks;
        int backRightTargetPosition  = backRightMotor.getCurrentPosition()  - deltaTicks;

        frontLeftMotor.setTargetPosition(frontLeftTargetPosition);
        frontRightMotor.setTargetPosition(frontRightTargetPosition);
        backLeftMotor.setTargetPosition(backLeftTargetPosition);
        backRightMotor.setTargetPosition(backRightTargetPosition);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftMotor.setPower(drivePower);
        frontRightMotor.setPower(drivePower);
        backLeftMotor.setPower(drivePower);
        backRightMotor.setPower(drivePower);

        waitForMotors();
    }

    public void turn(double degrees) {
        int deltaTicks = (int) (degrees * ticksPerDegree);

        int frontLeftTargetPosition  = frontLeftMotor.getCurrentPosition()  + deltaTicks;
        int frontRightTargetPosition = frontRightMotor.getCurrentPosition() - deltaTicks;
        int backLeftTargetPosition   = backLeftMotor.getCurrentPosition()   + deltaTicks;
        int backRightTargetPosition  = backRightMotor.getCurrentPosition()  - deltaTicks;

        frontLeftMotor.setTargetPosition(frontLeftTargetPosition);
        frontRightMotor.setTargetPosition(frontRightTargetPosition);
        backLeftMotor.setTargetPosition(backLeftTargetPosition);
        backRightMotor.setTargetPosition(backRightTargetPosition);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftMotor.setPower(drivePower);
        frontRightMotor.setPower(drivePower);
        backLeftMotor.setPower(drivePower);
        backRightMotor.setPower(drivePower);

        waitForMotors();
    }
}
