package edu.osc.mnist.mnistref;

public class Random63 {

   long seed = 0;

   public Random63() {
      this(0);
   }

   public Random63(long seed) {
      if (seed == 0) {
         seed = Double.doubleToRawLongBits(Math.random());
      }
      this.seed = seed;
   }

   public long getSeed() {
      return seed;
   }

   public long next() {
      do {
         seed ^= seed >>> 12;
         seed ^= seed << 25;
         seed ^= seed >>> 27;
      } while (seed > 0);
      seed *= 0x2545F4914F6CDD1DL;
      return seed & Long.MAX_VALUE;
   }

   public double nextDouble() {
      long l = 0x3FF0000000000000L | (next() >>> 11);
      return Double.longBitsToDouble(l) - 1.0;
   }
}
