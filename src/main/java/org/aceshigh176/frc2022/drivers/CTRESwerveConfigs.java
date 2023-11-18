package org.aceshigh176.frc2022.drivers;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import org.aceshigh176.frc2022.SwerveMinibotConstants;

public class CTRESwerveConfigs {
    public static TalonFXConfiguration swerveDriveFXConfig() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        SupplyCurrentLimitConfiguration driveSupplyLimit = new SupplyCurrentLimitConfiguration(
                SwerveMinibotConstants.swerveConstants.driveEnableCurrentLimit,
                SwerveMinibotConstants.swerveConstants.driveContinuousCurrentLimit,
                SwerveMinibotConstants.swerveConstants.drivePeakCurrentLimit,
                SwerveMinibotConstants.swerveConstants.drivePeakCurrentDuration);

        config.slot0.kP = SwerveMinibotConstants.swerveConstants.driveKP;
        config.slot0.kI = SwerveMinibotConstants.swerveConstants.driveKI;
        config.slot0.kD = SwerveMinibotConstants.swerveConstants.driveKD;
        config.slot0.kF = SwerveMinibotConstants.swerveConstants.driveKF;
        config.supplyCurrLimit = driveSupplyLimit;
        config.initializationStrategy = SensorInitializationStrategy.BootToZero;
        config.openloopRamp = SwerveMinibotConstants.swerveConstants.openLoopRamp;
        config.closedloopRamp = SwerveMinibotConstants.swerveConstants.closedLoopRamp;
        config.peakOutputForward = .1;
        config.peakOutputReverse = -.1;
        return config;
    }

    public static TalonFXConfiguration swerveAngleFXConfig() {
        TalonFXConfiguration angleConfig = new TalonFXConfiguration();
        SupplyCurrentLimitConfiguration angleSupplyLimit = new SupplyCurrentLimitConfiguration(
                SwerveMinibotConstants.swerveConstants.angleEnableCurrentLimit,
                SwerveMinibotConstants.swerveConstants.angleContinuousCurrentLimit,
                SwerveMinibotConstants.swerveConstants.anglePeakCurrentLimit,
                SwerveMinibotConstants.swerveConstants.anglePeakCurrentDuration);

        angleConfig.slot0.kP = SwerveMinibotConstants.swerveConstants.angleKP;
        angleConfig.slot0.kI = SwerveMinibotConstants.swerveConstants.angleKI;
        angleConfig.slot0.kD = SwerveMinibotConstants.swerveConstants.angleKD;
        angleConfig.slot0.kF = SwerveMinibotConstants.swerveConstants.angleKF;
        angleConfig.supplyCurrLimit = angleSupplyLimit;
        angleConfig.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        angleConfig.peakOutputForward = .1;
        angleConfig.peakOutputReverse = -.1;
        return angleConfig;
    }

    public static CANCoderConfiguration swerveCancoderConfig() {
        CANCoderConfiguration config = new CANCoderConfiguration();
        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        config.sensorDirection = SwerveMinibotConstants.swerveConstants.canCoderInvert;
        config.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        config.sensorTimeBase = SensorTimeBase.PerSecond;
        return config;
    }
}
