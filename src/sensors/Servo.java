package sensors;

import utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Servo {

    // Servo Settings
    private int enablePosition;
    private int restingPosition;
    private int timeout;
    private boolean servoStatus = false;

    // Logging
    private Logger log = new Logger();

    public Servo() {
        try {
            Scanner sc = new Scanner(new File("ServoConfig.ini"));
            this.enablePosition = Integer.parseInt(sc.nextLine());
            this.restingPosition = Integer.parseInt(sc.nextLine());
            this.timeout = Integer.parseInt(sc.nextLine());
        } catch (FileNotFoundException fnf) {
            System.out.println("[ERROR] ServoConfig.ini not found");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void switchAC() {
        try {
            Runtime runTime = Runtime.getRuntime();
            runTime.exec("gpio mode 1 pwm");
            runTime.exec("gpio pwm-ms");
            runTime.exec("gpio pwmc 192");
            runTime.exec("gpio pwmr 2000");
            runTime.exec("gpio pwm 1 " + this.restingPosition); // Resting position

            Thread.sleep(this.timeout);
            runTime.exec("gpio pwm 1 " + this.enablePosition); // Turn on/off
            Thread.sleep(this.timeout);
            runTime.exec("gpio pwm 1 " + this.restingPosition); // Resting position
            Thread.sleep(this.timeout);

            this.servoStatus = !this.servoStatus;
        } catch (Exception e) {
            this.log.alert("AC Controller Servo Error",
                    "The AC controller encountered a servo motor error.\n\nException occurred:\n" + e.getMessage());
        }
    }

    public boolean getServoStatus() {
        return this.servoStatus;
    }
}
