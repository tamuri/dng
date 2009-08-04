/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bbk.dng.actions;

import java.awt.Color;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 *
 * @author roman
 */
class PostScript {

  private Color              lastColour = null;
  private Color              lastCircleColour = null;
  private Color              lastSphereColour = null;
  private float              lastLineWidth = (float) -1.0;
  private PrintStream        file = null;

  private static double      xx1 =  -1.0;
  private static double      xx2 = 650.0;
  private static double      xy1 =  -1.0;
  private static double      xy2 = 951.0;

  private static float       offset_x;
  private static float       offset_y;

  private static float       pageCentrex;
  private static float       pageCentrey;
  private static float       pageMaxx;
  private static float       pageMaxy;
  private static float       pageMinx;
  private static float       pageMiny;

  private final static float DEFAULT_LINE_WIDTH = (float) 2;
  private final static float NEAR_ZERO = (float) 0.000001;

  public static final float  PLOT_MARGIN = (float) 20;

  //
  // F I E L D S
  //

  public final static int    PORTRAIT = 0;
  public final static int    LANDSCAPE = 1;

  public static final int    BBOXX1 =  30;
  public static final int    BBOXX2 = 550;
  public static final int    BBOXY1 =  50;
  public static final int    BBOXY2 = 780;
  public static final double BORDER_MARGIN = 0.93;


  //
  // C O N S T R U C T O R S
  //
  //
  // Initialise the pointer to the PostScript output file
  //
  PostScript(PrintStream file) {
    this.file = file;
  }

  //
  // P U B L I C   M E T H O D S
  //


  // Return the plot width according to the selected orientation
  public static int getWidth(boolean landscape) {
    int width = 0;

    if (landscape) {
      width = BBOXY2 - BBOXY1;
    }
    else{
      width = BBOXX2 - BBOXX1;
    }

    return width;
  }

  // Return the plot height according to the selected orientation
  public static int getHeight(boolean landscape) {
    int height = 0;

    if (landscape) {
      height = BBOXX2 - BBOXX1;
    }
    else{
      height = BBOXY2 - BBOXY1;
    }

    return height;
  }


  //
  // Plot arc
  //
  public void psArc(float x, float y, float radius, float angle_start,
          float angle_end, float lineWidth, Color colour) {

    // Set the arc colour
    setRGBcolour(colour);

    // Convert the coordinates to PostScript coords
    x = convertx(x);
    y = converty(y);

    // Convert the radius to PostScript equivalent
    radius = convertLength(radius);

    // Write out the arc
    writeCommand(x, y, radius, angle_start, angle_end, "Arc", 2);
  }

  //
  // Plot a coloured circle with no border
  //
  public void psFilledCircle(float x, float y, float radius, Color colour) {

    // Set the arc colour
    setCircleColour(colour);

    // Convert the coordinates to PostScript coords
    x = convertx(x);
    y = converty(y);

    // Convert the radius to PostScript equivalent
    radius = convertLength(radius);

    // Write out the arc
    writeCommand(x, y, radius, "Ucircle", 2);
  }

  //
  // Write out the given centred text
  //
  public void psCentredText(float x,float y,float textSize,Color colour,
			    String text)
    {
      // Convert the coordinates to PostScript coords
      x = convertx(x);
      y = converty(y);

      // Convert the text size to PostScript equivalent
      textSize = convertLength(textSize);

      // Set the colour
      setRGBcolour(colour);

      // Write out the text
      writeCommand(x,y,"moveto",2);
      file.format("(%s) %s Center\n",text,format(textSize,2));
      file.format("(%s) %s Print\n",text,format(textSize,2));
    }

  //
  // Write a PostScript comment
  //
  public void psComment(String string)
    {
      file.format("\n");
      file.format("%% %s\n",string);
    }

  //
  // Draw the given line
  //
  public void psDrawDashedLine(float x1, float y1, float x2, float y2,
          float width, Color colour, float on, float off) {
    // Convert the dash on/off lengths to PostScript coordinatses
    on = convertLength(on);
    off = convertLength(off);

    file.format("[ %s %s ] 0 setdash\n",format(on, 2),format(off, 2));
    psDrawLine(x1, y1, x2, y2, width, colour);
    file.format("[] 0 setdash\n");
  }

