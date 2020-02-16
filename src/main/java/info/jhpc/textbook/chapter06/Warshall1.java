/*
 To accompany High-Performance Java Platform(tm) Computing:
 Threads and Networking, published by Prentice Hall PTR and
 Sun Microsystems Press.

 Threads and Networking Library
 Copyright (C) 1999-2000
 Thomas W. Christopher and George K. Thiruvathukal

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Library General Public
 License as published by the Free Software Foundation; either
 version 2 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Library General Public License for more details.

 You should have received a copy of the GNU Library General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 */
package info.jhpc.textbook.chapter06;

import info.jhpc.thread.*;
import java.util.Random;

class Warshall1 {
   int numThreads;

   public Warshall1(int numThreads) {
      this.numThreads = numThreads;
   }

   private class Close extends Thread {
      boolean[][] a;

      int t;

      SimpleBarrier b;

      Accumulator done;

      Close(boolean[][] a, int t, SimpleBarrier b, Accumulator done) {
         this.a = a;
         this.t = t;
         this.b = b;
         this.done = done;
      }

      public void run() {
         try {
            int i, j, k;
            for (k = 0; k < a.length; k++) {
               for (i = t; i < a.length; i += numThreads) {
                  if (a[i][k])
                     for (j = 0; j < a.length; j++) {
                        a[i][j] = a[i][j] | a[k][j];
                     }
               }
               b.gather();
            }
            done.signal();
         } catch (InterruptedException ex) {
         }
      }
   }

   public void closure(boolean[][] a) {
      int i;
      Accumulator done = new Accumulator(numThreads);
      SimpleBarrier b = new SimpleBarrier(numThreads);
      for (i = 0; i < numThreads; i++) {
         new Close(a, i, b, done).start();
      }
      try {
         done.getFuture().getValue();
      } catch (InterruptedException ex) {
      }
   }

   public static class Test1 {
      public static void main(String[] args) {
         int N = 10;
         int nt = 3;
         int i;
         boolean a[][] = new boolean[N][N];
         Warshall1 w = new Warshall1(nt);
         for (i = 0; i < N; i++)
            a[i][(i + 1) % N] = true;
         show(a);
         System.out.println();
         w.closure(a);
         show(a);
      }

      static void show(boolean a[][]) {
         int i, j;
         for (i = 0; i < a.length; i++) {
            for (j = 0; j < a.length; j++) {
               System.out.print(a[i][j] ? '1' : '0');
            }
            System.out.println();
         }
      }
   }

   public static class Test2 {
      public static void main(String[] args) {
         int N = 10;
         int nt = 3;
         int i, j;
         double probTrue = 0.3;
         boolean a[][] = new boolean[N][N];

         Warshall1 w = new Warshall1(nt);

         Random rand = new Random();
         for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++) {
               a[i][j] = (rand.nextDouble() <= probTrue);
            }
         }
         show(a);
         System.out.println();
         w.closure(a);
         show(a);
      }

      static void show(boolean a[][]) {
         int i, j;
         for (i = 0; i < a.length; i++) {
            for (j = 0; j < a.length; j++) {
               System.out.print(a[i][j] ? '1' : '0');
            }
            System.out.println();
         }
      }
   }

   public static class TestTime2 {
      public static void main(String[] args) {
         if (args.length < 2) {
            System.out.println("Usage: java Warshall1$TestTime2 N nt");
            System.exit(0);
         }
         int N = Integer.parseInt(args[0]);
         int nt = Integer.parseInt(args[1]);
         int i, j;
         double probTrue = 0.3;
         boolean a[][] = new boolean[N][N];

         Warshall1 w = new Warshall1(nt);

         Random rand = new Random();
         for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++) {
               a[i][j] = (rand.nextDouble() <= probTrue);
            }
         }
         long start = System.currentTimeMillis();
         w.closure(a);
         System.out.println("Warshall1\t" + N + "\t" + nt + "\t"
               + (System.currentTimeMillis() - start));
      }
   }
}
