package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class FireHardwareMap {
    //create drivetrain motors

    //Motors near intake are front, motors near outtake are back
    public DcMotor frontRightMotor = null;
    public DcMotor frontLeftMotor = null;
    public DcMotor backRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor intakeMotor = null;

    //Left outtake when shooting (near back right motor)
    public DcMotor outTakeLeft = null;
    //Left outtake when shooting (near back left motor)
    public DcMotor outTakeRight = null;

    //intermediate motor
    public DcMotor midMotor = null;

    // CHANGED: BNO055IMU -> IMU (Universal Interface for BHI260AP)
    public IMU imu;

    public RevBlinkinLedDriver led;

    public Servo pusherServo = null;

    public Limelight3A limelight = null;
    public VoltageSensor batteryVoltageSensor = null;

    //Hardware Map object
    com.qualcomm.robotcore.hardware.HardwareMap HardwareMap = null;

    public ElapsedTime runtime = new ElapsedTime();

    public FireHardwareMap(com.qualcomm.robotcore.hardware.HardwareMap hwmap){
        initialize(hwmap);
    }

    // Optional init method if you need to call it separately
    public void init(HardwareMap hwMap) {
        initialize(hwMap);
    }

    private void initialize(com.qualcomm.robotcore.hardware.HardwareMap hwmap){
        HardwareMap = hwmap;

        // --- MOTORS ---
        frontRightMotor = HardwareMap.get(DcMotor.class, "frontRightMotor");
        frontLeftMotor = HardwareMap.get(DcMotor.class, "frontLeftMotor");
        backRightMotor = HardwareMap.get(DcMotor.class, "backRightMotor");
        backLeftMotor = HardwareMap.get(DcMotor.class, "backLeftMotor");
        intakeMotor = HardwareMap.get(DcMotor.class, "intakeMotor");
        outTakeLeft = HardwareMap.get(DcMotor.class, "outTakeLeft");
        outTakeRight = HardwareMap.get(DcMotor.class, "outTakeRight");
        midMotor = HardwareMap.get(DcMotor.class, "midMotor");

        // --- SENSORS & SERVOS ---
        // limelight = HardwareMap.get(Limelight3A.class, "limelight");
        batteryVoltageSensor = HardwareMap.voltageSensor.iterator().next();
        pusherServo = HardwareMap.get(Servo.class, "pusherServo");

        // led = HardwareMap.get(RevBlinkinLedDriver.class, "led");

        // --- MOTOR DIRECTIONS ---
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        midMotor.setDirection(DcMotor.Direction.REVERSE);
        outTakeLeft.setDirection(DcMotor.Direction.REVERSE);
        outTakeRight.setDirection(DcMotor.Direction.FORWARD);

        pusherServo.setDirection(Servo.Direction.REVERSE);


        // --- MOTOR MODES ---
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        midMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        // --- ZERO POWER BEHAVIOR ---
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTakeLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTakeRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        midMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // --- SET INITIAL POWER ---
        frontRightMotor.setPower(0);
        frontLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        backLeftMotor.setPower(0);

        intakeMotor.setPower(0);
        outTakeLeft.setPower(0);
        outTakeRight.setPower(0);
        midMotor.setPower(0);


        // --------------------------------------------------------------------------------
        // NEW IMU INITIALIZATION (BHI260AP / Universal Interface)
        // --------------------------------------------------------------------------------

        // 1. Get the IMU from hardware map (Make sure config name is "imu")
        imu = HardwareMap.get(IMU.class, "imu");

        // 2. Define how the Control Hub is mounted on the robot.
        //    EDIT THESE TWO LINES to match your actual robot mounting!
        //    Options: UP, DOWN, LEFT, RIGHT, FORWARD, BACKWARD
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        // 3. Initialize the IMU with these parameters
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }
}