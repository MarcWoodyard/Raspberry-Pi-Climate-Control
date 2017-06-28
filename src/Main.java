import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main {
	public static void main(String[] args) {
		final double MAX_TEMP = 84.0;
		final double MIN_TEMP = 76.0;
		final long MAX_AC_RUNTIME = 959999999999L; //16 minutes.
		Controller a = new Controller(MAX_TEMP, MIN_TEMP);
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a yyyy/MM/dd");

		long elapsedTime = 0;
		long totalTime = 0;

		do {
			a.temperatureUpdate();

			if (a.getTemperature() > MAX_TEMP) {

				long timerStart = System.nanoTime(); //When did the AC turn on?
				System.out.println("[AC ON] [" + dateFormat.format(new Date()) + "] Turning AC on.");
				a.switchAC(); //Turn AC on.

				do {
					System.out.println("[AC ON] [" + dateFormat.format(new Date()) + "] Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());

					a.sleep(1);

					a.temperatureUpdate();
					a.tempWatch();

					//Room not getting hotter, but not getting that much cooler.
					elapsedTime = System.nanoTime() - timerStart;
					totalTime += elapsedTime;
					/*
					if (totalTime >= MAX_AC_RUNTIME)
						break;
					*/

				} while (a.getTemperature() > MIN_TEMP);

				System.out.println("[AC OFF] [" + dateFormat.format(new Date()) + "] Turning AC off.");
				a.switchAC(); //Turn AC off.
				elapsedTime = 0;
				totalTime = 0;
			}

			System.out.println("[INFO] [" + dateFormat.format(new Date()) + "] Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
			a.tempWatch();

			a.sleep(1);

		} while (1 == 1);
	}
}
