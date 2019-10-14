package utils;

import sensors.DHT11;
import sensors.RaspiStill;
import sensors.Servo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RoomMonitor extends Thread {

    private static Logger log = new Logger();

    private static Servo servo = new Servo();

    // Temperature Sensor
    private static DHT11 tempSensor = new DHT11();
    private static double curTemp;
    private static double curHumidity;
    private static double waitTime;
    private static double maxTemperature;
    private static double minTemperature;

    // Image Analyzer
    private static RaspiStill raspStill = new RaspiStill();
    private static ImageAnalyzer imgAnalyzer = new ImageAnalyzer();
    private static String onColor;

    private static boolean isRunning; // Is monitoring thread running
    private static int minutesRunning = 0;
    private static int minutesRunningToday = 0;
    private static Timer resetMinutesRunning = null;

    public RoomMonitor() {

        // Import settings the first time object created.
        if (waitTime == 0.0d && maxTemperature == 0.0d && minTemperature == 0.0d) {
            try {
                Scanner sc = new Scanner(new File("TemperatureSettings.ini"));

                waitTime = Double.parseDouble(sc.nextLine());
                maxTemperature = Double.parseDouble(sc.nextLine());
                minTemperature = Double.parseDouble(sc.nextLine());
            } catch (FileNotFoundException fnf) {
                System.out.println("[ERROR] TemperatureSettings.ini not found");
                System.exit(0);
            }
        }

        if (onColor == null) {
            try {
                Scanner sc = new Scanner(new File("RaspstillConfig.ini"));

                onColor = sc.nextLine();
            } catch (FileNotFoundException fnf) {
                System.out.println("[ERROR] RaspstillConfig.ini not found");
                System.exit(0);
            }
        }

        if (resetMinutesRunning == null)
            this.setupMinutesRunningReset();
    }

    @Override
    public void run() {
        if (isRunning) {
            log.add("[ERROR]", "Cannot create another RoomMonitor thread, one is already running.");
            System.exit(0);
        } else
            isRunning = true;

        log.alert("AC Controller Starting Up", "Your room temperature monitor just started up.");
        this.sleep(5000.0);

        while (true) {
            try {
                this.temperatureUpdate();

                if (this.getTemperature() >= this.getMaxTemp()) { // Turn on
                    log.add("[AC ON]", "Turning AC on. Temperature: " + this.getTemperature());
                    servo.switchAC();

                    int rounds = 0;

                    do {
                        verifyACOn(true);
                        minutesRunning += waitTime;
                        this.sleep((int) waitTime);
                        this.temperatureUpdate();
                        log.add("[AC ON]", "Temperature: " + (int) this.getTemperature() + "°F Humidity: " + (int) this.getHumidity() + "%");
                        rounds++;

                        if (rounds == 6)
                            log.alert("Room Temperature Not Changing", "The room is not getting colder.");
                        else if (rounds > 6)
                            rounds = 0;
                    } while (this.getTemperature() >= this.getMinTemp()); // Turn off

                    log.add("[AC OFF]", "Turning AC off. Temperature: " + this.getTemperature());
                    servo.switchAC();
                    verifyACOn(false);
                }

                logACOff();
                this.sleep((int) waitTime);
            } catch (Exception e) {
                log.alert("AC Controller ERROR!",
                        "An error occurred while the AC Controller was running. \n\nDebug Information:\n----------------\n\n"
                                + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void verifyACOn(boolean status) { // True = verify on, False = verify off.
        int errorCount = 0;
        boolean sendResolved = false;

        if (status)
            log.add("[AC ON]", "Checking status...");
        else if (!status)
            log.add("[AC OFF]", "Checking status...");

        do {
            servo.switchAC();
            errorCount++;
            this.sleep(2);

            if (errorCount > 2) {
                if (status)
                    log.alert("[ERROR] Can't Turn AC Off", "We're having trouble turning the AC off.");
                else if (!status)
                    log.alert("[ERROR] Can't Turn AC On", "We're having trouble turning the AC on.");

                servo = new Servo();
                errorCount = 0;
                sendResolved = true; // When we get out of this loop, send resolved email.
            }
        } while (this.acStatus() == status);

        if (sendResolved)
            onOffResolved();

        log.add("[AC OFF]", "AC status = off.");
    }

    private void logACOff() {
        log.add("[INFO]", "Temperature: " + (int) this.getTemperature() + "°F Humidity: " + (int) this.getHumidity() + "%");
        this.sleep(waitTime);
    }

    private void onOffResolved() {
        log.alert("[RESOLVED] AC On/Off Resolved",
                "We've resolved the AC on/off issue. Please" + "check the log for more details.");
    }

    private void sleep(int minutes) {
        this.sleep(Integer.valueOf(minutes * 60 * 1000).doubleValue()); // Minutes to sleep * seconds to a minute * milliseconds to a second.
    }

    private void sleep(Double milliseconds) {
        try {
            Thread.sleep(Math.round(milliseconds));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void temperatureUpdate() {
        tempSensor.readSensor();
        curTemp = tempSensor.getTemperature();
        curHumidity = tempSensor.getHumidity();
    }

    public double getTemperature() {
        return curTemp;
    }

    public double getHumidity() {
        return curHumidity;
    }

    public double getMaxTemp() {
        return maxTemperature;
    }

    public double getMinTemp() {
        return minTemperature;
    }

    public String getMode() {
        if (servo.getServoStatus())
            return "Cooling";

        return "Monitoring";
    }

    public int getMinutesRunning() {
        return minutesRunningToday;
    }

    private boolean acStatus() { // True = AC On, False = AC Off
        raspStill.takePicture();
        sleep(5000.0);
        TreeMap<Integer, Integer> colorMap = imgAnalyzer.mapPixels(new File("image.jpg"));
        sleep(5000.0);
        ArrayList<String> colorArray = imgAnalyzer.extractColors(colorMap);

        // Convert everything to lowercase to make sure it matches what's in RaspstillConfig.ini
        for (String s : colorArray) {
            if (s.toLowerCase().equalsIgnoreCase(onColor))
                return true;
        }

        return false;
    }

    private void setupMinutesRunningReset() {
        resetMinutesRunning = new Timer();

        TimerTask timerTask = new TimerTask() {
            public void run() {
                Calendar calendar = new GregorianCalendar();

                if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
                    calendar.setTime(new Date());
                    minutesRunningToday = minutesRunning;
                    minutesRunning = 0;
                }
            }
        };

        resetMinutesRunning.schedule(timerTask, 45 * 60 * 1000); // Run every 45 minutes.
    }

    public void shutdown() {
        log.alert("[SHUTDOWN] AC Controller Shutting Down", "The AC Controller is shutting down.");

        do {
            if (servo.getServoStatus())
                servo.switchAC();
            else if (this.acStatus())
                servo.switchAC();
        } while (this.acStatus());

        System.exit(0);
    }

}
