package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;
@Autonomous(name="redEncoderAuton", group="Auton")
public class redEncoderAuton extends LinearOpMode {

    FireHardwareMap robot = null;
    private static final double PUSHER_POS_PUSH = 1;
    private static final double PUSHER_POS_REST = 0.0;

    @Override
    public void runOpMode() {

        robot = new FireHardwareMap(hardwareMap);
        robot.pusherServo.setPosition(PUSHER_POS_REST);
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
            robot.outTakeRight.setPower(0.4625);
            robot.outTakeLeft.setPower(0.4625);
            bad.driveTime(0.3, 1750); // Power 0.5, Time 500ms
            robot.midMotor.setPower(-0.5);
            robot.intakeMotor.setPower(-0.75);
            sleep(600);
            robot.midMotor.setPower(0);
            robot.intakeMotor.setPower(0);
            robot.outTakeRight.setPower(0.44);
            robot.outTakeLeft.setPower(0.44);
            bad.driveTime(0.3, 750);
            sleep(500);
            sleep(500);
            robot.midMotor.setPower(-0.5);
            robot.intakeMotor.setPower(-0.75);
            sleep(1000);
            robot.outTakeRight.setPower(0.38);
            robot.outTakeLeft.setPower(0.38);
            sleep(2000);
            servoFlip(1000);
            robot.frontLeftMotor.setPower(-0.5);
            robot.frontRightMotor.setPower(0.5);
            robot.backLeftMotor.setPower(0.5);
            robot.backRightMotor.setPower(-0.5);
            sleep(1000);


            //bad.drive(150);
        }
    }

    private void intake(double power) {
        robot.intakeMotor.setPower(power);
        sleep(2000);

        robot.intakeMotor.setPower(0);
    }

    private void servoFlip(long holdMs) {
        robot.pusherServo.setPosition(PUSHER_POS_PUSH);  // "press"
        sleep(holdMs);
        robot.pusherServo.setPosition(PUSHER_POS_REST);  // "release"
        sleep(holdMs); // small settle time (tune)
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