import utils.Controller;
import utils.Logger;
import utils.ConfigImporter;

public class Main {
  public static void main(String[] args) {
    Logger log = new Logger();
    ConfigImporter config = new ConfigImporter();
    Controller a = new Controller();
    log.alert("AC Controller Starting Up", "Your Raspberry Pi AC controller just started up.");

    int count = 0;
    int offCounter = 0;

    while (true) {
      try {
        a.temperatureUpdate();

        // Room Too Hot ----------------------------------
        if (a.getTemperature() >= a.getMaxTemp()) {
          offCounter = 0;
          log.add("[AC ON]", "Turning AC on. Temperature: " + a.getTemperature());
          a.switchAC();

          do {
            a.sleep(config.getSleepTime());
            a.temperatureUpdate();
            log.add("[AC ON]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");

            // Verify AC On
            if (a.acStatus() == false) {
              log.alert("[INFO]", "Detected AC is still off?");
              a.switchAC();
              count++;
            }

            if (count >= 3) {
              log.alert("[ERROR] Can't Turn AC On", "We're having trouble turning the AC on.");
              a.newServo();
              count = 0;
            }
          } while (a.getTemperature() > a.getMinTemp());

          count = 0;
          log.add("[AC OFF]", "Turning AC off. Temperature: " + a.getTemperature());
          a.switchAC();

          log.add("[INFO]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");
          a.sleep(config.getSleepTime());

          // Verify AC Off
          while(a.acStatus() == true) {
            log.alert("[ERROR] Can't Turn AC Off", "We're having trouble turning the AC off.");
            a.switchAC();
            offCounter++;
            a.sleep(config.getSleepTime());

            if (offCounter >= 5) {
              log.alert("[ERROR] New Servo Created", ".");
              a.newServo();
              offCounter = 0;
            }
          }
        }

        log.add("[INFO]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");
        a.sleep(config.getSleepTime());
      } catch (Exception e) {
        log.alert("AC Controller ERROR!",
            "An error occured while the AC Controller was running. \n\nDebug Information:\n----------------\n\n"
                + e.getStackTrace());
      }
    }
  }
}
