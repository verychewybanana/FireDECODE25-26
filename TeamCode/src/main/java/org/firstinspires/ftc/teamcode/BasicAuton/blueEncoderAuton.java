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

            bad.drive(-50);   // will now BLOCK until finished

            bad.turn(10);     // blocks until turn finished

            bad.strafe(30);   // blocks until strafe done

        }
    }
}
