package info.jhpc.textbook.chapter03;

import java.io.*;
import java.util.*;

// begin-class-FileCopy1
public class FileCopy1 {

   public static int getIntProp(Properties p, String key, int defaultValue) {

      try {
         return Integer.parseInt(p.getProperty(key));
      } catch (Exception e) {
         return defaultValue;
      }
   }

   public static void main(String args[]) {
      String srcFile = args[0];
      String dstFile = args[1];
      Properties p = new Properties();
      try {
         FileInputStream propFile = new FileInputStream("FileCopy1.rc");
         p.load(propFile);
      } catch (Exception e) {
         System.err.println("FileCopy1: Can't load Properties");
      }

      int buffers = getIntProp(p, "buffers", 20);
      int bufferSize = getIntProp(p, "bufferSize", 4096);

      System.out.println("source = " + args[0]);
      System.out.println("destination = " + args[1]);
      System.out.println("buffers = " + buffers);
      System.out.println("bufferSize = " + bufferSize);

      Pool pool = new Pool(buffers, bufferSize);
      BufferQueue copyBuffers = new BufferQueue();

      FileCopyReader1 src;

      try {
         src = new FileCopyReader1(srcFile, pool, copyBuffers);
      } catch (Exception e) {
         System.err.println("Cannot open " + srcFile);
         return;
      }

      FileCopyWriter1 dst;

      try {
         dst = new FileCopyWriter1(dstFile, pool, copyBuffers);
      } catch (Exception e) {
         System.err.println("Cannot open " + dstFile);
         return;
      }

      src.start();
      dst.start();

      try {
         src.join();
      } catch (Exception e) {
      }

      try {
         dst.join();
      } catch (Exception e) {
      }
   }
}
// end-class-FileCopy1
