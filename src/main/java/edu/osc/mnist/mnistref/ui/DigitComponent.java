package edu.osc.mnist.mnistref.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

public class DigitComponent extends Component {
   private static final long serialVersionUID = 2519871207473034778L;

   public final static int DEFAULT_REPLICATION = 8;

   private int[][] pixels = null;
   private int pixelRows = 0;
   private int pixelCols = 0;
   private int replication = DEFAULT_REPLICATION;
   private Dimension minimumSize = null;
   private Dimension preferredSize = null;

   public DigitComponent() {
      super();
      setBackground(Color.LIGHT_GRAY);
   }

   public DigitComponent(int[][] pixels) {
      super();
      setPixels(pixels);
   }

   public int[][] getPixels() {
      return pixels;
   }

   public void setPixels(int[][] pixels) {
      this.pixels = pixels;
      pixelRows = 0;
      pixelCols = 0;
      minimumSize = null;
      preferredSize = null;
      if (pixels != null) {
         pixelRows = pixels.length;
         if (pixelRows > 0) {
            pixelCols = pixels[0].length;
         }
      }
   }

   public int getPixelRows() {
      return pixelRows;
   }

   public int getPixelCols() {
      return pixelCols;
   }

   public int getReplication() {
      return replication;
   }

   public void setReplication(int replication) {
      if (replication <= 0)
         replication = DEFAULT_REPLICATION;
      preferredSize = null;
      this.replication = replication;
   }

   @Override
   public Dimension getMinimumSize() {
      if (minimumSize == null) {
         minimumSize = new Dimension(pixelCols, pixelRows);
      }
      return minimumSize;
   }

   @Override
   public Dimension getPreferredSize() {
      if (preferredSize == null) {
         preferredSize = new Dimension(replication * pixelCols, replication * pixelRows);
      }
      return preferredSize;
   }

   @Override
   public void paint(Graphics g) {
      int cols = getWidth();
      int rows = getHeight();
      int cRep = cols / pixelCols;
      int rRep = rows / pixelRows;
      int rep = Math.max(1, Math.min(cRep, rRep));

      g.setColor(getBackground());
      g.fillRect(0, 0, cols, rows);

      for (int ir = 0; ir < pixelRows; ir++) {
         for (int ic = 0; ic < pixelCols; ic++) {
            int v = 255 - Math.min(255, Math.max(0, pixels[ir][ic]));
            g.setColor(new Color(v, v, v));
            g.fillRect(rep * ic, rep * ir, rep, rep);
         }
      }
   }
}
