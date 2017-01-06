package edu.osc.mnist.mnistref.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

public class WeightComponent extends Component {
   private static final long serialVersionUID = 4608490519465493347L;

   public final static int DEFAULT_REPLICATION = 8;

   private double[] weights = null;
   private int weightsRows = 0;
   private int weightsCols = 0;
   private int replication = DEFAULT_REPLICATION;
   private Dimension minimumSize = null;
   private Dimension preferredSize = null;

   public WeightComponent() {
      super();
      setBackground(Color.BLACK);
   }

   public WeightComponent(double[] weights, int weightsRows) {
      super();
      setWeights(weights, weightsRows);
   }

   public double[] getWeights() {
      return weights;
   }

   public void setWeights(double[] weights, int weightsRows) {
      this.weights = weights;
      this.weightsRows = weightsRows;
      weightsCols = 0;
      minimumSize = null;
      preferredSize = null;
      if (weights != null) {
         weightsCols = weights.length / weightsRows;
      }
   }

   public int getWeightsRows() {
      return weightsRows;
   }

   public int getWeightsCols() {
      return weightsCols;
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
         minimumSize = new Dimension(weightsCols, weightsRows);
      }
      return minimumSize;
   }

   @Override
   public Dimension getPreferredSize() {
      if (preferredSize == null) {
         preferredSize = new Dimension(replication * weightsCols, replication * weightsRows);
      }
      return preferredSize;
   }

   @Override
   public void paint(Graphics g) {
      int cols = getWidth();
      int rows = getHeight();
      int cRep = cols / weightsCols;
      int rRep = rows / weightsRows;
      int rep = Math.max(1, Math.min(cRep, rRep));

      g.setColor(getBackground());
      g.fillRect(0, 0, cols, rows);

      double maxAbs = 0.0;
      for (double w : weights) {
         maxAbs = Math.max(maxAbs, Math.abs(w));
      }

      for (int ir = 0, i = 0; ir < weightsRows; ir++) {
         for (int ic = 0; ic < weightsCols; ic++, i++) {
            int v = Math.min(255, (int) (256 * Math.abs(weights[i]) / maxAbs));
            if (weights[i] >= 0) {
               g.setColor(new Color(0, v, 0));
            } else {
               g.setColor(new Color(v, 0, 0));
            }
            g.fillRect(rep * ic, rep * ir, rep, rep);
         }
      }
   }
}
