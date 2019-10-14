import utils.ProgramSettings;
import utils.RoomMonitor;
import web.WebThread;

public class Launcher {

    public static void main(String[] args) {

        try {
            boolean verbose = args[0].equals("true");
            int port = Integer.parseInt(args[1]);

            System.out.print("[INFO] Configuring program settings...");
            ProgramSettings a = new ProgramSettings();
            a.setVerbose(verbose);
            a.setPort(port);
            System.out.println("Done!");

            System.out.print("[INFO] Starting monitoring thread...");
            RoomMonitor roomMonitor = new RoomMonitor();
            roomMonitor.start();
            System.out.println("Done!");

            System.out.print("[INFO] Starting web interface...");
            WebThread webInterface = new WebThread();
            webInterface.start();
            System.out.println("Done!");

        } catch (ArrayIndexOutOfBoundsException a) {
            System.out.println("Command line arguments not specified. (boolean) Verbose Logging (int) Web server Port");
            System.exit(0);
        }
    }

}