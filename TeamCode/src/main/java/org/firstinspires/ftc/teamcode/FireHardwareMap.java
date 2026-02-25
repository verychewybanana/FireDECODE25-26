package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class FireHardwareMap {

    // --- DRIVETRAIN MOTORS ---
    public DcMotor leftFront  = null;
    public DcMotor rightFront = null;
    public DcMotor leftBack   = null;
    public DcMotor rightBack  = null;

    // --- MECHANISM MOTORS ---
    public DcMotor intakeMotor = null;
    public DcMotor midMotor1   = null;
    public DcMotor midMotor2   = null;
    public DcMotor outtakeMotor = null;

    // --- SENSORS ---
    public IMU imu;
    public VoltageSensor batteryVoltageSensor = null;

    com.qualcomm.robotcore.hardware.HardwareMap HardwareMap = null;
    public ElapsedTime runtime = new ElapsedTime();

    public FireHardwareMap(com.qualcomm.robotcore.hardware.HardwareMap hwmap) {
        initialize(hwmap);
    }

    public void init(HardwareMap hwMap) {
        initialize(hwMap);
    }

    private void initialize(com.qualcomm.robotcore.hardware.HardwareMap hwmap) {
        HardwareMap = hwmap;

        // --- MOTORS ---
        leftFront   = HardwareMap.get(DcMotor.class, "leftFront");
        rightFront  = HardwareMap.get(DcMotor.class, "rightFront");
        leftBack    = HardwareMap.get(DcMotor.class, "leftBack");
        rightBack   = HardwareMap.get(DcMotor.class, "rightBack");

        intakeMotor  = HardwareMap.get(DcMotor.class, "intakeMotor");
        midMotor1    = HardwareMap.get(DcMotor.class, "midMotor1");
        midMotor2    = HardwareMap.get(DcMotor.class, "midMotor2");
        outtakeMotor = HardwareMap.get(DcMotor.class, "outtakeMotor");

        // --- SENSORS ---
        batteryVoltageSensor = HardwareMap.voltageSensor.iterator().next();

        // --- MOTOR DIRECTIONS ---
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        midMotor1.setDirection(DcMotor.Direction.REVERSE);
        midMotor2.setDirection(DcMotor.Direction.REVERSE); // opposite so they both push inward
        outtakeMotor.setDirection(DcMotor.Direction.REVERSE);

        // --- MOTOR MODES ---
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        midMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        midMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // --- ZERO POWER BEHAVIOR ---
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        midMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        midMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // --- INITIAL POWER ---
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);

        intakeMotor.setPower(0);
        midMotor1.setPower(0);
        midMotor2.setPower(0);
        outtakeMotor.setPower(0);

        // --- IMU ---
        imu = HardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }
}