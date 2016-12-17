package edu.osc.mnist.mnistref;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;

public class IDXReader {

   public final static int MAGIC_OFFSET = 0;
   public final static int MAGIC = 0;

   public final static int TYPE_OFFSET = 2;
   public final static int TYPE_UNSIGNED_BYTE = 0x08;
   public final static int TYPE_SIGNED_BYTE = 0x09;
   public final static int TYPE_NOT_ALLOWED = 0x0A;
   public final static int TYPE_SHORT = 0x0B;
   public final static int TYPE_INT = 0x0C;
   public final static int TYPE_FLOAT = 0x0D;
   public final static int TYPE_DOUBLE = 0x0E;

   public final static int N_DIMENSIONS_OFFSET = 3;

   public final static int DIMENSIONS_OFFSET = 4;

   private final DataInputStream dis;
   private int type;
   private int nDimensions;
   private int[] dimensions;
   private int lastDimension;
   private byte[] buffer;
   private ByteBuffer byteBuffer;

   public IDXReader(InputStream is) throws IOException, ParseException {
      this.dis = new DataInputStream(is);
      readMagic();
      readType();
      readNDimensions();
      readDimensions();
   }

   public int getNDimensions() {
      return nDimensions;
   }

   public int[] getDimensions() {
      return dimensions.clone();
   }

   public int[] readInt1D() throws IOException, ParseException {
      init(TYPE_UNSIGNED_BYTE, TYPE_INT, 1);
      int[] int1 = new int[lastDimension];
      return readInt1D(int1);
   }

   public int[][] readInt2D() throws IOException, ParseException {
      init(TYPE_UNSIGNED_BYTE, TYPE_INT, 2);
      int[][] int2 = new int[dimensions[0]][lastDimension];
      return readInt2D(int2);
   }

   public int[][][] readInt3D() throws IOException, ParseException {
      init(TYPE_UNSIGNED_BYTE, TYPE_INT, 3);
      int[][][] int3 = new int[dimensions[0]][dimensions[1]][lastDimension];
      return readInt3D(int3);
   }

   public double[] readDouble1D() throws IOException, ParseException {
      init(TYPE_FLOAT, TYPE_DOUBLE, 1);
      double[] double1 = new double[lastDimension];
      return readDouble1D(double1);
   }

   public double[][] readDouble2D() throws IOException, ParseException {
      init(TYPE_FLOAT, TYPE_DOUBLE, 2);
      double[][] double2 = new double[dimensions[0]][lastDimension];
      return readDouble2D(double2);
   }

   public double[][][] readDouble3D() throws IOException, ParseException {
      init(TYPE_FLOAT, TYPE_DOUBLE, 3);
      double[][][] double3 = new double[dimensions[0]][dimensions[1]][lastDimension];
      return readDouble3D(double3);
   }

   void init(int expectedTypeMin, int expectedTypeMax, int expectedNDimensions) throws IOException, ParseException {
      if ((type < expectedTypeMin) || (type > expectedTypeMax)) {
         throw new ParseException("Expected type in [0x" + Integer.toHexString(expectedTypeMin) + ",0x"
               + Integer.toHexString(expectedTypeMax) + "] found " + Integer.toHexString(type), TYPE_OFFSET);
      }

      if (nDimensions < expectedNDimensions) {
         throw new ParseException("Expected number of dimensions " + expectedNDimensions + " found " + nDimensions,
               N_DIMENSIONS_OFFSET);
      }

      lastDimension = getLastDimension(expectedNDimensions);

      int size = 1;
      switch (type) {
      case TYPE_SHORT:
         size = 2;
         break;
      case TYPE_INT:
      case TYPE_FLOAT:
         size = 4;
         break;
      case TYPE_DOUBLE:
         size = 8;
         break;
      }
      buffer = new byte[size * lastDimension];
      byteBuffer = ByteBuffer.wrap(buffer);
      byteBuffer.order(ByteOrder.BIG_ENDIAN);
   }

   void readMagic() throws IOException, ParseException {
      int magic = dis.readUnsignedShort();
      if (magic != MAGIC) {
         throw new ParseException(
               "Expected magic of 0x" + Integer.toHexString(MAGIC) + ", found 0x" + Integer.toHexString(magic),
               MAGIC_OFFSET);
      }
   }

