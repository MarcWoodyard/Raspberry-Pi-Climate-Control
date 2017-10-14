public class Main {
	public static void main(String[] args) {

		// Command Line Arguments (default values).
		double max = 82.0;
		double min = 70.0;
		int sleepTime = 2;

		if (args.length > 0) {
		    try {
		        max = Double.parseDouble(args[0]);
		        min = Double.parseDouble(args[1]);
			sleepTime = Integer.parseInt(args[2]);
		    } catch (NumberFormatException e) {
		        System.out.println("Arguments must be in double format. [Max Temperature (Double)] [Min Temperature (Double)] [Sleep Time (Integer)]");
		        System.exit(0);
		    }
		} else {
			System.out.println("Please specify a maximun and minimun temperature.");
			System.exit(0);
		}

		Controller a = new Controller(max, min);
		a.alert("AC Controller Starting Up", "Your Raspberry Pi AC controller just started up.");

		while(true) {
			try {
				a.temperatureUpdate();

				if (a.getTemperature() >= a.getMaxTemp()) {
					a.log("[AC ON]", "Turning AC on. Temperature: " + a.getTemperature());
					a.switchAC(); // Turn AC on.

					do {
						a.sleep(sleepTime);
						a.temperatureUpdate();
						a.log("[AC ON]", "Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());

						if(a.getTemperature() >= a.getMaxTemp())
							a.tempWatch();
					} while (a.getTemperature() > a.getMinTemp());

					a.log("[AC OFF]", "Turning AC off.");
					a.switchAC(); // Turn AC off.

					a.sleep(sleepTime);
					do {
						a.temperatureUpdate();
						a.log("[INFO]","Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
						a.tempWatch();
						a.sleep(sleepTime);
					} while(a.getTemperature() <= a.getMinTemp() + 3);
				}

				a.log("[INFO]","Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
				a.sleep(sleepTime);

				// Temporary - Disable during winter months.
				a.tempWatch();
			} catch (Exception e) {
				a.alert("AC Controller ERROR!", "An error occured while the AC Controller was running. \n\nDebug Information:\n----------------\n\n" + e.printStackTrace())
			}	
		}
	}
}
