package bbk;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Date: 13-Aug-2008 01:03:53
 */
public class Scratch {


    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


        JPanel panel = new ArchitecturePanel();
        f.getContentPane().add("Center", panel);
        f.pack();
        f.setSize(new Dimension(550, 100));
        f.setVisible(true);


    }
}

class ArchitecturePanel extends JPanel {
    final static BasicStroke stroke = new BasicStroke(1.0f);
    final static double recWidth = 6;
    final static double pfamAHeight = 12;
    final static double pfamBHeight = 6;
    final static double gap = 3;

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //BufferedImage bi = new BufferedImage(550,100, BufferedImage.TYPE_BYTE_BINARY);
        //Graphics2D g2 = bi.createGraphics();

        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // draw Rectangle2D.Double
        g2.setStroke(stroke);

        double x = 5;
        double y = 10;

        int archCount = 10;

        Random r = new Random();

        g2.draw(new Line2D.Double(0, y, 200, y));

        for (int i=0; i<archCount; i++) {

            if (r.nextInt(2) == 1) {
               //
                g2.setPaint(Color.red);
                g2.fill(new Rectangle2D.Double(x, y - pfamAHeight / 2, recWidth, pfamAHeight));
                g2.setPaint(Color.black);
                g2.draw(new Rectangle2D.Double(x, y - pfamAHeight / 2, recWidth, pfamAHeight));
            } else {
                g2.setPaint(Color.green);
                g2.fill(new Rectangle2D.Double(x, y - pfamBHeight / 2, recWidth, pfamBHeight));
                g2.setPaint(Color.black);
                g2.draw(new Rectangle2D.Double(x, y - pfamBHeight / 2, recWidth, pfamBHeight));
            }

            x += recWidth + gap;
        }


        
    }
}

class MyPanel extends JPanel {

    final static int maxCharHeight = 15;
    final static int minFontSize = 6;

    final static Color bg = Color.white;
    final static Color fg = Color.black;
    final static Color red = Color.red;
    final static Color white = Color.white;

    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
    Dimension totalSize;
    FontMetrics fontMetrics;

    public void paint(Graphics g) {
        //super.paint(g);    //To change body of overridden methods use File | Settings | File Templates.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = getSize();
        int gridWidth = d.width / 6;
        int gridHeight = d.height / 2;

        fontMetrics = pickFont(g2, "Filled and Stroked GeneralPath",
                gridWidth);

        Color fg3D = Color.lightGray;

        g2.setPaint(fg3D);
        g2.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
        g2.draw3DRect(3, 3, d.width - 7, d.height - 7, false);
        g2.setPaint(fg);

        int x = 5;
        int y = 7;
        int rectWidth = gridWidth - 2*x;
        int stringY = gridHeight - 3 - fontMetrics.getDescent();
        int rectHeight = stringY - fontMetrics.getMaxAscent() - y - 2;

        // draw Line2D.Double
        g2.draw(new Line2D.Double(x, y+rectHeight-1, x + rectWidth, y));
        g2.drawString("Line2D", x, stringY);
        x += gridWidth;

        // draw Rectangle2D.Double
        g2.setStroke(stroke);
        g2.draw(new Rectangle2D.Double(x, y, rectWidth, rectHeight));
        g2.drawString("Rectangle2D", x, stringY);
        x += gridWidth;

        // draw  RoundRectangle2D.Double
        g2.setStroke(dashed);
        g2.draw(new RoundRectangle2D.Double(x, y, rectWidth,
                rectHeight, 10, 10));
        g2.drawString("RoundRectangle2D", x, stringY);
        x += gridWidth;

        // draw Arc2D.Double
        g2.setStroke(wideStroke);
        g2.draw(new Arc2D.Double(x, y, rectWidth, rectHeight, 90,
                135, Arc2D.OPEN));
        g2.drawString("Arc2D", x, stringY);
        x += gridWidth;

        // draw Ellipse2D.Double
        g2.setStroke(stroke);
        g2.draw(new Ellipse2D.Double(x, y, rectWidth, rectHeight));
        g2.drawString("Ellipse2D", x, stringY);
        x += gridWidth;

        // draw GeneralPath (polygon)
        int x1Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y1Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                x1Points.length);
        polygon.moveTo(x1Points[0], y1Points[0]);
        for ( int index = 1; index < x1Points.length; index++ ) {
            polygon.lineTo(x1Points[index], y1Points[index]);
        };
        polygon.closePath();

