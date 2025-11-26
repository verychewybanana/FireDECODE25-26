package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

public class FireHardwareMap {
    //create drivetrain motors
    public DcMotor frontRightMotor = null;
    public DcMotor frontLeftMotor = null;
    public DcMotor backRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor intakeMotor = null;
    public DcMotor outTakeMotorRight = null;
    public DcMotor outTakeMotorLeft = null;
    public DcMotor outTake1 = null;
    public DcMotor outTake2 = null;
    public DcMotor outTake3 = null;
    public BNO055IMU imu;
    public RevBlinkinLedDriver led;

    public void init(HardwareMap hwMap) {
        led = hwMap.get(RevBlinkinLedDriver.class, "led");  // Make sure "led" matches your config name
    }

    //Hardware Map object
    com.qualcomm.robotcore.hardware.HardwareMap HardwareMap = null;

    public ElapsedTime runtime = new ElapsedTime();

    public FireHardwareMap(com.qualcomm.robotcore.hardware.HardwareMap hwmap){

        initialize(hwmap);
    }

    private void initialize(com.qualcomm.robotcore.hardware.HardwareMap hwmap){
        HardwareMap = hwmap;
        //the name of device should change based on name
        frontRightMotor = HardwareMap.get(DcMotor.class, "frontRightMotor");
        frontLeftMotor = HardwareMap.get(DcMotor.class, "frontLeftMotor");
        backRightMotor = HardwareMap.get(DcMotor.class, "backRightMotor");
        backLeftMotor = HardwareMap.get(DcMotor.class, "backLeftMotor");
        intakeMotor = HardwareMap.get(DcMotor.class, "intakeMotor");
        outTakeMotorLeft = HardwareMap.get(DcMotor.class, "outTakeMotorLeft");
        outTakeMotorRight = HardwareMap.get(DcMotor.class, "outTakeMotorRight");
        outTake1 = HardwareMap.get(DcMotor.class, "outTake1");
        outTake2 = HardwareMap.get(DcMotor.class, "outTake2");
        outTake3 = HardwareMap.get(DcMotor.class, "outTake3");

        // imu = HardwareMap.get(BNO055IMU.class, "imuex");
       // led = HardwareMap.get(RevBlinkinLedDriver.class, "led");
       // color = HardwareMap.get(ColorRangeSensor.class, "color");

        //Making servo


        //Set up motor direction
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
        outTakeMotorLeft.setDirection(DcMotor.Direction.REVERSE);
        outTakeMotorRight.setDirection(DcMotor.Direction.FORWARD);
        outTake3.setDirection(DcMotor.Direction.FORWARD);
        outTake2.setDirection(DcMotor.Direction.FORWARD);
        outTake1.setDirection(DcMotor.Direction.FORWARD);



        //Set motor mode
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTakeMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTakeMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTake3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTake2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTake1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



        //Set zero power behavior
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTakeMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTakeMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTake1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTake2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTake3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);




        //Set 0 power
        frontRightMotor.setPower(0);
        frontLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        backLeftMotor.setPower(0);

        intakeMotor.setPower(0);
        outTakeMotorLeft.setPower(0);
        outTakeMotorRight.setPower(0);
        outTake1.setPower(0);
        outTake2.setPower(0);
        outTake3.setPower(0);


        // .setPosition(Constants.ARMSERVO_HOMEPOSITION);
        // armServo.setPosition(0);



        /*
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        //return value of radians
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        //gets imu from rev hardware map and connects it to code
        imu = hwmap.get(BNO055IMU.class, "imuex");
        //sets the settings we declared above.
        imu.initialize(parameters);
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

         */
        BNO055IMU imu = HardwareMap.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        imu.initialize(parameters);
    }
}