  //
  // Draw angled ellipse
  //
  public void psEllipse(float x, float y, float width, float height,
          Color colour, float lineWidth, float angle) {
    float ratio;

    // Calculate the height to width ratio
    ratio = height / width;

    // Convert the ellipse-coordinates to PostScript coords
    x = convertx(x);
    y = converty(y);

    // Convert the ellipse width to PostScript equivalent
    width = convertLength(width);

    // Convert the line-width to PostScript equivalent
    lineWidth = convertLength(lineWidth);
    if (Math.abs(lineWidth) < NEAR_ZERO) {
      lineWidth = DEFAULT_LINE_WIDTH;
    }

    // Move to ellipse centre
    writeCommand(x, y, "moveto", 2);

    // Perform rotation
    writeCommand(x, y, -angle, "RotAngle", 2);

    // Set the line width
    setLineWidth(lineWidth);

    // Set the line colour
    setRGBcolour(colour);

    // Draw the ellipse
    writeCommand((float) 0.0, (float) 0.0, width, (float) 1.0, ratio, 0, 0,
            "Oellipse", 2);

    // Restore graphics mode
    file.format("R\n");
  }

  //
  // Define the dash-pattern for a dashed line
  //
  public void setDashPattern(float on, float off) {
    // Convert the dash on/off lengths to PostScript coordinatses
    on = convertLength(on);
    off = convertLength(off);

    if (on > 0 && off > 0)
      file.format("[ %s %s ] 0 setdash\n",format(on, 2),format(off, 2));
    else
      file.format("[] 0 setdash\n");
  }

  //
  // Draw the given line
  //
  public void psDrawLine(float x1, float y1, float x2, float y2, float width,
          Color colour) {
    // Convert the end-coordinates to PostScript coords
    x1 = convertx(x1);
    y1 = converty(y1);
    x2 = convertx(x2);
    y2 = converty(y2);

    // Convert the line-width to PostScript equivalent
    width = convertLength(width);
    if (Math.abs(width) < NEAR_ZERO) {
      width = DEFAULT_LINE_WIDTH;
    }

    // Set the line width
    setLineWidth(width);

    // Set the line colour
    setRGBcolour(colour);

    // Draw the line
    writeCommand(x1, y1, x2, y2, "L", 2);
  }

  //
  // Set the given fill colour
  //
  public void psFillColour(Color colour) {
  }

  //
  // Rotate page through 90 degrees
  //
  public void psRotate90(float new_origin_x,float new_origin_y)
    {
      file.format("\n");
      writeCommand(new_origin_x,new_origin_y," moveto Rot90",1);
      file.format("\n");
    }

  //
  // Write out a sphere of the given colour
  //
  public void psSphere(float x,float y,float radius,Color colour,
		       Color edgeColour)
    {
      // Convert the coordinates to PostScript coords
      x = convertx(x);
      y = converty(y);

      // Convert the radius to PostScript equivalent
      radius = convertLength(radius);

      // Set the edge colour
      setRGBcolour(edgeColour);

      // Set the sphere colour
      setSphereColour(colour);

      // Set default line-width
      setLineWidth(DEFAULT_LINE_WIDTH);

      // Draw the filled sphere
      writeCommand(x,y,radius,"Sphere",2);
    }

  //
  // Write out unbounded box
  //
  public void psUnboundedBox(float x1,float y1,float x2,float y2,
			     float x3,float y3,float x4,float y4,
			     Color colour)
    {
      // Convert the coordinates to PostScript coords
      x1 = convertx(x1);
      y1 = converty(y1);
      x2 = convertx(x2);
      y2 = converty(y2);
      x3 = convertx(x3);
      y3 = converty(y3);
      x4 = convertx(x4);
      y4 = converty(y4);

      // Set line-width to zero
      setLineWidth((float) 0.0);

      // Set graphics save mode on
      file.format("G\n");

      // Set the colour
      setRGBcolour(colour);

      // Define the command
      writeCommand(x1,y1,x2,y2,x3,y3,x4,y4,"Pl4",2);
    }

  //
  // Close off the current PostScript page
  //
  public void writeClosingLines() {
    String string;

    file.format("%%Trailer\n");
    string = String.valueOf(BBOXX1 - 1) + " " +
            String.valueOf(BBOXY1 - 1) + " " +
            String.valueOf(BBOXX2 + 1) + " " +
            String.valueOf(BBOXY2 + 1);
    file.format("%%BoundingBox: %s\n",string);
    file.format("%%EOF\n");
  }

