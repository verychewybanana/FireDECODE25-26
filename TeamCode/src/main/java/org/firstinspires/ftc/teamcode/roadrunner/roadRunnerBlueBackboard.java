//package org.firstinspires.ftc.teamcode.roadrunner;
//package org.firstinspires.ftc.teamcode.RRAutons;

//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//import org.firstinspires.ftc.teamcode.FireHardwareMap;
//import org.firstinspires.ftc.teamcode.RRAutons.opencv.BluePositionDetector;
//import org.firstinspires.ftc.teamcode.RRAutons.opencv.RedPositionDetector;
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
//import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

//@Autonomous(name="roadRunnerBlueBackboard", group="Auton")
//public class roadRunnerBlueBackboard extends LinearOpMode {


//  FireHardwareMap robot = null;
//  boolean recognized;


//  @Override
//  public void runOpMode() {
//      BluePositionDetector pd = new BluePositionDetector(hardwareMap, telemetry);
//
//      SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
//      robot = new FireHardwareMap(this.hardwareMap);
//
//      telemetry.addData("Status", "Initialized");
//      telemetry.update();
//
//      Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));
//      drive.setPoseEstimate(startPose);
//
//      TrajectorySequence beginLeft = drive.trajectorySequenceBuilder(startPose)
//              .strafeLeft(15)
//              .build();
//
//      TrajectorySequence navigateLeft = drive.trajectorySequenceBuilder(beginLeft.end())
//              .strafeRight(3)
//              .forward(13.0)
//              .build();
//
//      TrajectorySequence scoreFromLeft = drive.trajectorySequenceBuilder(navigateLeft.end())
//              .strafeLeft(29.0)
//              .turn(Math.toRadians(-90))
//              .strafeLeft(10)
//              .back(5.5)
//              .build();
//
//      TrajectorySequence parkFromLeft = drive.trajectorySequenceBuilder(scoreFromLeft.end())
//              .strafeLeft(19)
//              .build();
//
//      TrajectorySequence navigateMiddle = drive.trajectorySequenceBuilder(navigateLeft.end())
//              .waitSeconds(0.2)
//              .strafeRight(4.0)
//              .forward(11.0)
//              .build();

//        TrajectorySequence moveBackMid = drive.trajectorySequenceBuilder(navigateMiddle.end())
//                .back(11.0)
                .build();

//       TrajectorySequence pushPixelMid = drive.trajectorySequenceBuilder(moveBackMid.end())
                .forward(14.0)
                .build();

//      TrajectorySequence scoreFromMiddle = drive.trajectorySequenceBuilder(pushPixelMid.end())
                .waitSeconds(0.2)
                .strafeLeft(29.0)
                .turn(Math.toRadians(-90))
                .back(9.0)
                .build();

//      TrajectorySequence parkFromMiddle = drive.trajectorySequenceBuilder(scoreFromMiddle.end())
                .strafeLeft(16)
                .build();

//      TrajectorySequence navigateRight = drive.trajectorySequenceBuilder(navigateMiddle.end())
                .turn(Math.toRadians(-90))
                .build();

//      TrajectorySequence backUpRight = drive.trajectorySequenceBuilder(navigateRight.end())
                .back(8)
                .build();

//      TrajectorySequence pushPixelRight = drive.trajectorySequenceBuilder(backUpRight.end())
                .forward(9)
                .build();

//      TrajectorySequence scoreFromRight = drive.trajectorySequenceBuilder(pushPixelRight.end())
                .waitSeconds(0.2)
                .back(40.0)
                .strafeLeft(10)
                .build();

//      TrajectorySequence parkFromRight = drive.trajectorySequenceBuilder(scoreFromRight.end())
                .strafeLeft(10)
                .build();
/*
        waitForStart();

        pd.startStreaming();

        if (opModeIsActive()) {
            sleep(200);

            drive.followTrajectorySequence(beginLeft);

            sleep(500);

            boolean parkLeft = false;
            // camera queries if there are more than 500 white pixels then drop
//            parkLeft = (pd.getTotalPixelValues() > 500) ? true : false;
            if (parkLeft) {
                robot.intakeMotor.setPower(-0.7);
                sleep(1000);
                robot.intakeMotor.setPower(0.0);
                sleep(200);
                robot.doorServo.setPower(-0.8);
                drive.followTrajectorySequence(scoreFromLeft);
                score(robot);
                drive.followTrajectorySequence(parkFromLeft);
                requestOpModeStop();
            }

            drive.followTrajectorySequence(navigateMiddle);

            boolean parkMid = false;
//            parkMid = () ? true : false;
            if (parkMid) {
                drive.followTrajectorySequence(moveBackMid);
                robot.intakeMotor.setPower(-0.7);
                sleep(1000);
                robot.intakeMotor.setPower(0.0);
                sleep(200);
                robot.doorServo.setPower(-0.8);
                drive.followTrajectorySequence(pushPixelMid);
                drive.followTrajectorySequence(scoreFromMiddle);
                score(robot);
                drive.followTrajectorySequence(parkFromMiddle);
                requestOpModeStop();
            }

            drive.followTrajectorySequence(navigateRight);

            boolean parkRight = true;
//            recognized = () ? true : false;
            if (parkRight) {
                drive.followTrajectorySequence(backUpRight);
                robot.intakeMotor.setPower(-0.7);
                sleep(1000);
                robot.intakeMotor.setPower(0.0);
                sleep(200);
                robot.doorServo.setPower(-0.8);
                drive.followTrajectorySequence(pushPixelRight);
                drive.followTrajectorySequence(scoreFromRight);
                score(robot);
                drive.followTrajectorySequence(parkFromRight);
                requestOpModeStop();
            }


        }
    }

    public int getPixels(BluePositionDetector pd) {
        int left = pd.getLeftValue();
        int right = pd.getRightValue();

        telemetry.addData("Left pixels: ", left);
        telemetry.addData("Right pixels: ", right);
        telemetry.update();

        if (pd.getTotalPixelValues() < 20) {
            telemetry.addData("Found: ", "none");
            telemetry.update();
            return 0; // none found
        }

        if (left > right) {
            telemetry.addData("Found: ", "left");
            telemetry.update();
            return 1; // found on left side
        }

        telemetry.addData("Found: ", "right");
        telemetry.update();
        return 2; // found on right side
    }

    public void score(FireHardwareMap robot) {
        int duration = 1700;
        robot.slideRightMotor.setPower(0.9);
        robot.slideLeftMotor.setPower(0.9);
        sleep(duration);
        robot.slideLeftMotor.setPower(0.0);
        robot.slideRightMotor.setPower(0.0);
        sleep(500);
        robot.boxLeftServo.setPosition(0.3);
        robot.boxRightServo.setPosition(0.3);
        sleep(1000);
        robot.doorServo.setPower(0.3);
        sleep(1000);
        robot.boxLeftServo.setPosition(0.0);
        robot.boxRightServo.setPosition(0.0);
        sleep(500);
        robot.slideRightMotor.setPower(-0.9);
        robot.slideLeftMotor.setPower(-0.9);
        sleep(duration-500);
        robot.slideLeftMotor.setPower(0.0);
        robot.slideRightMotor.setPower(0.0);
    }
//
}
*/
