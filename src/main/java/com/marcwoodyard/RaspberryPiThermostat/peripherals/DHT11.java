package com.marcwoodyard.RaspberryPiThermostat.peripherals;

import com.marcwoodyard.RaspberryPiThermostat.utils.Logger;
import com.marcwoodyard.RaspberryPiThermostat.utils.ProgramSettings;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class DHT11 {
    private static final int MAXTIMINGS = 85;
    private static double temperature = 0.0;
    private static double humidity = 0.0;
    private final int[] dht11_dat = {0, 0, 0, 0, 0};

    public DHT11(int pin) {
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
            return;
        }

        GpioUtil.export(3, GpioUtil.DIRECTION_OUT);
    }

    public static double getTemperature() {
        return temperature;
    }

    public static double getHumidity() {
        return humidity;
    }

    private boolean readSensor() {
        int pin = ProgramSettings.getGpioDHT11();

        int laststate = Gpio.HIGH;
        int j = 0;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(pin, Gpio.HIGH);
        Gpio.pinMode(pin, Gpio.INPUT);

        for (int i = 0; i < MAXTIMINGS; i++) {
            int counter = 0;

            while (Gpio.digitalRead(pin) == laststate) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255)
                    break;
            }

            laststate = Gpio.digitalRead(pin);

            if (counter == 255)
                break;

            //Ignore first 3 transitions.
            if (i >= 4 && i % 2 == 0) {
                dht11_dat[j / 8] <<= 1; //Shove each bit into the storage bytes.

                if (counter > 16)
                    dht11_dat[j / 8] |= 1;

                j++;
            }

        }

        // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte.
        if (j >= 40 && checkParity()) {
            float h = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;

            if (h > 100)
                h = dht11_dat[0]; // for DHT11

            float c = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;

            if (c > 125)
                c = dht11_dat[2]; // for DHT11

            if ((dht11_dat[2] & 0x80) != 0) {
                c = -c;
            }

            final float f = c * 1.8f + 32; // Convert C to F.

            temperature = f;
            humidity = h;
            return true;
        } else
            return false;
    }

    private boolean checkParity() {
        return dht11_dat[4] == (dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3] & 0xFF);
    }

    public void updateData() {
        temperature = 0.0;
        humidity = 0.0;

        boolean result = false;
        int errorCount = 0;

        do {
            try {
                Thread.sleep(3000);
                result = this.readSensor();

                if ((temperature > 132 || temperature < 0) || (humidity > 100 || humidity < 0)) {
                    System.out.println("Data is incorrect");
                    result = false;
                    errorCount++;
                }

                if (errorCount >= 60) {
                    Logger.alert("[ERROR] Can't Get Temperature Data", "We've tried " + errorCount + " times to get take the room temperature but can't.");

                    temperature = 0.0;
                    humidity = 0.0;

                    return;
                }
            } catch (Exception e) {
                Logger.add("[ERROR]", e.toString());
            }
        } while (!result);
    }
}