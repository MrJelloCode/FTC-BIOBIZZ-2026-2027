package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "TeleOp Control", group = "Teleop")

public class TeleOpControlLinearOpMode extends LinearOpMode {



    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;


    // ============================================================
    // INTAKE
    // ============================================================

    private DcMotor intake = null;

    // Motor power:
    //  1.0 = 100% forward
    // -1.0 = 100% reverse
    //  0.0 = stopped

    private static final double INTAKE_IN_POWER = 1.0;
    private static final double INTAKE_OUT_POWER = -0.9;
    private static final double INTAKE_OFF_POWER = 0.0;

    private double intakePower = INTAKE_OFF_POWER;


    // ============================================================
    // CATAPULT
    // ============================================================

    private DcMotor catapult1 = null;
    private DcMotor catapult2 = null;


    /*
     * CATAPULT POSITIONS
     *
     * REST position:
     *      0 encoder ticks
     *
     * FIRE position:
     *      Must be experimentally determined.
     *
     * IMPORTANT:
     * The catapult MUST physically be in its resting position
     * before INIT is pressed because INIT resets the encoders to 0.
     */

    private static final int CATAPULT_REST_POSITION = 0;

    // ============================================================
    // !!! TUNE THIS VALUE ON THE REAL ROBOT !!!
    // ============================================================
    private static final int CATAPULT_FIRE_POSITION = 1000;


    /*
     * Maximum power that RUN_TO_POSITION is allowed to use.
     *
     * The motor controller determines which direction to move
     * based on:
     *
     * current position -> target position
     *
     * Therefore this stays POSITIVE even when returning to 0.
     */
    private static final double CATAPULT_MOVE_POWER = 1.0;


    /*
     * Catapult states:
     *
     * REST
     *      Catapult is at encoder position 0.
     *
     * FIRING
     *      Catapult is travelling toward CATAPULT_FIRE_POSITION.
     *
     * FIRED
     *      Catapult has reached CATAPULT_FIRE_POSITION.
     *
     * RESETTING
     *      Catapult is travelling back toward encoder position 0.
     */
    private enum CatapultMode {
        REST,
        FIRING,
        FIRED,
        RESETTING
    }

    private CatapultMode catapultMode = CatapultMode.REST;


    // ============================================================
    // MAIN OPMODE
    // ============================================================

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initializing");
        telemetry.update();


        // ========================================================
        // HARDWARE MAPPING
        // ========================================================

        // Drive motors
        leftFrontDrive =
                hardwareMap.get(DcMotor.class, "left_front_drive");

        leftBackDrive =
                hardwareMap.get(DcMotor.class, "left_back_drive");

        rightFrontDrive =
                hardwareMap.get(DcMotor.class, "right_front_drive");

        rightBackDrive =
                hardwareMap.get(DcMotor.class, "right_back_drive");


        // Intake
        intake =
                hardwareMap.get(DcMotor.class, "intake");


        // Catapult
        catapult1 =
                hardwareMap.get(DcMotor.class, "catapult1");

        catapult2 =
                hardwareMap.get(DcMotor.class, "catapult2");


