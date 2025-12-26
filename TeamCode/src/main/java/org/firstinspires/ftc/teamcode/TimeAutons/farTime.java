package org.firstinspires.ftc.teamcode.TimeAutons;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;

@Autonomous(name="redTime", group="TimeAutons")
@Config
public class farTime extends LinearOpMode {
    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {
        robot = new FireHardwareMap(this.hardwareMap);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        if (opModeIsActive()) {
            moveForward(-0.8);
            robot.outTakeRight.setPower(0.59);
            robot.outTakeLeft.setPower(0.59);
            sleep(150);
            pause();
            //manual strafe start
//            robot.frontLeftMotor.setPower(0.8);
//            robot.backLeftMotor.setPower(-0.8);
//            robot.backRightMotor.setPower(0.8);
//            robot.frontRightMotor.setPower(-0.8);
//            sleep(225);
//            pause();
            // manual strafe end
            // manual turn start
            robot.frontLeftMotor.setPower(0.8);
            robot.backLeftMotor.setPower(0.8);
            robot.backRightMotor.setPower(-0.8);
            robot.frontRightMotor.setPower(-0.8);
            sleep(135);
            pause();
            sleep(1000);
            // manual turn end
            robot.midMotor.setPower(-0.3);
            sleep(3000);
            robot.outTakeRight.setPower(0);
            robot.outTakeLeft.setPower(0);
            robot.midMotor.setPower(0);
            moveForward(-0.4);
            sleep(1000);
        }


    }
    public void moveForward(double power) {
        robot.frontLeftMotor.setPower(power);
        robot.frontRightMotor.setPower(power);
        robot.backRightMotor.setPower(power);
        robot.backLeftMotor.setPower(power);
    }
    public void turnLeft(double power) {
        robot.frontLeftMotor.setPower(-power);
        robot.frontRightMotor.setPower(power);
        robot.backRightMotor.setPower(power);
        robot.backLeftMotor.setPower(power);
    }
    public void turnRight(double power) {
        robot.frontLeftMotor.setPower(power);
        robot.frontRightMotor.setPower(-power);
        robot.backRightMotor.setPower(-power);
        robot.backLeftMotor.setPower(power);
    }

    public void pause() {
        robot.frontLeftMotor.setPower(0.0);
        robot.frontRightMotor.setPower(0.0);
        robot.backLeftMotor.setPower(0.0);
        robot.backRightMotor.setPower(0.0);

        sleep(200);
    }
}
