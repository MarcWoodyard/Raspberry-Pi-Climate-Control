package utils;

import java.io.File;

import java.util.Scanner;

public class ConfigImporter {

    // Servo Configuration
    private static String servoSleep;
    private static String switchButton;
    private static Scanner servoScanner;
    private static String restingPosition;

    // DHT11 Configuration
    private static String gpioPin;
    private static Scanner dht11Scanner;

    // Raspstill Configuration
    private static String onColor;
    private static String offColor;
    private static Scanner raspstillScanner;

    // General Program Configuration
    private static String sleepTime;
    private static String maxTemp;
    private static String minTemp;
    private static Scanner programScanner;

    // Error Reporting
    private static Logger log = new Logger();

    public ConfigImporter() {
        if (switchButton == null || restingPosition == null || servoSleep == null)
            this.importServoConfig();

        if (gpioPin == null)
            this.importDHT11Config();

        if (onColor == null || offColor == null)
            this.importRaspstillConfig();

        if (sleepTime == null)
            this.importProgramConfig();
    }

    /**
     * Imports servo configuration settings.
     */
    private void importServoConfig() {
        try {
            servoScanner = new Scanner(new File("./src/Config/ServoConfig.txt"));
            switchButton = servoScanner.nextLine();
            restingPosition = servoScanner.nextLine();
            servoSleep = servoScanner.nextLine();
        } catch (Exception e) {
            log.alert("Servo Import Error", "Error importing servo configuration.");
        }
    }

    /**
     * Imports DHT11 temperature sensor configuration settings.
     */
    private void importDHT11Config() {
        try {
            dht11Scanner = new Scanner(new File("./src/Config/DHT11Config.txt"));
            gpioPin = dht11Scanner.nextLine();
        } catch (Exception e) {
            log.alert("DHT11 Improt Error", "Error importing DHT11 configuration.");
        }
    }

    /**
     * Imports RaspStill confguration settings.
     */
    private void importRaspstillConfig() {
        try {
            raspstillScanner = new Scanner(new File("./src/Config/RaspstillConfig.txt"));
            onColor = raspstillScanner.nextLine();
            offColor = raspstillScanner.nextLine();
        } catch (Exception e) {
            log.alert("RaspStill Import Error", "Error importing Raspstill configuration.");
        }
    }

    /**
     * Imports the program's configuration settings.
     */
    private void importProgramConfig() {
        try {
            programScanner = new Scanner(new File("./src/Config/ProgramConfig.txt"));
            sleepTime = programScanner.nextLine();
            maxTemp = programScanner.nextLine();
            minTemp = programScanner.nextLine();
        } catch (Exception e) {
            log.alert("Program Import Error", "Error importing program configuration.");
        }
    }

    /**
     * Returns the servo position to enable the AC.
     * @return int - Servo position to enable AC.
     */
    public int getSwitchButton() {
        return Integer.parseInt(switchButton);
    }

    /**
     * Returns the servo position while not active.
     * @return int - Servo position while not active.
     */
    public int getRestingPosition() {
        return Integer.parseInt(restingPosition);
    }

    /**
     * Returns the number of miliseconds to wait while the servo is turning the AC on.
     * @return int - Miliseconds to wait for servo to turn the AC on.
     */
    public int getServoSleep() {
        return Integer.parseInt(servoSleep);
    }

    /**
     * Returns the GPIO pin the DHT11 temperature sensor is attached to.
     * @return int - GPIO pin DHT11 sensor is attached to.
     */
    public int getDHT11GPIO() {
        return Integer.parseInt(gpioPin);
    }

    /**
     * Returns the color that is produced by the AC interface while on.
     * @return String - color of AC interface while off.
     */
    public String getOnColor() {
        return onColor;
    }

    /**
     * Returns the color that is produced by the AC interface while off.
     * @return String - color of AC interface while off.
     */
    public String getOffColor() {
        return offColor;
    }

    /**
     * Returns the amount of minutes the program should wait before checking the room temperature.
     * @return int - Amount of minutes program will wait before checking the temperature.
     */
    public int getSleepTime() {
        return Integer.parseInt(sleepTime);
    }

    /**
     * Returns the max temperature the room can get that will turn on the air conditioner.
     * @return int - Max temperature that will turn on the AC.
     */
    public int getMaxTemp() {
        return Integer.parseInt(maxTemp);
    }

    /**
     * Returns the min temperature the room can get that will turn off the air conditioner.
     * @return int - Min temperature that will turn off the AC.
     */
    public int getMinTemp() {
        return Integer.parseInt(minTemp);
    }

}