package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProgramSettings {

    private static boolean verbose = false;
    private static int port = 2087;
    private static int powerConsumption = Integer.MAX_VALUE;
    private static double pricePerKWH = Double.MAX_VALUE;

    private static RoomMonitor roomMonitor = new RoomMonitor();

    public ProgramSettings() {
        if (powerConsumption == Integer.MAX_VALUE && pricePerKWH == Double.MAX_VALUE) {
            try {
                Scanner sc = new Scanner(new File("PowerConfig.ini"));

                powerConsumption = Integer.parseInt(sc.nextLine());
                pricePerKWH = Double.parseDouble(sc.nextLine());
            } catch (FileNotFoundException fnf) {
                System.out.println("[ERROR] PowerConfig.ini not found");
                System.exit(0);
            }
        }
    }

    public boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(boolean a) {
        verbose = a;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int b) {
        port = b;
    }

    public String getMode() {
        return roomMonitor.getMode();
    }

    public double getTemperature() {
        return roomMonitor.getTemperature();
    }

    public String getPowerConsumption() {
        // (Watts * Hours / Day) / 1000 watts/ 1 kilowatt) * 30 Days
        double result = (powerConsumption * (roomMonitor.getMinutesRunning() / 60.0) / 1000.0) * 30.0;
        return String.format("%.1f", result);
    }

    public String getPowerPrice() {
        // Cost = ((KWH per Month) * (Price per KWH) / 100 cent per $)
        double result = Double.parseDouble(this.getPowerConsumption()) * pricePerKWH / 100;
        return String.format("%.1f", result);
    }

}
