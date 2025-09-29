package org.firstinspires.ftc.teamcode.teamcode.BasicAuton;/*
package org.firstinspires.ftc.teamcode.BasicAuton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.FireHardwareMap;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

@Autonomous(name="redBackboardAuton", group="Auton")
@Disabled
public class redBackboardAuton extends LinearOpMode {

    private TfodProcessor tfod;
    private VisionPortal visionPortal;
    FireHardwareMap robot = null;

    @Override
    public void runOpMode() {
        robot = new FireHardwareMap(this.hardwareMap);
        BasicAutoDriving autoDriving = new BasicAutoDriving(robot.frontLeftMotor, robot.frontRightMotor, robot.backLeftMotor, robot.backRightMotor);


        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        if (opModeIsActive()){
//            initTfod();
//            int tickID = findPixelLocation(autoDriving);
            scorePreLoaded(0, autoDriving);



        }

    }

    public void initTfod() {
        tfod = TfodProcessor.easyCreateWithDefaults();

        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"), tfod);

        tfod.setZoom(2.0);

        telemetry.addData("Camera Status: ", "Initialized");
        telemetry.update();

    }

    public int findPixelLocation(BasicAutoDriving bad) {
//        bad.drive(30);
//        sleep(2000);
        visionPortal.resumeStreaming();
        sleep(1500);
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        sleep(1000);
        telemetry.addData("recogs len: ", currentRecognitions.size());
        telemetry.update();
        if (currentRecognitions.size() != 0) {
//            bad.drive(-30);
//            sleep(2000);
            telemetry.addData("Pixel Location: ", 1);
            telemetry.update();
            sleep(6000);
            return 1;
        }
        visionPortal.stopStreaming();

        bad.turn(10);
        sleep(500);
        bad.drive(30);
        sleep(1000);

        visionPortal.resumeStreaming();
        sleep(1500);
        currentRecognitions = tfod.getRecognitions();
        telemetry.addData("Recogs len: ", currentRecognitions.size());
        telemetry.update();
        sleep(100);
        visionPortal.stopStreaming();
        if (currentRecognitions.size() != 0) {
            bad.drive(-30);
            sleep(1000);
            bad.turn(-10);
            sleep(500);
            telemetry.addData("Pixel Location: ", 0);
            telemetry.update();
            sleep(1000);
            return 0;
        } else {
            bad.drive(-30);
            sleep(1000);
            bad.turn(-10);
            sleep(500);
            telemetry.addData("Pixel Location: ", 2);
            telemetry.update();
            sleep(1000);
            return 2;
        }
    }

    public void scorePreLoaded(int tickID, BasicAutoDriving bad) {
        if (tickID == 0) { // middle tick
            bad.drive(50);
            sleep(2000);
            robot.intakeMotor.setPower(-0.7);
            sleep(500);
            robot.intakeMotor.setPower(0.0);
            bad.drive(-43);
            sleep(2000);
        } else if (tickID == 1) { // left tick
            bad.drive(40);
            sleep(1500);
            bad.turn(-45);
            sleep(500);
            bad.drive(18);
            sleep(1000);
            bad.drive(-18);
            sleep(1000);
            bad.turn(45);
            sleep(500);
            bad.drive(-27);
            sleep(1500);
            bad.turn(80);
            sleep(1000);
            bad.drive(45);
            sleep(3000);
        } else if (tickID == 2) { // right tick
            bad.turn(13);
            sleep(500);
            bad.drive(48);
            sleep(2000);
            bad.drive(-40);
            sleep(2000);
            bad.turn(-13);
            sleep(500);
            bad.turn(83);
            sleep(1500);
            bad.drive(45);
            sleep(3000);


        }
    }

    public int parseDirection(String dir) {
        if (dir == "left") {
            return 1;
        } else if (dir == "center") {
            return 0;
        } else if (dir == "right") {
            return 2;
        }
        return 0;
    }
}
*/