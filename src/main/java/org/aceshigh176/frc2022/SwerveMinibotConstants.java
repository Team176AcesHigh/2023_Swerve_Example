package org.aceshigh176.frc2022;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.aceshigh176.frc2022.constants.SwerveModuleConstants;
import org.aceshigh176.lib.util.UnitUtil;

/**
 * A list of constants used by the rest of the robot code. This includes physical constants as well as constants
 * determined through calibrations.
 *
 * Constants marked final are to note that it cannot currently be changed on the fly.
 * That said, if you want to dynamically change it during runtime, you can move where it's used and then remove
 * the final flag.
 * Be aware that a method might need to be called to reconfigure the subsystem that uses those constants (such as pid
 * values over the CAN bus).
 */
public class SwerveMinibotConstants {

    static SwerveMinibotConstants mInstance = new SwerveMinibotConstants();

    public static SwerveMinibotConstants getInstance() {
        return mInstance;
    }

    public static final int kLongCANTimeoutMs = 100;
    public static final int kCANTimeoutMs = 10;

    public static final PhysicalRobotConstants physicalRobotConstants = new PhysicalRobotConstants();
    public static final SwerveConstants swerveConstants = new SwerveConstants();

    public static class PhysicalRobotConstants {
        public final double frameWidthMeters = UnitUtil.inchesToMeters(22);
        public final double frameLengthMeters = UnitUtil.inchesToMeters(22);
        public final double trackWidthMeters = UnitUtil.inchesToMeters(14.55 + (14.95 - 14.55) / 2.0);

        public final double driveGearRatio = 6.75 / 1.0;
        public final double angleGearRatio = 21.43;

        public final double wheelDiameterMeters = UnitUtil.inchesToMeters(4.0);
//        public final double wheelCircumferenceMeters = wheelDiameterMeters * Math.PI;
    }

    public static class SwerveConstants {

        // TODO: Tune
        public double maxSpeed = 160;

        public final boolean invertGyro = false;
        public final boolean canCoderInvert = false;

        public double openLoopRamp = 0.25;
        public double closedLoopRamp = .05;

        /* Swerve Current Limiting */
        public final int angleContinuousCurrentLimit = 25;
        public final int anglePeakCurrentLimit = 40;
        public final double anglePeakCurrentDuration = 0.1;
        public final boolean angleEnableCurrentLimit = true;

        public final int driveContinuousCurrentLimit = 35;
        public final int drivePeakCurrentLimit = 60;
        public final double drivePeakCurrentDuration = 0.1;
        public final boolean driveEnableCurrentLimit = true;

        /* Angle Motor PID Values */
        public final double angleKP = 0.3;
        public final double angleKI = 0.0;
        public final double angleKD = 0.0;
        public final double angleKF = 0.0;

        /* Drive Motor PID Values */
        public final double driveKP = 0.005; // was 0.05
        public final double driveKI = 0.0;
        public final double driveKD = 0.0;
        public final double driveKF = 0.0;

        public final int frontRightAngleMotorId = 1;
        public final int frontRightDriveMotorId = 2;
        public final int frontRightAngleCanCoderId = 13;
        public final double frontRightAngleOffset = 44.29;
        public final int backRightAngleMotorId = 3;
        public final int backRightDriveMotorId = 4;
        public final int backRightAngleCanCoderId = 12;
        public final double backRightAngleOffset = 284.58;
        public final int backLeftAngleMotorId = 5;
        public final int backLeftDriveMotorId = 6;
        public final int backLeftAngleCanCoderId = 11;
        public final double backLeftAngleOffset = 248.64;
        public final int frontLeftAngleMotorId = 7;
        public final int frontLeftDriveMotorId = 8;
        public final int frontLeftAngleCanCoderId = 10;
        public final double frontLeftAngleOffset = 78.92; // Angle such that

        public final SwerveModuleConstants frontLeft = new SwerveModuleConstants(
                frontLeftDriveMotorId,
                frontLeftAngleMotorId,
                frontLeftAngleCanCoderId,
                frontLeftAngleOffset
        );
        public final SwerveModuleConstants backLeft = new SwerveModuleConstants(
                backLeftDriveMotorId,
                backLeftAngleMotorId,
                backLeftAngleCanCoderId,
                backLeftAngleOffset
        );
        public final SwerveModuleConstants backRight = new SwerveModuleConstants(
                backRightDriveMotorId,
                backRightAngleMotorId,
                backRightAngleCanCoderId,
                backRightAngleOffset
        );
        public final SwerveModuleConstants frontRight = new SwerveModuleConstants(
                frontRightDriveMotorId,
                frontRightAngleMotorId,
                frontRightAngleCanCoderId,
                frontRightAngleOffset
        );

        public final NeutralMode angleNeutralMode = NeutralMode.Coast;
        public final NeutralMode driveNeutralMode = NeutralMode.Brake;

        public final boolean driveMotorInvert = false;
        public final boolean angleMotorInvert = true;

    }
}