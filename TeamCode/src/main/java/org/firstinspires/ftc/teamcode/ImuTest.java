package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "IMU Test", group = "Test")
public class ImuTest extends LinearOpMode {

    IMU imu;

    @Override
    public void runOpMode() {

        imu = hardwareMap.get(IMU.class, "imu");

        telemetry.addLine("IMU OK - waiting for start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double heading = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            telemetry.addData("Heading (deg)", heading);
            telemetry.update();
        }
    }
}
