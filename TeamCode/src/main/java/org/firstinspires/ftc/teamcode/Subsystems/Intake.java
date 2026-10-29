package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private DcMotorEx intakeMotor;
    public Intake(HardwareMap hw){
        intakeMotor = hw.get(DcMotorEx.class,"In");
    }

    public void setPower(double power){
        intakeMotor.setPower(power);
    }

}