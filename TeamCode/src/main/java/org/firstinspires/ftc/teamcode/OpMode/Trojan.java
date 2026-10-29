package org.firstinspires.ftc.teamcode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.Catapult;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Mecanum;

public class Trojan extends OpMode {

    // ------ SUBSYSTEMS -----

    private Mecanum drivetrain;
    private Intake intake;
    private Catapult catapult;

    private double slowMode = 1.0;
    private double slowTurn = 0.8;


    @Override
    public void init() {
        drivetrain  = new Mecanum(hardwareMap);
        intake = new Intake(hardwareMap);
        catapult = new Catapult(hardwareMap);
    }

    @Override
    public void loop() {

        if (gamepad1.right_trigger > 0.1){
            slowMode = 0.5;
            slowTurn = 0.3;
        } else {
            slowMode = 1.0;
            slowTurn = 0.8;
        };

        if(gamepad1.dpad_up){
            drivetrain.reset();
        }

        drivetrain.fieldCentricDrive(gamepad1, slowMode, slowTurn);

        intake.setPower(-gamepad2.right_stick_y);

        boolean catapultFireButton = gamepad1.right_trigger > 0.2;
        boolean catapultResetButton = gamepad1.left_trigger > 0.2;

        if (catapultFireButton && catapultResetButton) {
            catapultFireButton = false;
        }

        if(catapultFireButton){
            catapult.fireSequence();
        }else if(catapultResetButton){
            catapult.resetSequence();
        }



    }
}
