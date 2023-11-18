package org.aceshigh176.frc2022;

import com.ctre.phoenix.sensors.Pigeon2;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import org.aceshigh176.frc2022.drivers.SwerveModule;
import org.aceshigh176.lib.util.UnitUtil;
import org.aceshigh176.lib.util.Util;

import java.util.Arrays;

public class Robot extends TimedRobot {

    SwerveModule mSwerveModuleFrontLeft;
    SwerveModule mSwerveModuleBackLeft;
    SwerveModule mSwerveModuleBackRight;
    SwerveModule mSwerveModuleFrontRight;
    Pigeon2 mPigeon2;

    PS4Controller mPs4Controller = new PS4Controller(1);

    private SwerveDriveKinematics sdk = new SwerveDriveKinematics(
            Arrays.asList(
                    new Translation2d(SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0, SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0),
                    new Translation2d(-SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0, SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0),
                    new Translation2d(-SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0, -SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0),
                    new Translation2d(SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0, -SwerveMinibotConstants.physicalRobotConstants.trackWidthMeters / 2.0)
            ).toArray(new Translation2d[]{})
    );

    @Override
    public void robotInit() {
        mSwerveModuleFrontLeft = new SwerveModule(1, SwerveMinibotConstants.swerveConstants.frontLeft);
        mSwerveModuleBackLeft = new SwerveModule(1, SwerveMinibotConstants.swerveConstants.backLeft);
        mSwerveModuleBackRight = new SwerveModule(1, SwerveMinibotConstants.swerveConstants.backRight);
        mSwerveModuleFrontRight = new SwerveModule(1, SwerveMinibotConstants.swerveConstants.frontRight);
        mPigeon2 = new Pigeon2(30, "cancan");

        mSwerveModuleFrontLeft.resetToAbsolute();
        mSwerveModuleBackLeft.resetToAbsolute();
        mSwerveModuleBackRight.resetToAbsolute();
        mSwerveModuleFrontRight.resetToAbsolute();
    }

    int i = 0;

    @Override
    public void robotPeriodic() {
        double timestamp = Timer.getFPGATimestamp();
    }

    int moduleSelection = 0;

    @Override
    public void teleopPeriodic() {
        SwerveModuleState[] states = sdk.toSwerveModuleStates(
                new ChassisSpeeds(
                        UnitUtil.inchesToMeters(Util.deadband(mPs4Controller.getLeftX(), 0.05) * SwerveMinibotConstants.swerveConstants.maxSpeed),
                        UnitUtil.inchesToMeters(Util.deadband(mPs4Controller.getLeftY() * -1, 0.05) * SwerveMinibotConstants.swerveConstants.maxSpeed),
                        Util.deadband(mPs4Controller.getRightX(), 0.05)));

        mSwerveModuleFrontLeft.setDesiredState(states[0], true);
        mSwerveModuleBackLeft.setDesiredState(states[1], true);
        mSwerveModuleBackRight.setDesiredState(states[2], true);
        mSwerveModuleFrontRight.setDesiredState(states[3], true);
    }

    @Override
    public void testPeriodic() {
        double driveDemand = mPs4Controller.getLeftY() * -1;
        double angleDemand = mPs4Controller.getRightY() * -1;

        if(mPs4Controller.getCrossButtonPressed()) {
            moduleSelection++;
            moduleSelection = moduleSelection % 4;
            System.out.println("Now selected module " + moduleSelection);
        }

        mSwerveModuleFrontLeft.setManualOutputs(driveDemand * Util.bool2int(moduleSelection == 0), angleDemand * Util.bool2int(moduleSelection == 0));
        mSwerveModuleBackLeft.setManualOutputs(driveDemand * Util.bool2int(moduleSelection == 1), angleDemand * Util.bool2int(moduleSelection == 1));
        mSwerveModuleBackRight.setManualOutputs(driveDemand * Util.bool2int(moduleSelection == 2), angleDemand * Util.bool2int(moduleSelection == 2));
        mSwerveModuleFrontRight.setManualOutputs(driveDemand * Util.bool2int(moduleSelection == 3), angleDemand * Util.bool2int(moduleSelection == 3));
    }

    @Override
    public void disabledInit() {
        mSwerveModuleFrontLeft.setManualOutputs(0, 0);
        mSwerveModuleBackLeft.setManualOutputs(0, 0);
        mSwerveModuleBackRight.setManualOutputs(0, 0);
        mSwerveModuleFrontRight.setManualOutputs(0, 0);
    }
}