        g2.draw(polygon);
        g2.drawString("GeneralPath", x, stringY);

        // NEW ROW
        x = 5;
        y += gridHeight;
        stringY += gridHeight;

        // draw GeneralPath (polyline)

        int x2Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y2Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                x2Points.length);
        polyline.moveTo (x2Points[0], y2Points[0]);
        for ( int index = 1; index < x2Points.length; index++ ) {
            polyline.lineTo(x2Points[index], y2Points[index]);
        };

        g2.draw(polyline);
        g2.drawString("GeneralPath (open)", x, stringY);
        x += gridWidth;

        // fill Rectangle2D.Double (red)
        g2.setPaint(red);
        g2.fill(new Rectangle2D.Double(x, y, rectWidth, rectHeight));
        g2.setPaint(fg);
        g2.drawString("Filled Rectangle2D", x, stringY);
        x += gridWidth;

        // fill RoundRectangle2D.Double
        GradientPaint redtowhite = new GradientPaint(x,y,red,x+rectWidth, y,white);
        g2.setPaint(redtowhite);
        g2.fill(new RoundRectangle2D.Double(x, y, rectWidth,
                rectHeight, 10, 10));
        g2.setPaint(fg);
        g2.drawString("Filled RoundRectangle2D", x, stringY);
        x += gridWidth;

        // fill Arc2D
        g2.setPaint(red);
        g2.fill(new Arc2D.Double(x, y, rectWidth, rectHeight, 90,
                135, Arc2D.OPEN));
        g2.setPaint(fg);
        g2.drawString("Filled Arc2D", x, stringY);
        x += gridWidth;

        // fill Ellipse2D.Double
        redtowhite = new GradientPaint(x,y,red,x+rectWidth, y,white);
        g2.setPaint(redtowhite);
        g2.fill (new Ellipse2D.Double(x, y, rectWidth, rectHeight));
        g2.setPaint(fg);
        g2.drawString("Filled Ellipse2D", x, stringY);
        x += gridWidth;



        // fill and stroke GeneralPath
        int x3Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y3Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath filledPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                x3Points.length);
        filledPolygon.moveTo(x3Points[0], y3Points[0]);
        for ( int index = 1; index < x3Points.length; index++ ) {
            filledPolygon.lineTo(x3Points[index], y3Points[index]);
        };
        filledPolygon.closePath();
        g2.setPaint(red);
        g2.fill(filledPolygon);
        g2.setPaint(fg);
        g2.draw(filledPolygon);
        g2.drawString("Filled and Stroked GeneralPath", x, stringY);

    }


    FontMetrics pickFont(Graphics2D g2,
                         String longString,
                         int xSpace) {
        boolean fontFits = false;
        Font font = g2.getFont();
        FontMetrics fontMetrics = g2.getFontMetrics();
        int size = font.getSize();
        String name = font.getName();
        int style = font.getStyle();

        while ( !fontFits ) {
            if ( (fontMetrics.getHeight() <= maxCharHeight)
                    && (fontMetrics.stringWidth(longString) <= xSpace) ) {
                fontFits = true;
            }
            else {
                if ( size <= minFontSize ) {
                    fontFits = true;
                }
                else {
                    g2.setFont(font = new Font(name,
                            style,
                            --size));
                    fontMetrics = g2.getFontMetrics();
                }
            }
        }

        return fontMetrics;
    }
}
