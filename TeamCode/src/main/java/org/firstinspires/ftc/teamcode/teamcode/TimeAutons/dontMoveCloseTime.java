package org.firstinspires.ftc.teamcode.teamcode.TimeAutons;/*
package org.firstinspires.ftc.teamcode.TimeAutons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.FireHardwareMap;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

@Autonomous(name="dontMoveFarTime", group="TimeAutons")
@Disabled
public class dontMoveCloseTime extends LinearOpMode {
    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {
        robot = new FireHardwareMap(this.hardwareMap);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        if (opModeIsActive()) {
            robot.frontLeftMotor.setPower(0.5);
            robot.frontRightMotor.setPower(0.5);
            robot.backLeftMotor.setPower(0.5);
            robot.backRightMotor.setPower(0.5);

            sleep(1400);

            // stop
            robot.frontLeftMotor.setPower(0.0);
            robot.frontRightMotor.setPower(0.0);
            robot.backLeftMotor.setPower(0.0);
            robot.backRightMotor.setPower(0.0);

            sleep(200);

//            robot.intakeMotor.setPower(0.0);
            robot.frontLeftMotor.setPower(-0.5);
            robot.frontRightMotor.setPower(-0.5);
            robot.backLeftMotor.setPower(-0.5);
            robot.backRightMotor.setPower(-0.5);

            sleep(1200);

            // stop
            robot.frontLeftMotor.setPower(0.0);
            robot.frontRightMotor.setPower(0.0);
            robot.backLeftMotor.setPower(0.0);
            robot.backRightMotor.setPower(0.0);

            sleep(200);

        }

    }
}

 */