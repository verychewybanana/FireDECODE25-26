package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
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
    public BNO055IMU imu;
    public RevBlinkinLedDriver led;

    public Servo pusherServo = null;

    public Limelight3A limelight = null;
    public VoltageSensor batteryVoltageSensor = null;

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
        outTakeLeft = HardwareMap.get(DcMotor.class, "outTakeLeft");
        outTakeRight = HardwareMap.get(DcMotor.class, "outTakeRight");
        midMotor = HardwareMap.get(DcMotor.class, "midMotor");
       // limelight = HardwareMap.get(Limelight3A.class, "limelight");
        // Define Voltage Sensor (Usually "Control Hub")
        batteryVoltageSensor = HardwareMap.voltageSensor.iterator().next();
        pusherServo = HardwareMap.get(Servo.class, "pusherServo");

        // imu = HardwareMap.get(BNO055IMU.class, "imuex");
       // led = HardwareMap.get(RevBlinkinLedDriver.class, "led");
       // color = HardwareMap.get(ColorRangeSensor.class, "color");

        //Making servo


        //Set up motor direction
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        midMotor.setDirection(DcMotor.Direction.REVERSE);
        outTakeLeft.setDirection(DcMotor.Direction.REVERSE);
        outTakeRight.setDirection(DcMotor.Direction.FORWARD);

        pusherServo.setDirection(Servo.Direction.REVERSE);


        //Set motor mode
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        midMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outTakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



        //Set zero power behavior
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTakeLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outTakeRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        midMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);




        //Set 0 power
        frontRightMotor.setPower(0);
        frontLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        backLeftMotor.setPower(0);

        intakeMotor.setPower(0);
        outTakeLeft.setPower(0);
        outTakeRight.setPower(0);
        midMotor.setPower(0);




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