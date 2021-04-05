package com.marcwoodyard.RaspberryPiThermostat.peripherals;

import com.marcwoodyard.RaspberryPiThermostat.utils.ProgramSettings;
import com.pi4j.io.gpio.*;
import com.pi4j.util.CommandArgumentParser;

public class RelaySwitch {

    private static final GpioController gpioController = GpioFactory.getInstance();

    // Toggle AC
    private static Pin toggleACPin = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.getPinByAddress(ProgramSettings.getGpioOnOff()));
    private static GpioPinDigitalOutput toggleACOutput = gpioController.provisionDigitalOutputPin(toggleACPin, "toggle-ac", PinState.HIGH);
    private static boolean acStatus = false;

    // Filter Reset
    private static Pin filterResetPin = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.getPinByAddress(ProgramSettings.getGpioFilterReset()));
    private static GpioPinDigitalOutput filterResetOutput = gpioController.provisionDigitalOutputPin(filterResetPin, "filter-reset", PinState.HIGH);

    public static void toggleAC() {
        try {
            toggleACOutput.low(); // Turn On
            Thread.sleep(300);
            toggleACOutput.high(); // Turn Off
            acStatus = !acStatus;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void filterReset() {
        try {
            filterResetOutput.low(); // Turn On
            Thread.sleep(300);
            filterResetOutput.high(); // Turn Off
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAcStatus() {
        return acStatus;
    }

}
