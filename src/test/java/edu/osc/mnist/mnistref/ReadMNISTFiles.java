package edu.osc.mnist.mnistref;

import java.io.InputStream;

public class ReadMNISTFiles {

   public final static String[] LABEL_FILES = { "t10k-labels-idx1-ubyte", "train-labels-idx1-ubyte" };

   public final static String[] IMAGE_FILES = { "t10k-images-idx3-ubyte", "train-images-idx3-ubyte" };

   public static void main(String[] args) throws Exception {
      for (String file : LABEL_FILES) {
         InputStream is = ReadMNISTFiles.class.getClassLoader().getResourceAsStream(file);
         IDXReader idxR = new IDXReader(is);
         int[] labels = idxR.readInt1D();
         is.close();
         System.out.println(file + " " + labels.length);
         System.out.println(labels[0] + " " + labels[1]);
      }
      for (String file : IMAGE_FILES) {
         InputStream is = ReadMNISTFiles.class.getClassLoader().getResourceAsStream(file);
         IDXReader idxR = new IDXReader(is);
         int[][] images = idxR.readInt2D();
         is.close();
         System.out.println(file + " " + images.length + " x " + images[0].length);
      }
   }
}
