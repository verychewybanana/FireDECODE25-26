package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

public class EmptyHardwareMap {
    //create drivetrain motors
    public DcMotor frontRightMotor = null;
    public DcMotor frontLeftMotor = null;
    public DcMotor backRightMotor = null;
    public DcMotor backLeftMotor = null;

    // create slide motors
    public DcMotor slideLeftMotor = null;
    public DcMotor slideRightMotor = null;
    public DcMotor stringMotor = null;

    //create mechanism servos
    public DcMotor intakeMotor = null;
    public Servo boxLeftServo = null;
    public Servo boxRightServo = null;
    public CRServo doorServo = null;
    public Servo separatorServo = null;
    public CRServo airplaneServo = null;
//    public Servo hookServo = null;

    public RevBlinkinLedDriver led = null;

    public ColorRangeSensor color = null;

    public BNO055IMU imu = null;


    //Hardware Map object
    com.qualcomm.robotcore.hardware.HardwareMap HardwareMap = null;

    public ElapsedTime runtime = new ElapsedTime();

    public EmptyHardwareMap(com.qualcomm.robotcore.hardware.HardwareMap hwmap){

        initialize(hwmap);
    }

    private void initialize(com.qualcomm.robotcore.hardware.HardwareMap hwmap){
        HardwareMap = hwmap;

        intakeMotor = HardwareMap.get(DcMotor.class, "intakeMotor");
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}