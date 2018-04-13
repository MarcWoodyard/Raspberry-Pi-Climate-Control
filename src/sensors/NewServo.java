import java.util.Scanner;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GpioController gpioFactory = GpioFactory.getInstance();
        GpioPinDigitalOutput myServo =  gpioFactory.provisionDigitalOutputPin(
                    RaspiPin.GPIO_07, PinState.LOW);

        // Servo Specs: https://bit.ly/2IxXSdz

        //Input of hz and duty cycle
        System.out.println("Hz:");
        Scanner scanner = new Scanner(System.in);
        float hz = scanner.nextFloat();
        System.out.println("High in ms:");
        float highTime = scanner.nextFloat();
        scanner.close();

        //Calculate GPIO low time: hz period time - duty time
        float lowTime = 1000 / hz - highTime;

        while (true) {
            myServo.high();
            long upMs = new Float(highTime).longValue(); // Up time miliseconds
            int upNanos = new Float(highTime * 1000000 % 1000000).intValue(); // Up time nanoseconds
            java.lang.Thread.sleep(upMs, upNanos);

            myServo.low();
            long lowMs = new Float(lowTime).longValue();
            int lowNanos = new Float(lowTime * 1000000 % 1000000).intValue();

            java.lang.Thread.sleep(lowMs, lowNanos);

        }
    }
}
