package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.robotcore.external.navigation.*;


public class Mecanum {
    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    //Constructor
    public Mecanum(HardwareMap hw) {
        frontLeft = hw.get(DcMotorEx.class, "BR"); // TODO
        frontRight = hw.get(DcMotorEx.class, "FR"); // TODO
        backLeft = hw.get(DcMotorEx.class, "BL"); // TODO
        backRight = hw.get(DcMotorEx.class, "FL"); // TODO


        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD); // TODO
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD); // TODO
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE); // TODO
        backRight.setDirection(DcMotorSimple.Direction.REVERSE); // TODO


        imu = hw.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,  //TODO
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)); // TODO
        imu.initialize(parameters);
    }

    public void reset(){
        imu.resetYaw();
    }
    public void fieldCentricDrive(Gamepad gamepad, double slowMode, double slowTurn) {
        double y = -gamepad.left_stick_y;
        double x = gamepad.left_stick_x * 1.1;
        double rx = gamepad.right_stick_x * slowTurn;


        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
        rotX *= 1.1;


        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);

        if(gamepad.start)imu.resetYaw();


        frontLeft.setPower((rotY + rotX + rx) / denominator * slowMode);
        backLeft.setPower((rotY - rotX + rx) / denominator * slowMode);
        frontRight.setPower((rotY - rotX - rx) / denominator * slowMode);
        backRight.setPower((rotY + rotX - rx) / denominator * slowMode);
    }

    public void robotCentricDrive(Gamepad gamepad, double slowMode, double slowTurn){

        double y = -gamepad.left_stick_y; // Remember, Y stick value is reversed
        double x = gamepad.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);

    }


}