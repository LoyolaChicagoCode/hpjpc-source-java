package info.jhpc.textbook.chapter03;

import java.util.*;
import java.io.*;

// begin-class-FileCopy0
public class FileCopy0 {
   public static final int BLOCK_SIZE = 4096;

   public static void copy(String src, String dst) throws IOException {
      FileReader fr = new FileReader(src);
      FileWriter fw = new FileWriter(dst);
      char[] buffer = new char[BLOCK_SIZE];
      int bytesRead;
      while (true) {
         bytesRead = fr.read(buffer);
         System.out.println(bytesRead + " bytes read");
         if (bytesRead < 0)
            break;
         fw.write(buffer, 0, bytesRead);
         System.out.println(bytesRead + " bytes written");

      }
      fw.close();
      fr.close();
   }

   public static void main(String args[]) {
      String srcFile = args[0];
      String dstFile = args[1];
      try {
         copy(srcFile, dstFile);
      } catch (Exception e) {
         System.out.println("Copy failed.");
      }
   }
}
// end-class-FileCopy0
