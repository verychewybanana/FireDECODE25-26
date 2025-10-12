
package org.firstinspires.ftc.teamcode.RRAutons;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FireHardwareMap;
import org.firstinspires.ftc.teamcode.RRAutons.opencv.RedPositionDetector;

@Autonomous(name="CV Test", group="Auton")
@Config
public class CVTest extends LinearOpMode {
    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {
        robot = new FireHardwareMap(this.hardwareMap);
        RedPositionDetector pd = new RedPositionDetector(hardwareMap, telemetry);
        pd.startStreaming();
//        int count = 0;
        int x;
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while (opModeIsActive()) {
            robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
//            pd.getPosition();
            telemetry.addData("Left Pixels: ", pd.getLeftValue());
            telemetry.addData("Right Pixels: ", pd.getRightValue());
            telemetry.addData("Total Pixels: ", pd.getTotalPixelValues());
            if (pd.getLeftValue() - pd.getRightValue() > 1000){
                x = 0;
            }
            else if(pd.getRightValue()-pd.getLeftValue() > 1000){
                x = 1;
            }
            else{
                x = 2;
            }
            telemetry.addData("Zone: ", x);
//            telemetry.addData("count", count);
//            count++;

            telemetry.update();

//CVVVVVVV
        }
    }
}


