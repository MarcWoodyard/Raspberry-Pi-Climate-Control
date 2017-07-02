import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {

		System.out.println("[Launcher] Getting temperature configuration.");

		//Temperature Sensor
		double maxTemp = 0.0;
		double minTemp = 0.0;

		File file = new File("TemperatureConfig.txt");

	    try {
	        Scanner sc = new Scanner(file);

	        int place = 0;

	        while (sc.hasNextDouble() && place <= 1) {
	            double data = sc.nextDouble();

	            switch(place) {
	            	case 0:
	            		maxTemp = data;
	            		break;
	            	case 1:
	            		minTemp = data;
	            		break;
	            	default:
	            		CommunicationModule switchComs = new CommunicationModule();
									switchComs.sendEmail("Temperature Config File Error", "We couldn't parse TemperatureConfig.txt.", switchComs.getToEmail());
	            		break;
	            }
	            place++;
	        }

	        sc.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

		Controller a = new Controller(maxTemp, minTemp);

		a.sendEmail("AC Controller Starting Up", "Your Raspberry Pi AC controller just started up.");

		do {
			a.temperatureUpdate();

			if (a.getTemperature() > maxTemp) {

				long timerStart = System.nanoTime(); //When did the AC turn on?
				a.log("[AC ON] Turning AC on. Temperature: " + a.getTemperature());
				a.switchAC(); //Turn AC on.

				do {
					a.sleep(1);

					a.temperatureUpdate();
					a.log("[AC ON] Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
					a.tempWatch();

				} while (a.getTemperature() > minTemp);

				a.log("[AC OFF] Turning AC off.");
				a.switchAC(); //Turn AC off.
			}

			a.log("[INFO] Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
			a.tempWatch();

			a.sleep(1);

		} while (1 == 1);
	}
}
