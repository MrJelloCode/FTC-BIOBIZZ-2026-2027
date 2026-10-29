package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.TeleOpControlLinearOpMode;

public class Catapult {
    private DcMotorEx catapult1, catapult2;

    public Catapult(HardwareMap hw){
        catapult1 = hw.get(DcMotorEx.class, "c1");
        catapult2 = hw.get(DcMotorEx.class, "c2");

        catapult1.setDirection(DcMotorSimple.Direction.FORWARD);
        catapult2.setDirection(DcMotorSimple.Direction.REVERSE);

        catapult1.setTargetPosition(Constants.Catapult.CATAPULT_REST_POSITION);
        catapult2.setTargetPosition(Constants.Catapult.CATAPULT_REST_POSITION);

        // Special Mode that is needed
        catapult1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        catapult2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.setPower(0);
    }

    public void setPower(double power){
        catapult1.setPower(power);
        catapult2.setPower(power);
    }

    public void fireSequence(){
        catapult1.setTargetPosition(Constants.Catapult.CATAPULT_FIRE_POSITION);
        catapult2.setTargetPosition(Constants.Catapult.CATAPULT_FIRE_POSITION);


        catapult1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        catapult2.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        catapult1.setPower(Constants.Catapult.CATAPULT_MOVE_POWER);
        catapult2.setPower(Constants.Catapult.CATAPULT_MOVE_POWER);
    }

    public void resetSequence(){
        catapult1.setTargetPosition(Constants.Catapult.CATAPULT_REST_POSITION);
        catapult2.setTargetPosition(Constants.Catapult.CATAPULT_REST_POSITION);


        catapult1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        catapult2.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        catapult1.setPower(Constants.Catapult.CATAPULT_MOVE_POWER/2);
        catapult2.setPower(Constants.Catapult.CATAPULT_MOVE_POWER/2);
    }









}
