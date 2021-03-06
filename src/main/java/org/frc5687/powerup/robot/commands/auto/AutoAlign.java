package org.frc5687.powerup.robot.commands.auto;

/**
 * Created by Ben Bernard on 1/14/2018.
 */

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.frc5687.powerup.robot.Constants;
import org.frc5687.powerup.robot.Robot;
import org.frc5687.powerup.robot.subsystems.DriveTrain;
import org.frc5687.powerup.robot.Constants.Auto.Align;

/**
 * Autonomous command to turn to specified angle
 */
public class AutoAlign extends Command implements PIDOutput {

    private PIDController controller;
    private double endTime;
    private double angle;
    private double speed;
    private long _timeout = 2000;

    private double pidOut;

    private long _onTargetSince;
    private long startTimeMillis;
    private long _endTimeMillis;

    private DriveTrain driveTrain;
    private AHRS imu;

    private double _tolerance;

    public AutoAlign(Robot robot, double angle) {
        this(robot, angle, Align.SPEED);
    }

    public AutoAlign(Robot robot, double angle, double speed) {
        this(robot.getDriveTrain(), robot.getIMU(), angle, speed);
    }

    public AutoAlign(DriveTrain driveTrain, AHRS imu, double angle, double speed) {
        this(driveTrain, imu, angle, speed, 2000);
    }

    public AutoAlign(DriveTrain driveTrain, AHRS imu, double angle, double speed, long timeout) {
        this(driveTrain, imu, angle, speed, timeout, Align.TOLERANCE);
    }

    public AutoAlign(Robot robot, double angle, long timeout, double tolerance) {
        this(robot.getDriveTrain(), robot.getIMU(), angle, Align.SPEED, timeout, tolerance);
    }

    public AutoAlign(Robot robot, double angle, double speed, long timeout, double tolerance) {
        this(robot.getDriveTrain(), robot.getIMU(), angle, speed, timeout, tolerance);
    }

    public AutoAlign(DriveTrain driveTrain, AHRS imu, double angle, double speed, long timeout, double tolerance) {
        requires(driveTrain);
        this.angle = angle;
        this.speed = speed;
        this.driveTrain = driveTrain;
        this.imu = imu;
        _timeout = timeout;
        _tolerance = tolerance;
    }

    @Override
    protected void initialize() {
        double kP = Align.kP; // Double.parseDouble(SmartDashboard.getString("DB/String 0", ".04"));
        double kI = Align.kI; // Double.parseDouble(SmartDashboard.getString("DB/String 1", ".006"));
        double kD = Align.kD; //Double.parseDouble(SmartDashboard.getString("DB/String 2", ".09"));

        controller = new PIDController(kP, kI, kD, imu, this, 0.01);
        controller.setInputRange(Constants.Auto.MIN_IMU_ANGLE, Constants.Auto.MAX_IMU_ANGLE);
        controller.setOutputRange(-speed, speed);
        controller.setAbsoluteTolerance(_tolerance);
        controller.setContinuous();
        controller.setSetpoint(angle);
        controller.enable();
        DriverStation.reportError("AutoAlign initialized to " + angle + " at " + speed, false);
        DriverStation.reportError("kP="+kP+" , kI="+kI+", kD="+kD + ",T="+ Align.TOLERANCE, false);
        startTimeMillis = System.currentTimeMillis();
        _endTimeMillis = startTimeMillis + _timeout;
    }

    @Override
    protected void execute() {
        driveTrain.setPower(pidOut, -pidOut, true); // positive output is clockwise
        SmartDashboard.putBoolean("AutoAlign/onTarget", controller.onTarget());
        SmartDashboard.putNumber("AutoAlign/imu", imu.getYaw());
        SmartDashboard.putData("AutoAlign/pid", controller);
    }

    @Override
    protected boolean isFinished() {
        /*
        if (!controller.onTarget()) {
            _onTargetSince = 0;
            return false;
        }
        */

        if(System.currentTimeMillis() >= _endTimeMillis){
            DriverStation.reportError("AutoAlign timed out after " + _timeout + "ms", false);
            return true;
        }

        if (controller.onTarget()) {
            if (_onTargetSince == 0) {
                DriverStation.reportError("AutoAlign reached target " + imu.getYaw(), false);
                _onTargetSince = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() > _onTargetSince + Align.STEADY_TIME) {
                DriverStation.reportError("AutoAlign complete after " + Align.STEADY_TIME + " at " + imu.getYaw(), false);
                return  true;
            }
        }

        return false;
    }

    @Override
    protected void end() {
        driveTrain.setPower(0,0, true);
        DriverStation.reportError("AutoAlign finished: angle = " + imu.getYaw() + ", time = " + (System.currentTimeMillis() - startTimeMillis), false);
        controller.disable();
        DriverStation.reportError("AutoAlign.end() controller disabled", false);
    }

    @Override
    public void pidWrite(double output) {
        pidOut = output;
        //SmartDashboard.putNumber("AutoAlign/pidOut", pidOut);
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}