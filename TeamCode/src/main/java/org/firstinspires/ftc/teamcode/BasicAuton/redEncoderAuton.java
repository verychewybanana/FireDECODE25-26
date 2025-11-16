package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;
@Autonomous(name="redEncoderAuton", group="Auton")
public class redEncoderAuton extends LinearOpMode {
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
            robot.outTakeMotorLeft.setPower(0.85);
            robot.outTakeMotorRight.setPower(0.85);
            bad.drive(10);
            sleep(1000);
            bad.turn(5);
            sleep(1000);
            robot.intakeMotor.setPower(0.6);
            sleep(3000);
            robot.outTakeMotorRight.setPower(0);
            robot.outTakeMotorLeft.setPower(0);
            robot.intakeMotor.setPower(0);


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