   void readType() throws IOException, ParseException {
      type = dis.readUnsignedByte();
      if ((type < TYPE_UNSIGNED_BYTE) || (type == TYPE_NOT_ALLOWED) || (type > TYPE_DOUBLE)) {
         throw new ParseException("Unsupported data type 0x" + Integer.toHexString(type), TYPE_OFFSET);
      }
   }

   void readNDimensions() throws IOException, ParseException {
      nDimensions = dis.readUnsignedByte();
      if (nDimensions == 0) {
         throw new ParseException("Invalid number of dimensions " + nDimensions, N_DIMENSIONS_OFFSET);
      }
   }

   void readDimensions() throws IOException, ParseException {
      dimensions = new int[nDimensions];
      for (int iDim = 0, offset = DIMENSIONS_OFFSET; iDim < nDimensions; iDim++, offset += 4) {
         int dim = dis.readInt();
         if (dim <= 0) {
            throw new ParseException("Invalid dimension[ " + iDim + "] " + dim, offset);
         }
         dimensions[iDim] = dim;
      }
   }

   int getLastDimension(int nEffectiveDimensions) {
      int lastDim = 1;
      for (int i = nEffectiveDimensions - 1; i < nDimensions; i++) {
         lastDim *= dimensions[i];
      }
      return lastDim;
   }

   int[] readInt1D(int[] int1) throws IOException {
      dis.readFully(buffer);
      byteBuffer.rewind();
      switch (type) {
      case TYPE_UNSIGNED_BYTE:
         fillIntArrayUnsignedByte(int1);
         break;
      case TYPE_SIGNED_BYTE:
         fillIntArraySignedByte(int1);
         break;
      case TYPE_SHORT:
         fillIntArrayShort(int1);
         break;
      case TYPE_INT:
         fillIntArrayInt(int1);
         break;
      }
      return int1;
   }

   int[][] readInt2D(int[][] int2) throws IOException {
      for (int i = 0, n = int2.length; i < n; i++) {
         readInt1D(int2[i]);
      }
      return int2;
   }

   int[][][] readInt3D(int[][][] int3) throws IOException {
      for (int i = 0, n = int3.length; i < n; i++) {
         readInt2D(int3[i]);
      }
      return int3;
   }

   double[] readDouble1D(double[] double1) throws IOException {
      dis.readFully(buffer);
      byteBuffer.rewind();
      switch (type) {
      case TYPE_FLOAT:
         fillDoubleArrayFloat(double1);
         break;
      case TYPE_DOUBLE:
         fillDoubleArrayDouble(double1);
         break;
      }
      return double1;
   }

   double[][] readDouble2D(double[][] double2) throws IOException {
      for (int i = 0, n = double2.length; i < n; i++) {
         readDouble1D(double2[i]);
      }
      return double2;
   }

   double[][][] readDouble3D(double[][][] double3) throws IOException {
      for (int i = 0, n = double3.length; i < n; i++) {
         readDouble2D(double3[i]);
      }
      return double3;
   }

   void fillIntArrayUnsignedByte(int[] int1) {
      for (int ix = 0, nx = int1.length; ix < nx; ix++) {
         int1[ix] = (0xFF & byteBuffer.get());
      }
   }

   void fillIntArraySignedByte(int[] int1) {
      for (int ix = 0, nx = int1.length; ix < nx; ix++) {
         int1[ix] = byteBuffer.get();
      }
   }

   void fillIntArrayShort(int[] int1) {
      for (int ix = 0, nx = int1.length; ix < nx; ix++) {
         int1[ix] = byteBuffer.getShort();
      }
   }

   void fillIntArrayInt(int[] int1) {
      for (int ix = 0, nx = int1.length; ix < nx; ix++) {
         int1[ix] = byteBuffer.getInt();
      }
   }

   void fillDoubleArrayFloat(double[] double1) {
      for (int ix = 0, nx = double1.length; ix < nx; ix++) {
         double1[ix] = byteBuffer.getFloat();
      }
   }

   void fillDoubleArrayDouble(double[] double1) {
      for (int ix = 0, nx = double1.length; ix < nx; ix++) {
         double1[ix] = byteBuffer.getDouble();
      }
   }
}
