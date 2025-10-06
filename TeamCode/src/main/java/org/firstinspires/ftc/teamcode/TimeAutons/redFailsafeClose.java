package org.firstinspires.ftc.teamcode.TimeAutons;/*
package org.firstinspires.ftc.teamcode.TimeAutons;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.FireHardwareMap;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

@Autonomous(name="redFailsafeClose", group="TimeAutons")
@Disabled
public class redFailsafeClose extends LinearOpMode {

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

            sleep(1030);

            robot.frontLeftMotor.setPower(0.0);
            robot.frontRightMotor.setPower(0.0);
            robot.backLeftMotor.setPower(0.0);
            robot.backRightMotor.setPower(0.0);

            sleep(200);

            robot.frontLeftMotor.setPower(-0.5);
            robot.frontRightMotor.setPower(-0.5);
            robot.backLeftMotor.setPower(-0.5);
            robot.backRightMotor.setPower(-0.5);

            sleep(800);

            robot.frontLeftMotor.setPower(0.0);
            robot.frontRightMotor.setPower(0.0);
            robot.backLeftMotor.setPower(0.0);
            robot.backRightMotor.setPower(0.0);

            sleep(200);

            robot.frontLeftMotor.setPower(0.5);
            robot.frontRightMotor.setPower(-0.5);
            robot.backLeftMotor.setPower(0.5);
            robot.backRightMotor.setPower(-0.5);

            sleep(900);

            robot.frontLeftMotor.setPower(0.0);
            robot.frontRightMotor.setPower(0.0);
            robot.backLeftMotor.setPower(0.0);
            robot.backRightMotor.setPower(0.0);

            sleep(200);

            robot.frontLeftMotor.setPower(0.5);
            robot.frontRightMotor.setPower(0.5);
            robot.backLeftMotor.setPower(0.5);
            robot.backRightMotor.setPower(0.5);

            sleep(1400);


        }

    }


}


 */