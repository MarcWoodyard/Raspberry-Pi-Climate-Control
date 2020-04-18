import sensors.DHT11;
import sensors.RaspiStill;
import sensors.Servo;
import utils.Logger;
import utils.ProgramSettings;
import utils.RoomMonitor;
import web.WebThread;

public class Launcher {

    public static void main(String[] args) {
        try {
            boolean verbose = args[0].equals("true");
            int port = Integer.parseInt(args[1]);
            boolean testSensors = args[2].equals("true");

            if (testSensors) {
                try {
                    Servo servo = new Servo();
                    System.out.println("[TEST] Testing servo motor...");
                    servo.switchAC();

                    System.out.println("[TEST] Testing temperature sensor...");
                    DHT11 dht11 = new DHT11();
                    dht11.updateData();
                    System.out.println(dht11.getTemperature());

                    System.out.println("[TEST] Testing camera...");
                    new RaspiStill().takePicture();

                    System.out.println("[EXIT] Test finished.");
                } catch (Exception e) {
                    System.out.println("[ERROR] Test Failed. " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.print("[INFO] Configuring program settings...");
            ProgramSettings a = new ProgramSettings();
            a.setVerbose(verbose);
            a.setPort(port);
            System.out.println("Done!");

            System.out.print("[INFO] Starting web interface...");
            WebThread webInterface = new WebThread();
            webInterface.start();
            System.out.println("Done!");

            //System.out.print("[INFO] Monitoring process started.");
            System.out.println("[INFO] Monitoring process started.");
            RoomMonitor roomMonitor = new RoomMonitor();
            roomMonitor.run();
        } catch (ArrayIndexOutOfBoundsException a) {
            System.out.println("Command line arguments not specified. (boolean) Verbose Logging (int) Web server Port (boolean) Test servo");
            System.exit(0);
        } catch (Exception e) {
            new Logger().alert("Launcher.java Error", e.getMessage());
            e.printStackTrace();
        }
    }
}