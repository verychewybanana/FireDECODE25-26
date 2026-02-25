package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name="Motor Encoder Test", group="Test")
public class MotorEncoderTest extends LinearOpMode {

    @Override
    public void runOpMode() {

        DcMotorEx leftFront  = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        DcMotorEx leftBack   = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx rightBack  = hardwareMap.get(DcMotorEx.class, "rightBack");

        // Reset encoders so we start from zero
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addLine("Motor Sanity Test");
        telemetry.addLine("Press:");
        telemetry.addLine("X = Left Front");
        telemetry.addLine("Y = Left Back");
        telemetry.addLine("B = Right Front");
        telemetry.addLine("A = Right Back");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double power = 0.3;

            // Spin ONE motor at a time
            leftFront.setPower(gamepad1.x ? power : 0);
            leftBack.setPower(gamepad1.y ? power : 0);
            rightFront.setPower(gamepad1.b ? power : 0);
            rightBack.setPower(gamepad1.a ? power : 0);

            telemetry.addLine("=== HOLD BUTTONS TO TEST ===");
            telemetry.addLine("X=LF  Y=LB  B=RF  A=RB");

            telemetry.addLine("\n=== RAW ENCODER TICKS ===");
            telemetry.addData("Left Front  (X)", leftFront.getCurrentPosition());
            telemetry.addData("Left Back   (Y)", leftBack.getCurrentPosition());
            telemetry.addData("Right Front (B)", rightFront.getCurrentPosition());
            telemetry.addData("Right Back  (A)", rightBack.getCurrentPosition());

            telemetry.update();
        }
    }
}