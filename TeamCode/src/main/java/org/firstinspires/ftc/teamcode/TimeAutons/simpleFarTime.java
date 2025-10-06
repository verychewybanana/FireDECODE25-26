package org.firstinspires.ftc.teamcode.TimeAutons;/*
package org.firstinspires.ftc.teamcode.TimeAutons;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;

@Autonomous(name="simpleFarTime", group="TimeAutons")
@Config
public class simpleFarTime extends LinearOpMode {
    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {
        robot = new FireHardwareMap(this.hardwareMap);


        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        if (opModeIsActive()) {
//            robot.clawServo.setPower(0.5);
//            turnRight();
//            sleep(300);
//
//            pause();
//
//            robot.clawServo.setPower(0.1);
//            moveForward();
//            sleep(1000);
//
//            pause();
//
//            robot.clawServo.setPower(0.1);
//            turnLeft();
//            sleep(375);
//
//            pause();
//
//            robot.clawServo.setPower(0.1);
//            moveForward();
//            sleep(1300);
//
//            pause();
//
//            robot.slideMotor.setPower(0.5);
//            robot.armServo.setPower(-0.05);
//            robot.clawServo.setPower(0.1);
//            sleep(850);
//
//            pause();
//
//            robot.slideMotor.setPower(0.75);
//            robot.armServo.setPower(-0.1);
//            robot.clawServo.setPower(1);
//            sleep(2000);
//
//            pause();
//
//            // move forward
//            moveForward();
//            robot.slideMotor.setPower(0.75);
//            robot.clawServo.setPower(0.1);
//            sleep(255);
//
//            pause();
//
//            robot.slideMotor.setPower(-0.05);
//            robot.slideMotor.setPower(0.05);
//            robot.clawServo.setPower(0.05);
//            sleep(800);
//
//            robot.armServo.setPower(-0.05);
//            robot.slideMotor.setPower(0.05);
//            robot.clawServo.setPower(-0.5);
//            sleep(800);
//
//            // move backward
//            robot.frontLeftMotor.setPower(-0.56);
//            robot.frontRightMotor.setPower(-0.5);
//            robot.backLeftMotor.setPower(-0.5);
//            robot.backRightMotor.setPower(-0.56);
//            sleep(200);
//
//            pause();
//
//            robot.slideMotor.setPower(-0.9);
//            robot.clawServo.setPower(0.15);
//            robot.armServo.setPower(-0.1);
//            sleep(1300);
//
//            moveForward();
//            sleep(100);

            robot.clawServo.setPower(0.2);
            robot.frontLeftMotor.setPower(-0.56);
            robot.frontRightMotor.setPower(-0.5);
            robot.backLeftMotor.setPower(-0.5);
            robot.backRightMotor.setPower(-0.56);
            sleep(1600);

            pause();

            robot.clawServo.setPower(0.2);
            robot.armServo.setPower(-0.05);
            sleep(1000);

            robot.clawServo.setPower(0.1);
            robot.armServo.setPower(-0.05);
            sleep(1000);
        }
    }

    public void moveForward() {
        robot.armServo.setPower(-0.15);
        robot.frontLeftMotor.setPower(0.5);
        robot.frontRightMotor.setPower(0.5);
        robot.backRightMotor.setPower(0.5);
        robot.backLeftMotor.setPower(0.5);
    }
    public void turnLeft() {
        robot.armServo.setPower(-0.15);
        robot.frontLeftMotor.setPower(-0.5);
        robot.frontRightMotor.setPower(0.5);
        robot.backRightMotor.setPower(0.5);
        robot.backLeftMotor.setPower(-0.5);
    }
    public void turnRight() {
        robot.armServo.setPower(-0.15);
        robot.frontLeftMotor.setPower(0.5);
        robot.frontRightMotor.setPower(-0.5);
        robot.backRightMotor.setPower(-0.5);
        robot.backLeftMotor.setPower(0.5);
    }

    public void pause() {
        robot.frontLeftMotor.setPower(0.0);
        robot.frontRightMotor.setPower(0.0);
        robot.backLeftMotor.setPower(0.0);
        robot.backRightMotor.setPower(0.0);

        sleep(200);
    }


}

 */