        // ========================================================
        // DRIVE MOTOR DIRECTIONS
        // ========================================================

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);

        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);


        // ========================================================
        // INTAKE DIRECTION
        // ========================================================

        intake.setDirection(DcMotor.Direction.FORWARD);


        // ========================================================
        // CATAPULT MOTOR DIRECTIONS
        // ========================================================

        /*
         * The two motors physically face opposite directions.
         *
         * Reverse ONE of them so that giving both motors the
         * SAME encoder target causes them to rotate the
         * catapult in the same physical direction.
         */

        catapult1.setDirection(DcMotor.Direction.REVERSE);
        catapult2.setDirection(DcMotor.Direction.FORWARD);


        // ========================================================
        // ZERO POWER BEHAVIOR
        // ========================================================

        intake.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        catapult1.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        catapult2.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );


        // ========================================================
        // RESET CATAPULT ENCODERS
        // ========================================================

        /*
         * IMPORTANT:
         *
         * The catapult must physically be resting when INIT
         * is pressed.
         *
         * Whatever position it is currently in becomes
         * encoder position 0.
         */

        catapult1.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );

        catapult2.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );


        // Set initial target to resting position
        catapult1.setTargetPosition(
                CATAPULT_REST_POSITION
        );

        catapult2.setTargetPosition(
                CATAPULT_REST_POSITION
        );


        // Enable encoder-controlled movement
        catapult1.setMode(
                DcMotor.RunMode.RUN_TO_POSITION
        );

        catapult2.setMode(
                DcMotor.RunMode.RUN_TO_POSITION
        );


        // Do not move during INIT
        catapult1.setPower(0.0);
        catapult2.setPower(0.0);

        catapultMode = CatapultMode.REST;


        // ========================================================
        // READY
        // ========================================================

        telemetry.addData("Status", "Initialized");

        telemetry.addData(
                "Catapult Position",
                "%d / %d",
                catapult1.getCurrentPosition(),
                catapult2.getCurrentPosition()
        );

        telemetry.addData(
                "Catapult Mode",
                "%s",
                catapultMode
        );

        telemetry.update();


        // ========================================================
        // WAIT FOR START
        // ========================================================

        waitForStart();




        // ========================================================
        // DRIVE POWER VARIABLES
        // ========================================================

        double leftFrontPower = 0;
        double rightFrontPower = 0;
        double leftBackPower = 0;
        double rightBackPower = 0;


        // ========================================================
        // MAIN TELEOP LOOP
        // ========================================================

        while (opModeIsActive()) {

            double max;


            // ====================================================
            // READ DRIVE CONTROLS
            // ====================================================

            /*
             * Same drive controls as original code.
             *
             * axial:
             *      Forward / backward
             *
             * lateral:
             *      Strafing
             *
             * yaw:
             *      Rotation
             */

            double axial =
                    -gamepad1.left_stick_y;

            double lateral =
                    -gamepad1.right_stick_x;

            double yaw =
                    -gamepad1.left_stick_x;


            // ====================================================
            // READ INTAKE CONTROLS
            // ====================================================

            boolean intakeInButton =
                    gamepad1.left_trigger > 0.2;

            boolean intakeOutButton =
                    gamepad1.left_bumper;


            /*
             * If both intake controls are pressed,
             * OUT takes priority.
             */

            if (intakeInButton && intakeOutButton) {
                intakeInButton = false;
            }


            // ====================================================
            // READ CATAPULT CONTROLS
            // ====================================================

            /*
             * RIGHT BUMPER:
             *      FIRE
             *
             * RIGHT TRIGGER:
             *      RESET
             */

            boolean catapultFireButton =
                    gamepad1.right_bumper;

            boolean catapultResetButton =
                    gamepad1.right_trigger > 0.2;


            /*
             * If both controls are pressed at once,
             * RESET takes priority.
             */

            if (catapultFireButton &&
                    catapultResetButton) {

                catapultFireButton = false;
            }


            // ====================================================
            // MECANUM DRIVE CODE
            // ====================================================

            /*
             * Combine axial, lateral, and yaw movement
             * to calculate the power required by each wheel.
             */

            leftFrontPower =
                    axial + lateral + yaw;

            rightFrontPower =
                    axial - lateral - yaw;

            leftBackPower =
                    axial - lateral + yaw;

            rightBackPower =
                    axial + lateral - yaw;


            // ====================================================
            // NORMALIZE DRIVE POWER
            // ====================================================

            /*
             * Motor power must stay between -1 and +1.
             *
             * If any calculated motor power exceeds 1,
             * divide ALL motor powers by the largest value.
             *
             * This preserves the desired movement direction.
             */

            max = Math.max(
                    Math.abs(leftFrontPower),
                    Math.abs(rightFrontPower)
            );

            max = Math.max(
                    max,
                    Math.abs(leftBackPower)
            );

            max = Math.max(
                    max,
                    Math.abs(rightBackPower)
            );


            if (max > 1.0) {

                leftFrontPower /= max;
                rightFrontPower /= max;

                leftBackPower /= max;
                rightBackPower /= max;
            }


            // ====================================================
            // INTAKE CODE
            // ====================================================

            if (intakeInButton) {

                intakePower = INTAKE_IN_POWER;

            } else if (intakeOutButton) {

                intakePower = INTAKE_OUT_POWER;

            } else {

                intakePower = INTAKE_OFF_POWER;
            }


            // ====================================================
            // CATAPULT FIRE COMMAND
            // ====================================================

            if (catapultFireButton) {

                catapult1.setTargetPosition(CATAPULT_FIRE_POSITION);
                catapult2.setTargetPosition(CATAPULT_FIRE_POSITION);


                catapult1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                catapult2.setMode(DcMotor.RunMode.RUN_TO_POSITION);


                catapult1.setPower(CATAPULT_MOVE_POWER);

                catapult2.setPower(CATAPULT_MOVE_POWER);


                catapultMode = CatapultMode.FIRING;
            }
            else if (catapultResetButton) {


                catapult1.setTargetPosition(CATAPULT_REST_POSITION);
                catapult2.setTargetPosition(CATAPULT_REST_POSITION);


                catapult1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                catapult2.setMode(DcMotor.RunMode.RUN_TO_POSITION);



                catapult1.setPower(CATAPULT_MOVE_POWER);

                catapult2.setPower(CATAPULT_MOVE_POWER);


                catapultMode = CatapultMode.RESETTING;
            }



            if (catapultMode == CatapultMode.FIRING) {

                if (!catapult1.isBusy() &&
                        !catapult2.isBusy()) {

                    catapultMode =
                            CatapultMode.FIRED;
                }
            }


            else if (catapultMode ==
                    CatapultMode.RESETTING) {

                if (!catapult1.isBusy() &&
                        !catapult2.isBusy()) {

                    catapultMode =
                            CatapultMode.REST;
                }
            }


            // ====================================================
            // SEND DRIVE MOTOR POWERS
            // ====================================================

            leftFrontDrive.setPower(
                    leftFrontPower
            );

            rightFrontDrive.setPower(
                    rightFrontPower
            );

            leftBackDrive.setPower(
                    leftBackPower
            );

            rightBackDrive.setPower(
                    rightBackPower
            );


            // ====================================================
            // SEND INTAKE POWER
            // ====================================================

            intake.setPower(
                    intakePower
            );


            // ====================================================
            // TELEMETRY
            // ====================================================



            // Drive information
            telemetry.addData(
                    "Front Left/Right",
                    "%4.2f, %4.2f",
                    leftFrontPower,
                    rightFrontPower
            );

            telemetry.addData(
                    "Back Left/Right",
                    "%4.2f, %4.2f",
                    leftBackPower,
                    rightBackPower
            );


            // Intake information
            telemetry.addData(
                    "Intake Power",
                    "%4.2f",
                    intake.getPower()
            );


            // Catapult information
            telemetry.addData(
                    "Catapult Mode",
                    "%s",
                    catapultMode
            );

            telemetry.addData(
                    "Catapult 1 Position",
                    "%d",
                    catapult1.getCurrentPosition()
            );

            telemetry.addData(
                    "Catapult 2 Position",
                    "%d",
                    catapult2.getCurrentPosition()
            );

            telemetry.addData(
                    "Catapult Target",
                    "%d",
                    catapult1.getTargetPosition()
            );

            telemetry.addData(
                    "Catapult 1 Busy",
                    "%s",
                    catapult1.isBusy()
            );

            telemetry.addData(
                    "Catapult 2 Busy",
                    "%s",
                    catapult2.isBusy()
            );


            telemetry.update();
        }
    }
}