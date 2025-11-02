package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="New", group="Diagnostic")
public class New extends LinearOpMode {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    @Override
    public void runOpMode() {
        // --- DEFINE MOTORS ---
        // These names MUST match your Driver Hub configuration
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");

        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);


        telemetry.addLine("--- MOTOR TEST ---");
        telemetry.addLine("Robot MUST be on blocks");
        telemetry.addLine("Press buttons to test motors ONE AT A TIME.");
        telemetry.addLine("All wheels should spin FORWARD.");
        telemetry.addLine("--------------------");
        telemetry.addLine("X = Front Left");
        telemetry.addLine("Y = Front Right");
        telemetry.addLine("A = Back Left");
        telemetry.addLine("B = Back Right");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double testPower = 0.4;

            // Test Front Left
            if (gamepad1.x) {
                frontLeftMotor.setPower(testPower);
                telemetry.addData("Testing", "FRONT LEFT (X)");
            } else {
                frontLeftMotor.setPower(0);
            }

            // Test Front Right
            if (gamepad1.y) {
                frontRightMotor.setPower(testPower);
                telemetry.addData("Testing", "FRONT RIGHT (Y)");
            } else {
                frontRightMotor.setPower(0);
            }

            // Test Back Left
            if (gamepad1.a) {
                backLeftMotor.setPower(testPower);
                telemetry.addData("Testing", "BACK LEFT (A)");
            } else {
                backLeftMotor.setPower(0);
            }

            // Test Back Right
            if (gamepad1.b) {
                backRightMotor.setPower(testPower);
                telemetry.addData("Testing", "BACK RIGHT (B)");
            } else {
                backRightMotor.setPower(0);
            }

            if(!gamepad1.x && !gamepad1.y && !gamepad1.a && !gamepad1.b) {
                telemetry.addData("Testing", "None");
            }
            telemetry.update();
        }
    }
}