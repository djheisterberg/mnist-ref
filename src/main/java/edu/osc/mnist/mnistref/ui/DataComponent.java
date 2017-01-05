package edu.osc.mnist.mnistref.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

public class DataComponent extends Component {
   private static final long serialVersionUID = 4272721784342026342L;

   public final static int DEFAULT_REPLICATION = 8;

   private double[] data = null;
   private int dataRows = 0;
   private int dataCols = 0;
   private int replication = DEFAULT_REPLICATION;
   private Dimension minimumSize = null;
   private Dimension preferredSize = null;

   public DataComponent() {
      super();
      setBackground(Color.LIGHT_GRAY);
   }

   public DataComponent(double[] data, int dataRows) {
      super();
      setData(data, dataRows);
   }

   public double[] getData() {
      return data;
   }

   public void setData(double[] data, int dataRows) {
      this.data = data;
      this.dataRows = dataRows;
      dataCols = 0;
      minimumSize = null;
      preferredSize = null;
      if (data != null) {
         dataCols = data.length / dataRows;
      }
   }

   public int getDataRows() {
      return dataRows;
   }

   public int getDataCols() {
      return dataCols;
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
         minimumSize = new Dimension(dataCols, dataRows);
      }
      return minimumSize;
   }

   @Override
   public Dimension getPreferredSize() {
      if (preferredSize == null) {
         preferredSize = new Dimension(replication * dataCols, replication * dataRows);
      }
      return preferredSize;
   }

   @Override
   public void paint(Graphics g) {
      int cols = getWidth();
      int rows = getHeight();
      int cRep = cols / dataCols;
      int rRep = rows / dataRows;
      int rep = Math.max(1, Math.min(cRep, rRep));

      g.setColor(getBackground());
      g.fillRect(0, 0, cols, rows);

      for (int ir = 0, i = 0; ir < dataRows; ir++) {
         for (int ic = 0; ic < dataCols; ic++, i++) {
            int v = 255 - (int) (256 * data[i]);
            g.setColor(new Color(v, v, v));
            g.fillRect(rep * ic, rep * ir, rep, rep);
         }
      }
   }
}
