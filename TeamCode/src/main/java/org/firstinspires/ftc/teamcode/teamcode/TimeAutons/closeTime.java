package org.firstinspires.ftc.teamcode.teamcode.TimeAutons;/*
package org.firstinspires.ftc.teamcode.TimeAutons;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;

@Autonomous(name="closeAutonTime", group="TimeAutons")
@Config
public class closeTime extends LinearOpMode {
    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {
        robot = new FireHardwareMap(this.hardwareMap);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        if (opModeIsActive()) {
            robot.armServo.setPower(-0.1);
            robot.clawServo.setPower(0.05);
            robot.frontLeftMotor.setPower(0.645);
            robot.frontRightMotor.setPower(0.5);
            robot.backRightMotor.setPower(0.5);
            robot.backLeftMotor.setPower(0.645);
            sleep(1100);

            pause();

            turnLeft();
            robot.armServo.setPower(-0.1);
            robot.clawServo.setPower(0.05);
            sleep(475);

            pause();

            robot.slideMotor.setPower(0.75);
            robot.armServo.setPower(-0.1);
            robot.clawServo.setPower(0.05);
            sleep(2000);

            pause();

            // move forward
            moveForward();
            robot.slideMotor.setPower(0.75);
            robot.armServo.setPower(-0.1);
            robot.clawServo.setPower(0.05);
            sleep(255);

            pause();

            robot.slideMotor.setPower(-0.05);
            robot.slideMotor.setPower(0.05);
            robot.clawServo.setPower(0.05);
            sleep(800);

            robot.armServo.setPower(-0.05);
            robot.slideMotor.setPower(0.05);
            robot.clawServo.setPower(-0.5);
            sleep(800);

            // move backward
            robot.frontLeftMotor.setPower(-0.56);
            robot.frontRightMotor.setPower(-0.5);
            robot.backLeftMotor.setPower(-0.5);
            robot.backRightMotor.setPower(-0.56);
            sleep(200);

            robot.slideMotor.setPower(-0.55);
            robot.armServo.setPower(-0.1);
            robot.clawServo.setPower(0.12);
            turnRight();
            sleep(840);

            turnRight();
            robot.armServo.setPower(-0.1);
            sleep(500);

            moveForward();
            sleep(1025);

            turnLeft();
            robot.armServo.setPower(-0.05);
            sleep(700);

            moveForward();
            sleep(1025);

            turnLeft();
            robot.armServo.setPower(-0.05);
            sleep(1130);

            moveForward();
            sleep(745);

            robot.armServo.setPower(-0.1);
            sleep(1000);
        }

    }
    public void moveForward() {
        robot.armServo.setPower(-0.05);
        robot.frontLeftMotor.setPower(0.56);
        robot.frontRightMotor.setPower(0.5);
        robot.backRightMotor.setPower(0.5);
        robot.backLeftMotor.setPower(0.56);
    }
    public void turnLeft() {
        robot.armServo.setPower(-0.05);
        robot.frontLeftMotor.setPower(-0.5);
        robot.frontRightMotor.setPower(0.5);
        robot.backRightMotor.setPower(0.5);
        robot.backLeftMotor.setPower(-0.5);
    }
    public void turnRight() {
        robot.armServo.setPower(-0.05);
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