  //
  // Close off the current PostScript page
  //
  public void writeEndPage()
    {
      file.format("\n");
      file.format("\n");
      file.format("LigPlusSave restore\n");
      file.format("showpage\n");
    }

  //
  // Write out the main PostScript header records
  //
  public void writeMainHeaders(String plotTitle, String application,
          String version, String date, int npages)
    {
      // Start of header records
      file.format("%%!PS-Adobe-3.0\n");
      file.format("%%%%Creator: " + application + " version "
              + version + "\n");
      file.format("%%%%DocumentNeededResources: font Times-Roman Symbol\n");
      file.format("%%%%BoundingBox: (atend)\n");
      file.format("%%%%Pages: %d\n",npages);
      file.format("%%%%Date: %s\n",date);

      // Write out the plot title
      file.format("%%%%Title: %s\n",plotTitle);

      // Close off header records
      file.format("%%%%EndComments\n");

      // Write out the standard commands
      file.format("%%%%BeginProlog\n");
      file.format("/L { moveto lineto stroke } bind def\n");
      file.format("/G { gsave } bind def\n");
      file.format("/R { grestore } bind def\n");
      file.format("/W { setlinewidth } bind def\n");
      file.format("/D { setdash } bind def\n");
      file.format("/Col { setrgbcolor } bind def\n");
      file.format("/Zero_linewidth { 0.0 } def\n");
      file.format("/Sphcol { 1 setgray } def\n");
      file.format("/Sphere { newpath 3 copy 0 360 arc gsave Sphcol fill 0\n");
      file.format("          setgray 0.5 setlinewidth 3 copy 0.94 mul\n");
      file.format("          260 350 arc stroke 3 copy 0.87 mul 275 335\n");
      file.format("          arc stroke 3 copy 0.79 mul 295 315 arc\n");
      file.format("          stroke 3 copy 0.8 mul 115 135 arc 3 copy\n");
      file.format("          0.6 mul 135 115 arcn closepath gsave 1 setgray\n");
      file.format("          fill grestore stroke 3 copy 0.7 mul 115 135\n");
      file.format("          arc stroke 3 copy 0.6 mul 124.9 125 arc\n");
      file.format("          0.8 mul 125 125.1 arc stroke grestore stroke\n");
      file.format("        } bind def\n");
      file.format("/Poly3  { moveto lineto lineto fill grestore } bind def\n");
      file.format("/Pl3    { 6 copy Poly3 moveto moveto moveto closepath \n");
      file.format("          stroke } bind def\n");
      file.format("/Pline3 { 6 copy Poly3 moveto lineto lineto closepath\n");
      file.format("          stroke } bind def\n");
      file.format("/Poly4  { moveto lineto lineto lineto fill grestore } \n");
      file.format("          bind def\n");
      file.format("/Pl4    { 8 copy Poly4 moveto moveto moveto moveto \n");
      file.format("          closepath stroke } bind def\n");
      file.format("/Pline4 { 8 copy Poly4 moveto lineto lineto lineto\n");
      file.format("          closepath stroke } bind def\n");
      file.format("/Circol { 1 setgray } def\n");
      file.format("/Circle { gsave newpath 0 360 arc gsave Circol fill \n");
      file.format("          grestore stroke grestore } bind def\n");
      file.format("/Ocircle { gsave newpath 0 360 arc stroke grestore } bind def \n");
      file.format("/Ucircle { gsave newpath 0 360 arc gsave Circol fill \n");
      file.format("           grestore grestore } bind def\n");
      file.format("/Arc    { newpath arc stroke newpath } bind def\n");
      file.format("/Ellipse { gsave translate scale Circle grestore } ");
      file.format("bind def\n");
      file.format("/Oellipse { gsave translate scale Ocircle grestore } ");
      file.format("bind def\n");
      file.format("/Print  { /Times-Roman findfont exch scalefont setfont\n");
      file.format("          show } bind def\n");
      file.format("/Gprint { /Symbol findfont exch scalefont setfont show\n");
      file.format("          } bind def\n");
      file.format("/Center { dup /Times-Roman findfont exch scalefont\n");
      file.format("          setfont exch stringwidth pop -2 div exch -3\n");
      file.format("          div rmoveto } bind def\n");
      file.format("/CenterRot90 {\n");
      file.format("          dup /Times-Roman findfont exch scalefont\n");
      file.format("          setfont exch stringwidth pop -2 div exch 3\n");
      file.format("          div exch rmoveto } bind def\n");
      file.format("/UncenterRot90 {\n");
      file.format("          dup /Times-Roman findfont exch scalefont\n");
      file.format("          setfont exch stringwidth } bind def\n");
      file.format("/Rot90  { gsave currentpoint translate 90 rotate }\n");
      file.format("          bind def\n");
      file.format("/RotAngle  { gsave currentpoint translate rotate } bind def\n");
      file.format("%%%%EndProlog\n");

      // Start of the set-up commands
      file.format("%%%%BeginSetup\n");
      file.format("1 setlinecap 1 setlinejoin 1 setlinewidth 0 setgray\n");
      file.format(" [ ] 0 setdash newpath\n");
      file.format("%%%%EndSetup\n");
    }

