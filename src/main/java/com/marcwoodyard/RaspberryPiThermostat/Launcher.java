package com.marcwoodyard.RaspberryPiThermostat;

import com.marcwoodyard.RaspberryPiThermostat.peripherals.DHT11;
import com.marcwoodyard.RaspberryPiThermostat.peripherals.RelaySwitch;
import com.marcwoodyard.RaspberryPiThermostat.utils.Logger;
import com.marcwoodyard.RaspberryPiThermostat.utils.ProgramSettings;
import com.marcwoodyard.RaspberryPiThermostat.web.WebServer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Launcher {

    private static int onCounter = 0;
    private static boolean acOn = false;
    private static int minutesRunning = 0;

    public static void main(String[] args) {

        // Needs to be called first, we need the settings to run the functions below.
        System.out.println("[INFO] Importing Settings...");
        ProgramSettings.importSettings();

        WebServer.startServer(8443);

        while(true) { if(false) break; }

        System.out.println("[INFO] Connecting to DHT11 Sensor...");
        DHT11 dht11 = new DHT11(ProgramSettings.getGpioDHT11());

        System.out.println("[INFO] Starting Web Interface...");
        WebServer.startServer(8443);

        Logger.alert("[INFO] Thermostat Startup Complete", "The thermostat startup processes " +
                "have completed and is now up and running.");

        // Schedule AC filter reset.
        Timer timer = new Timer();
        Calendar filterCalendar = Calendar.getInstance();
        filterCalendar.set(Calendar.HOUR_OF_DAY, 12);

        Calendar minutesRunningCalendar = Calendar.getInstance();
        minutesRunningCalendar.set(Calendar.HOUR_OF_DAY, 0);

        TimerTask filterReset = new TimerTask() {
            @Override
            public void run() {
                RelaySwitch.filterReset();
            }
        };

        TimerTask minutesRunningReset = new TimerTask() {
            @Override
            public void run() {
                minutesRunning = 0;
            }
        };

        // 24*60*60*1000 = 86400000 --> milliseconds in a day.
        timer.schedule(filterReset, filterCalendar.getTime(), 24 * 60 * 60 * 1000);
        timer.schedule(filterReset, minutesRunningCalendar.getTime(), 24 * 60 * 60 * 1000);

        // TODO Verify that AC is off before starting program???

        RelaySwitch.filterReset();

        while (true) {
            try {
                while (true) {
                    dht11.updateData();

                    Logger.add("[INFO]",
                            "Temperature: " + DHT11.getTemperature() +
                                    " Humidity: " + DHT11.getHumidity());

                    if (DHT11.getTemperature() >= ProgramSettings.getMaxTemperature()) {
                        long startTime = System.nanoTime();

                        RelaySwitch.toggleAC();

                        do {
                            // Send alert if AC is running for more than 20 minutes.
                            if (onCounter == 20 || onCounter == 30)
                                Logger.alert("[Error]", "The room is currently " + DHT11.getTemperature() + " and hasn't " +
                                        "gotten colder in " + onCounter + " minutes.");

                            onCounter++;

                            Logger.add("[AC ON]",
                                    "Temperature: " + DHT11.getTemperature() +
                                            " Humidity: " + DHT11.getHumidity());

                            Thread.sleep(60000);

                            dht11.updateData();
                        } while (DHT11.getTemperature() > ProgramSettings.getMinTemperature());

                        RelaySwitch.toggleAC();

                        long duration = System.nanoTime() - startTime;
                        minutesRunning += duration;

                        Logger.add("[AC OFF]",
                                "Temperature: " + DHT11.getTemperature() +
                                        " Humidity: " + DHT11.getHumidity());

                        onCounter = 0;
                    }

                    Thread.sleep(60000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean getACOn() {
        return acOn;
    }

    public static int getMinutesRunning() {
        return minutesRunning;
    }

}
