package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;
@Autonomous(name="encoderAuton", group="Auton")
public class encoderAuton extends LinearOpMode {
    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {


        robot = new FireHardwareMap(this.hardwareMap);
        BasicAutoDriving bad = new BasicAutoDriving(robot.frontLeftMotor, robot.frontRightMotor, robot.backLeftMotor, robot.backRightMotor);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        if (opModeIsActive()){
            bad.drive(100);
            sleep(2000);
            bad.turn(90);
        }

    }

}
