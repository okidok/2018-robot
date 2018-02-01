package org.frc5687.powerup.robot;

public class Constants {

    public class DriveTrain {
        public static final double DEADBAND = 0.15;
        public static final boolean LEFT_MOTORS_INVERTED = true;
        public static final boolean RIGHT_MOTORS_INVERTED = false;
    }

    public class Intake {
        public static final double DEADBAND = 0.05;
        public static final boolean LEFT_MOTORS_INVERTED = true;
        public static final boolean RIGHT_MOTORS_INVERTED = false;
        public static final double OUTTAKE_SPEED = -0.75;
    }

    public class Carriage {
        public static final double DEADBAND = 0.13;
        public static final boolean MOTOR_INVERTED = true;
        public static final int ENCODER_TOP = 967;
        public static final double RUNWAY = 25.5; // in
    }

    public class Arm {
        public static final double ENCODER_START = 0;
        public static final double ENCODER_MIDDLE = 133;
        public static final double ENCODER_TOP = 340;

        public static final double HOLD_SPEED = 0.15;
    }

}