  //
  // Write out the page-headers for the current page
  //
  public void writePageHeaders(int page, int npages, Color bgColour) {
    String string;

    // Define pages's clipping borders
    file.format("%%%%Page: p%d %d\n",page,page);
    file.format("/LigPlusSave save def\n");
    string = String.valueOf((float) xx1) + " " +
            String.valueOf((float) xy1) + " moveto " +
            String.valueOf((float) xx2) + " " +
            String.valueOf((float) xy1) + " lineto " +
            String.valueOf((float) xx2) + " " +
            String.valueOf((float) xy2) + " lineto ";
    file.format("%s\n",string);
    string = String.valueOf((float) xx1) + " " +
            String.valueOf((float) xy2) + " lineto closepath";
    file.format("%s\n",string);
    file.format("gsave 1.0000 setgray fill grestore\n");
    file.format("stroke gsave\n");

    // Draw in the background colour
    psComment("Background\n");
    setRGBcolour(bgColour);
    writeCommand((float) BBOXX1, (float) BBOXY1, (float) BBOXX2,
            (float) BBOXY1, (float) BBOXX2, (float) BBOXY2,
            (float) BBOXX1, (float) BBOXY2, "Pl4", 2);

    // Unset last colour
    lastColour = null;

    // Set default line-width
    setLineWidth(DEFAULT_LINE_WIDTH);
  }

  //
  // Write Landscape commands
  //
  public void writeLandscape() {
    // Make necessary adjustments
    psComment("Landscape orientation\n");
    psRotate90((float) BBOXX2 + BBOXX1, (float) 0);
  }


  //
  // P R I V A T E   M E T H O D S
  //

  //
  // Convert the given length into PostScript coordinates
  //
  public float convertLength(float length)
    {
      // Perform the conversion
      return length;
    }

  // Calculate the x-coord in PostScript coordinates
  //
  public float convertx(float x)
    {
      // Perform the conversion on the x-value
      return x + BBOXX1;
    }

  //
  // Calculate the y-coord in PostScript coordinates
  //
  public float converty(float y)
    {
      // Perform the conversion on the y-value
    return y + BBOXY1;
    }

  //
  // Format floating-point number for given number of decimal places
  //
  private String format(float x,int nDecimals)
    {
      // Define number of decimal places for the numbers
      DecimalFormat number = (DecimalFormat) NumberFormat.getInstance();
      DecimalFormatSymbols symbols = number.getDecimalFormatSymbols();
      symbols.setDecimalSeparator('.');
      number.setDecimalFormatSymbols(symbols);
      number.setMaximumFractionDigits(nDecimals);
      number.setGroupingUsed(false);

      return number.format(x);
    }

  private void getLandscapeHeight()
    {
      pageMinx = BBOXY1;
      pageMaxx = BBOXY2;
    }

  //
  // Set the current line-thickness
  //
  private void setLineWidth(float lineWidth) {
    // Proceed only if the line-width has changed
    if (lineWidth != lastLineWidth) {
      // Write out the command
      writeCommand(lineWidth, "W", 2);
    }

    // Save the current line-width
    lastLineWidth = lineWidth;
  }

  //
  // Redefine the circle colour, if necessary
  //
  private void setCircleColour(Color colour) {
    // Proceed only if the colour has changed
    if (colour != lastCircleColour) {
      // Get the RGB values for this colour
      float red = (float) colour.getRed() / (float) 255.0;
      float green = (float) colour.getGreen() / (float) 255.0;
      float blue = (float) colour.getBlue() / (float) 255.0;

      // Write out the command
      file.format("/Circol { " + format(red, 4) + " " +
              format(green, 4) + " " + format(blue, 4) +
              " setrgbcolor } def\n");
    }

    // Save the current colour
    lastCircleColour = colour;
  }

