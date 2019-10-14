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
        // Import settings the first time object created.
        if (pin == 0) {
            try {
                Scanner sc = new Scanner(new File("DHT11.ini"));
                this.pin = Integer.parseInt(sc.nextLine());
            } catch (FileNotFoundException fnf) {
                System.out.println("[ERROR] DHT11.ini not found");
                System.exit(0);
            }
        }

        if (Gpio.wiringPiSetup() == -1) {
            this.log.alert("[ERROR] GPIO setup failed.", "An error occurred when creating a DHT11 object");
            this.log.add("[ERROR]", "GPIO setup failed.");
            return;
        }

        GpioUtil.export(3, GpioUtil.DIRECTION_OUT);
    }

    public void readSensor() {
        int laststate = Gpio.HIGH;
        int j = 0;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

        Gpio.pinMode(this.pin, Gpio.OUTPUT);
        Gpio.digitalWrite(this.pin, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(this.pin, Gpio.HIGH);
        Gpio.pinMode(this.pin, Gpio.INPUT);

        for (int i = 0; i < MAXTIMINGS; i++) {
            int counter = 0;
            while (Gpio.digitalRead(this.pin) == laststate) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            laststate = Gpio.digitalRead(this.pin);

            if (counter == 255) {
                break;
            }

            try {
                /* ignore first 3 transitions */
                if (i >= 4 && i % 2 == 0) {
                    /* shove each bit into the storage bytes */
                    dht11_dat[j / 8] <<= 1;
                    if (counter > 16) {
                        dht11_dat[j / 8] |= 1;
                    }
                    j++;
                }
            } catch (Exception e) {
                log.add("[ERROR]", "Error reading temperature from GPIO pins.");

                try {
                    Thread.sleep(1000);
                } catch (Exception f) {
                    e.printStackTrace();
                }

                this.readSensor();
            }
        }
        // check we read 40 bits (8bit x 5 ) + verify checksum in the last
        // byte
        if (j >= 40 && checkParity()) {
            float h = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;
            if (h > 100) {
                h = dht11_dat[0]; // for DHT11
            }
            float c = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;
            if (c > 125) {
                c = dht11_dat[2]; // for DHT11
            }
            if ((dht11_dat[2] & 0x80) != 0) {
                c = -c;
            }

            //final float f = c * 1.8f + 32;

            this.temperature = c * 1.8f + 32;
            this.humidity = h;
        } else {
            try {
                Thread.sleep(1000);
                this.readSensor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkParity() {
        return dht11_dat[4] == (dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3] & 0xFF);
    }

    public double getTemperature() {
        return this.temperature;
    }

    public double getHumidity() {
        return this.humidity;
    }

}