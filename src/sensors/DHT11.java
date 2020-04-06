package sensors;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DHT11 {
    private static final int MAXTIMINGS = 85;
    private final int[] dht11_dat = {0, 0, 0, 0, 0};
    private int pin = 0;
    private Logger log = new Logger();

    private double temperature;
    private double humidity;

    public DHT11() {
        try {
            Scanner sc = new Scanner(new File("DHT11.ini"));
            this.pin = Integer.parseInt(sc.nextLine());
        } catch (FileNotFoundException fnf) {
            System.out.println("[ERROR] DHT11.ini not found");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        if (Gpio.wiringPiSetup() == -1) {
            this.log.alert("[ERROR] GPIO setup failed.", "An error occurred when creating a DHT11 object");
            this.log.add("[ERROR]", "GPIO setup failed.");
            return;
        }

        GpioUtil.export(3, GpioUtil.DIRECTION_OUT);
    }

    /**
     * Updates the temperature and humidity data from the sensor.
     */
    private boolean readSensor() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            final float f = c * 1.8f + 32;
            this.temperature = f;
            this.humidity = h;
            return true;
        } else
            return false;
    }

    /**
     * Returns the humidity from the sensor.
     *
     * @return Boolean - True of false depending of temperature data array.
     */
    private boolean checkParity() {
        return dht11_dat[4] == (dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3] & 0xFF);
    }

    public void updateData() {
        this.temperature = 0.0;
        this.humidity = 0.0;

        boolean result = false;
        int errorCount = 0;

        do {
            try {
                result = this.readSensor();

                if ((this.temperature > 132 || this.temperature < 0) || (this.humidity > 100 || this.humidity < 0)) {
                    System.out.println("Data is incorrect");
                    result = false;
                    errorCount++;
                }

                if (errorCount >= 60) {
                    log.alert("[ERROR] Can't Get Temperature Data", "We've tried " + errorCount + " times to get take the room temperature but can't.");

                    this.temperature = 0.0;
                    this.humidity = 0.0;

                    return;
                }
            } catch (Exception e) {
                this.log.add("[ERROR]", e.toString());
            }
        } while (!result);
    }

    /**
     * Returns the temperature from the sensor.
     *
     * @return Double - temperature as a double.
     */
    public double getTemperature() {
        return this.temperature;
    }

    /**
     * Returns the humidity from the sensor.
     *
     * @return Double - humidity as a double.
     */
    public double getHumidity() {
        return this.humidity;
    }


}