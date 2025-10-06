package org.firstinspires.ftc.teamcode;/*
package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**

 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *

 */
/*
@TeleOp(name="Field Oriented TeleOp", group="Linear Opmode")
public class FieldOriented extends LinearOpMode {

    /*
    Controls for gamepad2
    Dpad down - lower slides
    Dpad up - raise slides to high level
    Dpad right - raise slides to mid level
    Y - open door
    B - close door
    A - tilt box to scoring position
    X - return box to 0
    Left bumper - toggle separator
    Right bumper - toggle hook servo up or down
    Left joystick y - spin intake wheels
    Right joystick x - actuator motor



    //ServoImplEx servo;
    //PwmControl.PwmRange range = new PwmControl.PwmRange(533,2425);
    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private FireHardwareMap HW = null;
    private ActiveLocation activeLocation = null;
    @Override

    public void runOpMode() {
        HW = new FireHardwareMap(this.hardwareMap);
        activeLocation = new ActiveLocation(HW);
        //servo = hardwareMap.get(ServoImplEx.class, "left_hand");
        //servo.setPwmRange(range);


        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        boolean separated = false;
        boolean hookUp = false;
        double currentAngle;
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;
            double i =0.0;
            currentAngle = activeLocation.getTrimmedAngleInRadians();


            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y * Math.cos(currentAngle) +
                    gamepad1.left_stick_x * Math.sin(currentAngle); // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x * Math.cos(currentAngle) -
                    -gamepad1.left_stick_y * Math.sin(currentAngle);
            double yaw     =  gamepad1.right_stick_x;


            double axial2 =  -gamepad2.left_stick_y;
//            double lateral2 =  gamepad2.left_stick_x * 1.1;
            double yaw2     =  gamepad2.right_stick_x;


            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            double intakeWheelPower = (gamepad2.right_trigger-gamepad2.left_trigger);


            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));
            i = gamepad1.right_trigger;
            if (max > 1) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
//                axial2 /=max;
                i /= max;
//                (lateral2)/=max;
                yaw2 /=max;
            }
            if(gamepad1.right_bumper){
                i=i;
            }
            else{
                leftFrontPower  /= 2;
                rightFrontPower /= 2;
                leftBackPower   /= 2;
                rightBackPower  /= 2;
            }

          /*
            double doorServoPower = HW.doorServo.getPower();

            if (gamepad2.b) {
                doorServoPower = 0.35;
            } else if (gamepad2.a) {
                doorServoPower = -0.8;
//            } else {
//                doorServoPower = 0.0;
            }

            double airplaneServoPower;

            if (gamepad1.y) {
                airplaneServoPower = 0.8;
            } else if (gamepad1.b) {
                airplaneServoPower = -0.8;
            } else {
              ]
               =['irplaneServoPower = 0;
            }-oad2.left_bumper) {
//                separatorServoPosition = 0.3;
//            }  else if (gamepad2.right_bumper) {
//                separatorServoPosition = 0.7;
//            }
//
//            double hookServoPower;

            if (gamepad1.dpad_up) {
                hangMotorPower = 0.9;
            } else if (gamepad1.dpad_down) {
                hangMotorPower = -0.9;
            }





            double slidePower;
            double susanPower;

            double armPower = 0;
            double clawPower = 0;

            if (gamepad2.y) {
                armPower = -0.5;
            } else if (gamepad2.a) {
                armPower = 0.3;
            } else {
                armPower = 0;
            }

            double susanPosition = HW.susanMotor.getCurrentPosition();

            if (gamepad1.dpad_left) {
                susanPower = 0.5;
            } else if (gamepad1.dpad_right) {
                susanPower = -0.5;
            } else {
                susanPower = 0;
            }

            if (gamepad2.dpad_up) {
                slidePower = 0.8;
            } else if (gamepad2.dpad_down) {
                slidePower = -0.4;
            } else {
                slidePower = 0;
            }

//            if (gamepad2.dpad_right) {
//                HW.armServo.setPosition(0.5);
//            } else if (gamepad2.dpad_left) {
//                HW.armServo.setPosition(0);
//            }

//            double armServoPosition = HW.armServo.getPosition();
//
//            if (gamepad2.y) {
//                armServoPosition = 0.0;
//            } else if (gamepad2.a) {
//                armServoPosition = 0.5;
//            }

            if (gamepad2.right_bumper) {
                clawPower = 0.8;
            } else {
                clawPower = -0.2;
            }

            yaw2 = yaw2/1.5;

            // Send calculated power to wheels
            HW.frontLeftMotor.setPower(leftFrontPower);
            HW.frontRightMotor.setPower(rightFrontPower);
            HW.backLeftMotor.setPower(leftBackPower*1.1);
            HW.backRightMotor.setPower(rightBackPower*1.1);

            HW.slideMotor.setPower(slidePower);
            HW.susanMotor.setPower(susanPower);

            HW.clawServo.setPower(clawPower);
            // HW.armServo.setPosition(armServoPosition);

            HW.armServo.setPower(armPower);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            /*
            telemetry.addData("LED GREEN", HW.color.green());
            telemetry.addData("LED red", HW.color.red());
            telemetry.addData("LED blue", HW.color.blue());
            telemetry.addData("LED ARGB", HW.color.argb());

             */

           // telemetry.update();

        //}
    //}
//}
