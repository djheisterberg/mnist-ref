package edu.osc.mnist.mnistref;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.osc.mnist.mnistref.ui.DigitComponent;

public class ShowTestImages extends JFrame implements Runnable {
   private static final long serialVersionUID = -7228238968526794163L;

   public final static String TEST_IMAGES = "t10k-images-idx3-ubyte";

   private final int[][][] digits;

   public ShowTestImages(int[][][] digits) {
      super("Test Digits");
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.digits = digits;
   }

   public static void main(String[] args) throws Exception {
      InputStream is = ShowTestImages.class.getClassLoader().getResourceAsStream(TEST_IMAGES);
      IDXReader idxR = new IDXReader(is);
      int[][][] testImages = idxR.readInt3D();
      is.close();

      ShowTestImages app = new ShowTestImages(testImages);
      SwingUtilities.invokeLater(app);
   }

   @Override
   public void run() {
      Container content = getContentPane();
      content.setLayout(new GridLayout(5, 5));

      for (int i = 0, n = Math.min(25, digits.length); i < n; i++) {
         content.add(new DigitComponent(digits[i]));
      }

      pack();
      setVisible(true);
   }
}
