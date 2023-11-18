package org.aceshigh176.frc2022.drivers;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import org.aceshigh176.frc2022.SwerveMinibotConstants;
import org.aceshigh176.frc2022.constants.SwerveModuleConstants;
import org.aceshigh176.lib.util.UnitUtil;

public class SwerveModule {
    public int moduleNumber;
    public double angleOffset;
    private TalonFX mAngleMotor;
    private TalonFX mDriveMotor;
    private CANCoder angleEncoder;
    private double lastAngle;

    private double anglekP;
    private double anglekI;
    private double anglekD;

//    SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(Constants.SwerveConstants.driveKS, Constants.SwerveConstants.driveKV, Constants.SwerveConstants.driveKA);

    private final double mDriveRotationsPerEngineeringUnit = SwerveMinibotConstants.physicalRobotConstants.driveGearRatio / (UnitUtil.rotationsToDistance(1, SwerveMinibotConstants.physicalRobotConstants.wheelDiameterMeters));
    private final double mAngleRotationsPerEngineeringUnit = SwerveMinibotConstants.physicalRobotConstants.angleGearRatio / 360.0;

    public SwerveModule(int moduleNumber, SwerveModuleConstants moduleConstants){
        this.moduleNumber = moduleNumber;
        angleOffset = moduleConstants.angleOffset;

        // TODO: Config with retry
        /* Angle Encoder Config */
        angleEncoder = new CANCoder(moduleConstants.cancoderID, "cancan");
        configAngleEncoder();
        angleEncoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, 255);
        angleEncoder.setStatusFramePeriod(CANCoderStatusFrame.VbatAndFaults, 255);

        /* Angle Motor Config */
//        mAngleMotor = TalonFXFactory.createDefaultTalon(moduleConstants.angleMotorID);
        mAngleMotor = new TalonFX(moduleConstants.angleMotorID, "cancan");
        configAngleMotor();
        TalonFXConfiguration angleConfiguration = CTRESwerveConfigs.swerveAngleFXConfig();
        anglekP = angleConfiguration.slot0.kP;
        anglekI = angleConfiguration.slot0.kI;
        anglekD = angleConfiguration.slot0.kD;

        /* Drive Motor Config */
//        mDriveMotor = TalonFXFactory.createDefaultTalon(moduleConstants.driveMotorID);
        mDriveMotor = new TalonFX(moduleConstants.driveMotorID, "cancan");
        configDriveMotor();

