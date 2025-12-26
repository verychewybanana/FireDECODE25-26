package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;
@Autonomous(name="blueEncoderAuton", group="Auton")
public class blueEncoderAuton extends LinearOpMode {

    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {

        robot = new FireHardwareMap(hardwareMap);

        BasicAutoDriving bad = new BasicAutoDriving(
                this,   // REQUIRED
                robot.frontLeftMotor,
                robot.frontRightMotor,
                robot.backLeftMotor,
                robot.backRightMotor
        );

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            bad.drive(-150);
            robot.outTakeRight.setPower(0.57);
            robot.outTakeLeft.setPower(0.57);
            sleep(200);
            //manual strafe start
            robot.frontLeftMotor.setPower(-0.8);
            robot.backLeftMotor.setPower(0.8);
            robot.backRightMotor.setPower(-0.8);
            robot.frontRightMotor.setPower(0.8);
            sleep(200);
            // manual strafe end
            // manual turn start
            robot.frontLeftMotor.setPower(0.8);
            robot.backLeftMotor.setPower(0.8);
            robot.backRightMotor.setPower(-0.8);
            robot.frontRightMotor.setPower(-0.8);
            sleep(100);
            // manual turn end
            robot.midMotor.setPower(0.25);
            sleep(1000);
            robot.outTakeRight.setPower(0);
            robot.outTakeLeft.setPower(0);
            robot.midMotor.setPower(0);
            bad.drive(-300);
            sleep(1000);
        }
    }

    private void intake(double power) {
        robot.intakeMotor.setPower(power);
        sleep(2000);

        robot.intakeMotor.setPower(0);
    }

    public void forward(double power) {
        robot.frontLeftMotor.setPower(power);
        robot.frontRightMotor.setPower(power);
        robot.backLeftMotor.setPower(power);
        robot.backRightMotor.setPower(power);
    }

    private void outtake(double power) {
        robot.midMotor.setPower(0.5);
        sleep(2000);

        robot.outTakeLeft.setPower(power);
        robot.outTakeRight.setPower(power);
        sleep(2000);

        robot.outTakeLeft.setPower(0);
        robot.outTakeRight.setPower(0);

    }




}
