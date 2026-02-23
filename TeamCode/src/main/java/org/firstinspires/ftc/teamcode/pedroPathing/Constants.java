package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.paths.PathConstraints;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
public class Constants {

    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(0)                              // TODO: set robot mass in KG
            .forwardZeroPowerAcceleration(0)      // TODO: from Forward Zero Power Acceleration Tuner
            .lateralZeroPowerAcceleration(0)      // TODO: from Lateral Zero Power Acceleration Tuner
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0, 0.01, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(0.8, 0, 0, 0.01))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.1, 0, 0.00035, 0.6, 0.015))
            .centripetalScaling(0.0005);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .leftFrontMotorName("leftFront")
            .leftRearMotorName("leftBack")
            .rightFrontMotorName("rightFront")
            .rightRearMotorName("rightBack")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(0)                         // TODO: from Forward Velocity Tuner
            .yVelocity(0);                         // TODO: from Lateral Velocity Tuner

    public static DriveEncoderConstants localizerConstants = new DriveEncoderConstants()
            .leftFrontMotorName("leftFront")
            .leftRearMotorName("leftBack")
            .rightFrontMotorName("rightFront")
            .rightRearMotorName("rightBack")
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.FORWARD)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD)
            .robotWidth(0)                         // TODO: measure width in inches (left to right wheels)
            .robotLength(0)                        // TODO: measure length in inches (front to back wheels)
            .forwardTicksToInches(0)               // TODO: from Forward Tuner
            .strafeTicksToInches(0)                // TODO: from Lateral Tuner
            .turnTicksToInches(0);                 // TODO: from Turn Tuner

    public static PathConstraints pathConstraints = new PathConstraints(
            0.995, 0.1, 0.1, 0.009, 50, 1.25, 10, 1
    );

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .driveEncoderLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}