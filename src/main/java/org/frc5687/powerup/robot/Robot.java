package org.frc5687.powerup.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.frc5687.powerup.robot.commands.CarriageZeroEncoder;
import org.frc5687.powerup.robot.commands.auto.AutoAlign;
import org.frc5687.powerup.robot.commands.auto.AutoAlignToSwitch;
import org.frc5687.powerup.robot.commands.auto.AutoGroup;
import org.frc5687.powerup.robot.subsystems.*;
import edu.wpi.first.wpilibj.CameraServer;
import org.frc5687.powerup.robot.utils.AutoChooser;
import org.frc5687.powerup.robot.utils.PDP;

public class Robot extends IterativeRobot  {

    // I really don't like the idea of public static refrences to subsystems...

    private Command autoCommand;

    private OI oi;
    private DriveTrain driveTrain;
    private Intake intake;
    private Carriage carriage;
    private Climber _climber;
    private Arm _arm;
    private AHRS imu;
    private UsbCamera camera;
    private PDP pdp;
    private AutoChooser _autoChooser;

    public Robot() {
    }

    @Override
    public void startCompetition() {
        super.startCompetition();
    }

    @Override
    public void robotInit() {

        imu = new AHRS(SPI.Port.kMXP);
        pdp = new PDP();
        oi = new OI();
        _arm = new Arm(oi);
        driveTrain = new DriveTrain(imu, oi);
        carriage = new Carriage(oi);
        intake = new Intake(oi);
        _climber = new Climber(oi);
        _autoChooser = new AutoChooser();

        try {
            camera = CameraServer.getInstance().startAutomaticCapture(0);
        } catch (Exception e) {
            DriverStation.reportError(e.getMessage(), true);
        }

    }
    public Arm getArm() { return _arm; }
    public DriveTrain getDriveTrain() { return driveTrain; }
    public Carriage getCarriage() { return carriage; }
    public Climber getClimber() { return _climber; }


    @Override
    public void disabledInit() {

    }

    @Override
    public void autonomousInit() {
        imu.reset();
        String gameData = DriverStation.getInstance().getGameSpecificMessage();
        int switchSide = 0;
        int scaleSide = 0;
        if (gameData.length()>0) {
           switchSide = gameData.charAt(0)=='L' ? Constants.AutoChooser.LEFT : Constants.AutoChooser.RIGHT;
        }
        if (gameData.length()>1) {
            scaleSide = gameData.charAt(1)=='L' ? Constants.AutoChooser.LEFT : Constants.AutoChooser.RIGHT;
        }

        int autoPosition = _autoChooser.positionSwitchValue();
        int autoMode = _autoChooser.modeSwitchValue();

        autoCommand = new AutoGroup(autoMode, autoPosition, switchSide, scaleSide, this);

        autoCommand.start();
    }

    @Override
    public void teleopInit() {
        if (autoCommand != null) autoCommand.cancel();
    }

    @Override
    public void testInit() {
    }

    @Override
    public void robotPeriodic() {
        updateDashboard();
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void testPeriodic() {
    }

    public void updateDashboard() {
        pdp.updateDashboard();
        intake.updateDashboard();
        driveTrain.updateDashboard();
        carriage.updateDashboard();
        _arm.updateDashboard();
        _autoChooser.updateDashboard();
    }

}