  //
  // Convert the colour to RGB and write out
  //
  private void setRGBcolour(Color colour) {
    // Proceed only if the colour has changed
    if (colour != lastColour) {
      // Get the RGB values for this colour
      float red = (float) colour.getRed() / (float) 255.0;
      float green = (float) colour.getGreen() / (float) 255.0;
      float blue = (float) colour.getBlue() / (float) 255.0;

      // Write out the command
      writeCommand(red, green, blue, "setrgbcolor", 4);
    }

    // Save the current colour
    lastColour = colour;
  }

  //
  // Redefine the sphere colour, if necessary
  //
  private void setSphereColour(Color colour) {
    // Proceed only if the colour has changed
    if (colour != lastSphereColour) {
      // Get the RGB values for this colour
      float red = (float) colour.getRed() / (float) 255.0;
      float green = (float) colour.getGreen() / (float) 255.0;
      float blue = (float) colour.getBlue() / (float) 255.0;

      // Write out the command
      file.format("/Sphcol { " + format(red, 4) + " " +
              format(green, 4) + " " + format(blue, 4) +
              " setrgbcolor } def\n");
    }

    // Save the current colour
    lastSphereColour = colour;
  }

  //
  // Write out the command in the appropriate format (for 1 float)
  //
  private void writeCommand(float x,String command,int nDecimals)
    {
      // Creat the command string
      String string = format(x,nDecimals) + " " + command;

      // Write out
      file.format("%s\n",string);
    }

  //
  // Write out the command in the appropriate format (for 2 floats)
  //
  private void writeCommand(float x,float y,String command,int nDecimals)
    {
      // Creat the command string
      String string = format(x,nDecimals) + " " + format(y,nDecimals) + " "
	+ command;

      // Write out
      file.format("%s\n",string);
    }

  //
  // Write out the command in the appropriate format (for 3 floats)
  //
  private void writeCommand(float x,float y,float z,String command,
			    int nDecimals)
    {
      // Creat the command string
      String string = format(x,nDecimals) + " " + format(y,nDecimals) + " "
	+ format(z,nDecimals) + " " + command;

      // Write out
      file.format("%s\n",string);
    }

  //
  // Write out the command in the appropriate format (for 4 floats)
  //
  private void writeCommand(float x1,float y1,float x2,float y2,
			    String command,int nDecimals)
    {
      // Creat the command string
      String string = format(x1,nDecimals) + " " + format(y1,nDecimals) + " " +
	 format(x2,nDecimals) + " " + format(y2,nDecimals) + " " + command;

      // Write out
      file.format("%s\n",string);
    }

  //
  // Write out the command in the appropriate format (for 5 floats)
  //
  private void writeCommand(float x1,float y1,float x2,float y2,float z,
			    String command,int nDecimals)
    {
      // Creat the command string
      String string = format(x1,nDecimals) + " " + format(y1,nDecimals) + " " +
	format(x2,nDecimals) + " " + format(y2,nDecimals) + " " +
	format(z,nDecimals) + " " + command;

      // Write out
      file.format("%s\n",string);
    }

  //
  // Write out the command in the appropriate format (for 7 floats)
  //
  private void writeCommand(float f, float f0, float radius, float elongX,
          float elongY, float x, float y, String command, int nDecimals) {

    // Creat the command string
    String string = format(f, nDecimals) + " " + format(f0, nDecimals) + " " +
            format(radius, nDecimals) + " " + format(elongX, nDecimals) + " " +
            format(elongY, nDecimals) + " " + format(x, nDecimals) + " " +
            format(y, nDecimals) + " " + command;

    // Write out
    file.format("%s\n",string);
  }

  //
  // Write out the command in the appropriate format (for 8 floats)
  //
  private void writeCommand(float x1, float y1, float x2, float y2,
          float x3, float y3, float x4, float y4,
          String command, int nDecimals) {
    // Creat the command string
    String string = format(x1, nDecimals) + " " + format(y1, nDecimals) + " " +
            format(x2, nDecimals) + " " + format(y2, nDecimals) + " " +
            format(x3, nDecimals) + " " + format(y3, nDecimals) + " " +
            format(x4, nDecimals) + " " + format(y4, nDecimals) + " " + command;

    // Write out
    file.format("%s\n",string);
  }
}