        lastAngle = getState().angle.getDegrees();
    }

    public void setManualOutputs(double drivePercent, double anglePercent) {
        mDriveMotor.set(TalonFXControlMode.PercentOutput, drivePercent);
        mAngleMotor.set(TalonFXControlMode.PercentOutput, anglePercent);
    }

    public void setDesiredState(SwerveModuleState desiredState, boolean isOpenLoop){
        desiredState = CTREModuleState.optimize(desiredState, getState().angle); //Custom optimize command, since default WPILib optimize assumes continuous controller which CTRE is not

        if(isOpenLoop){
            double percentOutput = desiredState.speedMetersPerSecond / UnitUtil.inchesToMeters(SwerveMinibotConstants.swerveConstants.maxSpeed);
            mDriveMotor.set(ControlMode.PercentOutput, percentOutput);
        }
        else {
//            double velocity = Conversions.MPSToFalcon(desiredState.speedMetersPerSecond, SwerveMinibotConstants.physicalRobotConstants.wheelDiameterMeters, SwerveMinibotConstants.physicalRobotConstants.driveGearRatio);
//            mDriveMotor.set(ControlMode.Velocity, velocity, DemandType.ArbitraryFeedForward, feedforward.calculate(desiredState.speedMetersPerSecond));
            // rotations per meter
            double velocityCounts = UnitUtil.engineeringUnitsPerSecondToCountsPer100ms(desiredState.speedMetersPerSecond, mDriveRotationsPerEngineeringUnit, 2048);
            mDriveMotor.set(ControlMode.Velocity, velocityCounts);
        }

        double angle = (Math.abs(desiredState.speedMetersPerSecond) <= (UnitUtil.inchesToMeters(SwerveMinibotConstants.swerveConstants.maxSpeed) * 0.01)) ? lastAngle : desiredState.angle.getDegrees(); //Prevent rotating module if speed is less then 1%. Prevents Jittering.
        mAngleMotor.set(ControlMode.Position, UnitUtil.engineeringUnitsToCounts(angle, mAngleRotationsPerEngineeringUnit, 2048));
        lastAngle = angle;
    }

    public void resetToAbsolute(){
//        double absolutePosition = Conversions.degreesToFalcon(getCanCoder().getDegrees() - angleOffset, Constants.SwerveConstants.angleGearRatio);
//        mAngleMotor.setSelectedSensorPosition(absolutePosition);
//        double currentAngleMotorDegrees = UnitUtil.countsToEngineeringUnits(mAngleMotor.getSelectedSensorPosition(), mAngleRotationsPerEngineeringUnit, 2048);
//        mAngleDegreesOffset =

        // TODO: Make this use a software offset so that if we boot up without the CANCoder we're still OK
        double absolutePosition = UnitUtil.engineeringUnitsToCounts(getCanCoder().getDegrees() - angleOffset, mAngleRotationsPerEngineeringUnit, 2048);
        mAngleMotor.setSelectedSensorPosition(absolutePosition);
    }

    private void configAngleEncoder(){
        angleEncoder.configFactoryDefault();
        angleEncoder.configAllSettings(CTRESwerveConfigs.swerveCancoderConfig());
    }

    private void configAngleMotor(){
        mAngleMotor.configFactoryDefault();
        mAngleMotor.configAllSettings(CTRESwerveConfigs.swerveAngleFXConfig());
        mAngleMotor.setInverted(SwerveMinibotConstants.swerveConstants.angleMotorInvert);
        mAngleMotor.setNeutralMode(SwerveMinibotConstants.swerveConstants.angleNeutralMode);
        resetToAbsolute();
    }

    private void configDriveMotor(){
        mDriveMotor.configFactoryDefault();
        mDriveMotor.configAllSettings(CTRESwerveConfigs.swerveDriveFXConfig());
        mDriveMotor.setInverted(SwerveMinibotConstants.swerveConstants.driveMotorInvert);
        mDriveMotor.setNeutralMode(SwerveMinibotConstants.swerveConstants.driveNeutralMode);
        mDriveMotor.setSelectedSensorPosition(0);
    }

    public void updateAnglePID(double kP, double kI, double kD) {
        if (anglekP != kP) {
            anglekP = kP;
            mAngleMotor.config_kP(0, anglekP, SwerveMinibotConstants.kLongCANTimeoutMs);
        }
        if (anglekI != kI) {
            anglekI = kI;
            mAngleMotor.config_kI(0, anglekI, SwerveMinibotConstants.kLongCANTimeoutMs);
        }
        if (anglekD != kP) {
            anglekD = kD;
            mAngleMotor.config_kD(0, anglekD, SwerveMinibotConstants.kLongCANTimeoutMs);
        }
    }

    public double[] getAnglePIDValues() {
        double[] values = {anglekP, anglekI, anglekD};
        return values;
    }

    public Rotation2d getCanCoder(){
        return Rotation2d.fromDegrees(angleEncoder.getAbsolutePosition());
    }

    public double getTargetAngle() {
        return lastAngle;
    }

    public SwerveModuleState getState(){
//        double velocity = Conversions.falconToMPS(mDriveMotor.getSelectedSensorVelocity(), Constants.SwerveConstants.wheelCircumference, Constants.SwerveConstants.driveGearRatio);
        double velocity = UnitUtil.countsPer100msToEngineeringUnitsPerSecond(mDriveMotor.getSelectedSensorVelocity(), mDriveRotationsPerEngineeringUnit, 2048);
//        Rotation2d angle = Rotation2d.fromDegrees(Conversions.falconToDegrees(mAngleMotor.getSelectedSensorPosition(), Constants.SwerveConstants.angleGearRatio));
        Rotation2d angle = Rotation2d.fromDegrees(UnitUtil.countsToEngineeringUnits(mAngleMotor.getSelectedSensorPosition(), mAngleRotationsPerEngineeringUnit, 2048));
        return new SwerveModuleState(velocity, angle);
    }
}