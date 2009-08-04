/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bbk.dng.actions;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author roman
 */
class Scale {

  //
  // C O N S T A N T S
  //
  private final static float  NEAR_ZERO = (float) 0.000001;
  private final static int    X = 0;
  private final static int    Y = 1;

  //
  // F I E L D S
  //
  private float[]          offset = new float[2];
  private float            scaleFactor = (float) 1.0;
  private int              plotAreaHeight = 0;
  private int              plotAreaWidth = 0;

  public Scale(WritePSFile psFile, Rectangle2D bounds, boolean landscape,
          float margin) {
    float[] cMin = new float[2];
    float[] cMax = new float[2];

    // Adjust the max and min coords to to add margin around the borders
    cMin[X] = (float) (bounds.getX() - margin);
    cMin[Y] = (float) (bounds.getY() - margin);
    cMax[X] = (float) (bounds.getX() + bounds.getWidth()) + margin;
    cMax[Y] = (float) (bounds.getY() + bounds.getHeight()) + margin;

    // Calculate the real height and width of the picture
    float height = cMax[1] - cMin[1];
    float width = cMax[0] - cMin[0];

    // Save the coords of the centre
    float centre[] = new float[2];
    centre[0] = (cMin[0] + cMax[0]) / 2;
    centre[1] = (cMin[1] + cMax[1]) / 2;

    // If width of height of diagram's real coords is close to zero,
    // then set to 1.0
    if (Math.abs(width) < NEAR_ZERO) {
      width = (float) 1.0;
    }
    if (Math.abs(height) < NEAR_ZERO) {
      height = (float) 1.0;
    }

    // Calculate the default real-object offset
    for (int i = 0; i < 2; i++) {
      offset[i] = - cMin[i];
    }

    // Calculate page width according to orientation
    plotAreaHeight = PostScript.getHeight(landscape);
    plotAreaWidth = PostScript.getWidth(landscape);

    // Determine whether picture bounded in the x- or y-direction
    if ((height / width) < ((float) plotAreaHeight / (float) plotAreaWidth)) {
      // Image limited by width
      scaleFactor = plotAreaWidth / width;

      // Adjust y-offset to centre the y values
      offset[1] = offset[1] + ((float) plotAreaHeight / scaleFactor - height) / 2;
    } else {
      // Image limited by height
      scaleFactor = (float) plotAreaHeight / height;

      // Adjust x-offset to centre the x values
      offset[0] = offset[0] + ((float) plotAreaWidth / scaleFactor - width) / 2;
    }
  }

  //
  // P U B L I C   M E T H O D S
  //

  // Return the given offset
  public float getOffset(int i)
    {
      return offset[i];
    }

  // Return the height of the plot area
  public int getPlotHeight() {
    return plotAreaHeight;
  }

  // Return the width of the plot area
  public int getPlotWidth() {
    return plotAreaWidth;
  }

  // Return the scale factor
  public float getScaleFactor()
    {
      return scaleFactor;
    }
}
