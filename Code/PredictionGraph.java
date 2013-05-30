/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stockforecast;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class PredictionGraph extends JPanel {
    static int[] data;
    static int[] pdata;
    final int PAD = 50 ;
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.WHITE);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        String s = "Stock Price";
        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
        for(int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float)font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw)/2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        // Abcissa label.
        s = " Days ";
        sy = h - PAD + (PAD - sh)/2 + lm.getAscent();
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (w - sw)/2;
        g2.drawString(s, sx, sy);
        // Draw lines.
        double xInc = (double)(w - 2*PAD)/(data.length-1);
        double scale = (double)(h - 2*PAD)/getMax();
        g2.setPaint(Color.green);
        for(int i = 0; i < data.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*data[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*data[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }

            g2.setPaint(Color.red);
            for(int i = 0; i < data.length; i++) {
            double x = PAD + i*xInc;
            double y = h - PAD - scale*data[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }

            g2.setPaint(Color.green);
        for(int i = 0; i < data.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*pdata[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*pdata[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
//data[23] = 342;data[24] = 3342;data[25] = 442;data[26] = 362;
            g2.setPaint(Color.yellow);
            for(int i = 0; i < data.length; i++) {
            double x = PAD + i*xInc;
            double y = h - PAD - scale*pdata[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
    }

    private int getMax() {
        int max = -Integer.MAX_VALUE;
        for(int i = 0; i < data.length; i++) {
            if(data[i] > max)
                max = data[i];
        }
        return max;
    }

    public void drawGraph(double [] aPrice, double [] pPrice, int sz)
    {
        int ar[] = new int[sz];
        int ar2[] = new int[sz];
        for(int i = 0; i < sz; i++) {
            ar[i] = (int) ((1000) * aPrice[i]);
         //   System.out.println(ar[i] + " ");
        }

         for(int i = 0; i < sz; i++) {
            ar2[i] = (int) ((1000) * pPrice[i]);
         //   System.out.println(ar[i] + " ");
        }

        System.out.println("ar.length = " + sz);
        data = ar;
        pdata = ar2;
        JFrame f = new JFrame();
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new PredictionGraph());
 
        f.setSize(700,500);
        f.setLocation(660,240);
        f.setVisible(true);
    }
}

