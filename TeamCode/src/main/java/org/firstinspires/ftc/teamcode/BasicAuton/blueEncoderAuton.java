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
            bad.driveTime(0.5, 1500); // Power 0.5, Time 500ms
            robot.outTakeRight.setPower(0.65);
            robot.outTakeLeft.setPower(0.65);
            sleep(2000);
            robot.midMotor.setPower(-0.5);
            robot.intakeMotor.setPower(-0.75);
            sleep(3000);
            robot.outTakeRight.setPower(0.45);
            robot.outTakeLeft.setPower(0.45);
            sleep(1000);
            robot.pusherServo.setPosition(-0.16);
            sleep(450);
            robot.pusherServo.setPosition(1);
            sleep(450);
            robot.intakeMotor.setPower(-0.75);
            robot.midMotor.setPower(-0.5);
            sleep(2000);
            robot.pusherServo.setPosition(-0.16);
            sleep(300);
            robot.pusherServo.setPosition(1);
            sleep(300);
            robot.pusherServo.setPosition(-1);
            //bad.drive(150);
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
