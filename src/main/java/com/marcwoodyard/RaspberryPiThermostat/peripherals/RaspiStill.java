package com.marcwoodyard.RaspberryPiThermostat.peripherals;

/**
 * @author robmcm
 * This class is a very simple Java wrapper for the raspistill executable,
 * which makes it easier to take photos from a Java application. Note that
 * there are considerably more parameters available for raspistill which
 * could be added to this class. (e.g. Shutter Speed, ISO, AWB, etc.)
 * <p>
 * https://blogs.msdn.microsoft.com/robert_mcmurray/2015/06/12/simple-java-wrapper-class-for-raspistill-on-the-raspberry-pi-2/
 */

public class RaspiStill {

    // Define the path to the raspistill executable.
    private static final String raspistillPath = "/opt/vc/bin/raspistill";
    private static final int picTimeout = 5000;
    private static final int picQuality = 100;
    private static int picWidth = 1024;
    private static int picHeight = 768;

    public static void takePicture(String picName, String picType) {
        try {
            // Determine the image type based on the file extension (or use the default).
            if (picName.indexOf('.') != -1)
                picType = picName.substring(picName.lastIndexOf('.') + 1);

            StringBuilder sb = new StringBuilder(raspistillPath);

            // Add parameters for no preview and burst mode.
            sb.append(" -n -bm");
            // Configure the camera timeout.
            sb.append(" -t " + picTimeout);
            // Configure the picture width.
            sb.append(" -w ").append(picWidth);
            // Configure the picture height.
            sb.append(" -h ").append(picHeight);
            // Configure the picture quality.
            sb.append(" -q " + picQuality);
            // Specify the image type.
            sb.append(" -e ").append(picType);
            // Specify the name of the image.
            sb.append(" -o ").append(picName);

            // Invoke raspistill to take the photo.
            Runtime.getRuntime().exec(sb.toString());
            // Pause to allow the camera time to take the photo.
            Thread.sleep(picTimeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
