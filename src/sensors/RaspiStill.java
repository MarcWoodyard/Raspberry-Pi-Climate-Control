/**
 @author robmcm
 This class is a very simple Java wrapper for the raspistill executable,
 which makes it easier to take photos from a Java application. Note that
 there are considerably more parameters available for raspistill which
 could be added to this class. (e.g. Shutter Speed, ISO, AWB, etc.)

 https://blogs.msdn.microsoft.com/robert_mcmurray/2015/06/12/simple-java-wrapper-class-for-raspistill-on-the-raspberry-pi-2/
 */

package sensors;

import utils.Logger;

public class RaspiStill {
  // Define the path to the raspistill executable.
  private final String raspistillPath = "/opt/vc/bin/raspistill";
  private final int picTimeout = 5000;
  private final int picQuality = 100;
  private int picWidth = 1024;
  private int picHeight = 768;
  private String picName = "image.jpg";
  private String picType = "jpg";
  private Logger log = new Logger();

  public RaspiStill() {

  }

  /**
   * Takes a picture with raspistill with default values.
   */
  public void takePicture() {
    try {
      // Determine the image type based on the file extension (or use the default).
      if (picName.indexOf('.') != -1)
        this.picType = this.picName.substring(this.picName.lastIndexOf('.') + 1);

      // Create a new string builder with the path to raspistill.
      StringBuilder sb = new StringBuilder(this.raspistillPath);

      // Add parameters for no preview and burst mode.
      sb.append(" -n -bm");
      // Configure the camera timeout.
      sb.append(" -t " + this.picTimeout);
      // Configure the picture width.
      sb.append(" -w " + this.picWidth);
      // Configure the picture height.
      sb.append(" -h " + this.picHeight);
      // Configure the picture quality.
      sb.append(" -q " + this.picQuality);
      // Specify the image type.
      sb.append(" -e " + this.picType);
      // Specify the name of the image.
      sb.append(" -o " + this.picName);

      // Invoke raspistill to take the photo.
      Runtime.getRuntime().exec(sb.toString());
      // Pause to allow the camera time to take the photo.
      Thread.sleep(this.picTimeout);
    } catch (Exception e) {
      this.log.alert("[ERROR] RaspiStill.java",
          "An error occured in RaspiStill.java.\n\nTechnical Information:\n" + e.getMessage());
    }
  }

  /**
   * Takes a picture with raspistill with default values.
   * @param String - File name (image.jpg)
   * @param int - Picture width
   * @param int - Picture height
   */
  public void TakePicture(String name, int width, int height) {
    this.picName = name;
    this.picWidth = width;
    this.picHeight = height;
    takePicture();
  }

  /**
   * Takes a picture with raspistill with default values.
   * @param String - File name (image.jpg)
   */
  public void takePicture(String name) {
    TakePicture(name, this.picWidth, this.picHeight);
  }

  /**
   * Takes a picture with raspistill with default values.
   * @param int - Picture width
   * @param int - Picture height
   */
  public void takePicture(int width, int height) {
    TakePicture(this.picName, width, height);
  }
}
