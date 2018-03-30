package utils;

import java.io.File;
import java.util.Map;
import java.awt.Color;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageAnalyzer {

  private static ColorUtils colorUtil = new ColorUtils();

  public ImageAnalyzer() {

  }

  /**
   * Creates a TreeMap<Color (Key), Times in Picture (Value)> and maps out the pixels in a image.
   * @param File - A file object linking to a picture.
   * @return TreeMap<Integer, Integer> - TreeMap containing pixel color values with the amount contained in the picture.
   */
  public TreeMap<Integer, Integer> mapPixels(File picture) {
    TreeMap<Integer, Integer> picColors = new TreeMap<Integer, Integer>();

    try {
      BufferedImage image = ImageIO.read(picture);

      for (int h = 0; h < image.getTileHeight(); h++) {
        for (int w = 0; w < image.getTileWidth(); w++) {

          if (picColors.containsKey(image.getRGB(w, h)) == true) {
            picColors.replace(image.getRGB(w, h), picColors.get(image.getRGB(w, h)) + 1);
          } else {
            picColors.put(image.getRGB(w, h), picColors.size() - 1);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return picColors;
  }

  /**
  * Returns the colors contained in a TreeMap.
  * @param TreeMap<Integer, Integer> - TreeMap containing pixel decimal color codes.
  * @return ArrayList<String> - ArrayList containing the color names converted from a decimal color code.
  */
  public ArrayList<String> extractColors(TreeMap<Integer, Integer> treeMap) {
    ArrayList<String> colorArr = new ArrayList<>();
    Iterator itr = treeMap.entrySet().iterator();

    while (itr.hasNext()) {
      String value = "" + itr.next();
      Color color = new Color(Integer.parseInt(value.substring(0, value.indexOf("="))));
      String result = colorUtil.getColorNameFromRgb(color.getRed(), color.getGreen(), color.getBlue());

      if (colorArr.contains(result) == false)
        colorArr.add(result);
    }

    return colorArr;
  }

  /**
  * Returns a Map in a String.
  * @param Map - A map object.
  * @return String - A map's values stored in a String.
  */
  public String mapToString(Map m) {
    String result = "";
    Iterator it = m.entrySet().iterator();

    while (it.hasNext()) {
      if (result.equals(""))
        result = result + it.next();
      else
        result = result + " " + it.next();
    }

    return result;
  }

}
