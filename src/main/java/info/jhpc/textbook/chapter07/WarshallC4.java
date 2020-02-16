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
/*Warshall's algorithm.
 Warshall's algorithm using chores to handle rows. Uses a class for each continuation. Some chores have their run() methods executed directly.
 */
package info.jhpc.textbook.chapter07;

import info.jhpc.thread.*;
import java.util.Random;

class WarshallC4 {
   int numThreads;

   public WarshallC4(int numThreads) {
      this.numThreads = numThreads;
   }

   private class Row implements Runnable {
      boolean[] row;

      int myRowNumber;

      Future[] row_k_step_k;

      Accumulator done;

      int j, k;

      Row(boolean[] row, int myRowNumber, Future[] row_k_step_k,
            Accumulator done) {
         this.row = row;
         this.myRowNumber = myRowNumber;
         this.row_k_step_k = row_k_step_k;
         this.done = done;
      }

      class Loop implements Runnable {
         public void run() {

            for (;;) {
               if (k >= row_k_step_k.length) {
                  boolean[][] result = (boolean[][]) done.getData();
                  result[myRowNumber] = row;
                  done.signal();
                  return;
               }
               if (k == myRowNumber) {
                  row_k_step_k[k].setValue(row.clone());
                  k++;
                  continue;
               }
               if (!row[k]) {
                  k++;
                  continue;
               }
               row_k_step_k[k].runDelayed(updateRow);
               return;
            }
         }
      }

      Loop loop = new Loop();

      class UpdateRow implements Runnable {
         public void run() {
            boolean[] row_k;
            try {
               row_k = (boolean[]) row_k_step_k[k].getValue();
               for (j = 0; j < row.length; j++) {
                  row[j] |= row_k[j];
               }
               k++;
               loop.run();
            } catch (InterruptedException ex) {
            }
         }
      }

      UpdateRow updateRow = new UpdateRow();

      public void run() {// for Row class
         k = 0;
         loop.run();
      }
   }

   public boolean[][] closure(boolean[][] a) {
      int i;
      RunQueue rq = new RunQueue(numThreads);
      FutureFactory ff = new FutureFactory(rq);
      Future[] kthRows = new Future[a.length];
      for (i = 0; i < kthRows.length; ++i)
         kthRows[i] = ff.make();
      Accumulator done = new Accumulator(a.length, new boolean[a.length][]);
      for (i = 0; i < a.length; i++) {
         rq.run(new Row((boolean[]) a[i].clone(), i, kthRows, done));
      }
      boolean[][] result = null;
      try {
         result = (boolean[][]) done.getFuture().getValue();
         rq.setMaxThreadsWaiting(0);
      } catch (InterruptedException ex) {
      }
      return result;
   }

   public static class Test1 {
      public static void main(String[] args) {
         int N = 10;
         int nt = 3;
         int i;
         boolean a[][] = new boolean[N][N];
         WarshallC4 w = new WarshallC4(nt);
         for (i = 0; i < N; i++)
            a[i][(i + 1) % N] = true;
         show(a);
         System.out.println();
         a = w.closure(a);
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

         WarshallC4 w = new WarshallC4(nt);

         Random rand = new Random();
         for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++) {
               a[i][j] = (rand.nextDouble() <= probTrue);
            }
         }
         show(a);
         System.out.println();
         a = w.closure(a);
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
            System.out.println("usage: java WarshallC4$TestTime2 N nt");
            System.exit(0);
         }
         int N = Integer.parseInt(args[0]);
         int nt = Integer.parseInt(args[1]);
         int i, j;
         double probTrue = 0.3;
         boolean a[][] = new boolean[N][N];

         WarshallC4 w = new WarshallC4(nt);

         Random rand = new Random();
         for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++) {
               a[i][j] = (rand.nextDouble() <= probTrue);
            }
         }
         long start = System.currentTimeMillis();
         a = w.closure(a);
         System.out.println("WarshallC4\t" + N + "\t" + nt + "\t"
               + (System.currentTimeMillis() - start));
      }
   }
}
