package edu.osc.mnist.mnistref;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.osc.mnist.mnistref.ui.DataComponent;
import edu.osc.mnist.mnistref.ui.WeightComponent;

public class MNistRef extends JFrame implements Runnable {
   private static final long serialVersionUID = 7840803213149047385L;

   public final static String TRAINING_DATA = "src/test/resources/train-images-idx3-ubyte";
   public final static String TRAINING_LABELS = "src/test/resources/train-labels-idx1-ubyte";
   public final static String TEST_DATA = "src/test/resources/t10k-images-idx3-ubyte";
   public final static String TEST_LABELS = "src/test/resources/t10k-labels-idx1-ubyte";

   public final static int ND = 10;

   public static void main(String[] args) throws Exception {
      File dot = new File(".");
      System.out.println(dot.getAbsolutePath());

      Random63 rnd63 = new Random63(8675309);

      double[][] trainingData = readData(TRAINING_DATA);
      double[][] trainingLabels = readLabels(TRAINING_LABELS);
      double[][] testData = readData(TEST_DATA);
      double[][] testLabels = readLabels(TEST_LABELS);

      final int nn = trainingData[0].length;
      double[][] w = new double[ND][nn];
      double[] b = new double[ND];
      initWB(rnd63, w, b);

      double[] y = new double[ND];
      double[] g = new double[ND];

      MNistRef mnr = new MNistRef();
      double[][] digits = new double[ND][];
      for (int id = 0; id < ND; id++) {
         for (int i = 0, n = testLabels.length; i < n; i++) {
            if (testLabels[i][id] > 0.5) {
               digits[id] = testData[i];
            }
         }
      }
      mnr.data = digits;
      mnr.weights = w;
      SwingUtilities.invokeLater(mnr);
      Thread.sleep(1000L);

      int nCorrect = 0;
      for (int it = 0, nt = testData.length; it < nt; it++) {
         evaluate(w, b, testData[it], y);
         nCorrect += oneHitCorrect(y, testLabels[it]);
      }
      System.out.println("Before training");
      System.out.println(nCorrect + " correct out of " + testData.length);

      for (int ic = 0; ic < 200; ic++) {

         for (int it = 0, nt = nn; it < nt; it++) {
            int ix = (int) (nn * rnd63.nextDouble());
            evaluate(w, b, trainingData[ix], y);
            learn(0.5, w, b, trainingData[ix], y, trainingLabels[ix], g);
         }
         SwingUtilities.invokeLater(mnr);
         Thread.sleep(100L);

         int nC = 0;
         for (int it = 0, nt = testData.length; it < nt; it++) {
            evaluate(w, b, testData[it], y);
            nC += oneHitCorrect(y, testLabels[it]);
         }
         System.out.println("After training " + (ic + 1));
         System.out.println(nC + " correct out of " + testData.length);

         if ((ic > 10) && (nC < (nCorrect - 10)))
            break;
         nCorrect = Math.max(nCorrect, nC);
      }

      for (int id = 0, nd = b.length; id < nd; id++) {
         System.out.print(" " + b[id]);
      }
      System.out.println();
   }

   public static double[][] readData(String dataFile) throws IOException, ParseException {
      InputStream is = new FileInputStream(dataFile);
      IDXReader idxR = new IDXReader(is);
      int[][] rawData = idxR.readInt2D();
      is.close();

      int n = rawData.length;
      int d = rawData[0].length;
      double[][] data = new double[n][d];
      for (int in = 0; in < n; in++) {
         for (int id = 0; id < d; id++) {
            data[in][id] = rawData[in][id] / 256.0;
         }
      }
      return data;
   }

   public static double[][] readLabels(String labelsFile) throws IOException, ParseException {
      InputStream is = new FileInputStream(labelsFile);
      IDXReader idxR = new IDXReader(is);
      int[] rawLabels = idxR.readInt1D();
      is.close();

      int n = rawLabels.length;
      double[][] labels = new double[n][ND];
      for (int in = 0; in < n; in++) {
         labels[in][rawLabels[in]] = 1.0;
      }
      return labels;
   }

   public static void initWB(Random63 rnd63, double[][] w, double[] b) {
      for (int id = 0, nd = w.length; id < nd; id++) {
         double[] wd = w[id];
         for (int i = 0, ni = wd.length; i < ni; i++) {
            wd[i] = 1.0 - 2 * rnd63.nextDouble();
         }
         b[id] = rnd63.nextDouble();
      }
   }

   public static void evaluate(double[][] w, double[] b, double[] data, double[] y) {
      for (int id = 0, nd = y.length; id < nd; id++) {
         y[id] = b[id];
         double[] wd = w[id];
         for (int i = 0, n = wd.length; i < n; i++) {
            y[id] += wd[i] * data[i];
         }
      }
      for (int id = 0, nd = y.length; id < nd; id++) {
         y[id] = 1.0 / (1.0 + Math.exp(-y[id]));
      }
   }

   public static int oneHitCorrect(double[] y, double[] t) {
      double maxY = y[0];
      double maxT = t[0];
      int maxYX = 0;
      int maxTX = 0;
      for (int id = 1, nd = y.length; id < nd; id++) {
         if (y[id] > maxY) {
            maxY = y[id];
            maxYX = id;
         }
         if (t[id] > maxT) {
            maxT = t[id];
            maxTX = id;
         }
      }
      return (maxYX == maxTX) ? 1 : 0;
   }

   public static void learn(double rate, double[][] w, double[] b, double[] data, double[] y, double[] t, double[] g) {
      double c = 0.0;
      for (int id = 0, nd = b.length; id < nd; id++) {
         double d = y[id] - t[id];
         c += d * d;
         g[id] = d * (y[id] - y[id] * y[id]);
      }

      for (int id = 0, nd = b.length; id < nd; id++) {

         double step = -rate * Math.signum(g[id]) * c / (1.0 + c);
         double[] wd = w[id];
         for (int i = 0, n = wd.length; i < n; i++) {
            wd[i] += step * data[i];
         }
         b[id] += step;
      }
   }

   private double[][] data;
   private double[][] weights;
   private boolean initialized = false;

   public MNistRef() {
      super("MNistRef");
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
   }

   @Override
   public void run() {
      if (!initialized) {
         initialized = true;

         Container content = getContentPane();
         content.setLayout(new GridLayout(0, 5));

         for (int i = 0, n = 10; i < n; i++) {
            content.add(new DataComponent(data[i], 28));
         }
         for (int i = 0, n = 10; i < n; i++) {
            content.add(new WeightComponent(weights[i], 28));
         }

         pack();
         setVisible(true);
      } else {
         repaint();
      }
   }
}
