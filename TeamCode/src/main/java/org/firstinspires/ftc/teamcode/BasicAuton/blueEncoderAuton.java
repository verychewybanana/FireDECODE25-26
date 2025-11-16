package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;
@Autonomous(name="blueEncoderAuton", group="Auton")
public class blueEncoderAuton extends LinearOpMode {
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
//            bad.drive(-10);
//            sleep(200);
//            bad.turn(5);
//            sleep(200);
//            robot.outTakeMotorRight.setPower(0.85);
//            robot.outTakeMotorLeft.setPower(0.85);
//            sleep(300);
//            robot.intakeMotor.setPower(0.8);
//            sleep(500);
//            robot.outTakeMotorRight.setPower(0);
//            robot.outTakeMotorLeft.setPower(0);
//            robot.intakeMotor.setPower(0);
            bad.drive(-50);
            sleep(2000);

            /*bad.drive(100);
            sleep(2000);
            robot.outTakeMotorLeft.setPower(0.85);
            robot.outTakeMotorRight.setPower(0.85);
            bad.turn(-20);
            sleep(2000);
            robot.intakeMotor.setPower(0.6);
            sleep(3000);
            robot.outTakeMotorRight.setPower(0);
            robot.outTakeMotorLeft.setPower(0);
            robot.intakeMotor.setPower(0);*/
        }

    }

}
