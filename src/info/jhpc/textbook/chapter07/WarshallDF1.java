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
/*
 Warshall's algorithm.
 Uses a program counter variable to keep track of state.
 */
package info.jhpc.textbook.chapter07;

import info.jhpc.thread.*;
import java.util.Random;

class WarshallDF1 {
   int numThreads;

   public WarshallDF1(int numThreads) {
      this.numThreads = numThreads;
   }

   private class RowUpdate implements Runnable {
      boolean[] row = null;

      Future myRow;

      int k;

      Future kthRow;

      Future resultRow;

      int j;

      RowUpdate(Future myRow, Future kthRow, int k, Future resultRow) {
         this.myRow = myRow;
         this.k = k;
         this.kthRow = kthRow;
         this.resultRow = resultRow;
      }

      public void run() {
         try {
            if (row == null) {
               if (!myRow.isSet()) {
                  myRow.runDelayed(this);
                  return;
               }
               row = (boolean[]) myRow.getValue();
               if (!row[k]) {
                  resultRow.setValue(row);
                  return;
               }
            }
            if (!kthRow.isSet()) {
               kthRow.runDelayed(this);
               return;
            }
            boolean[] row_k = (boolean[]) kthRow.getValue();
            boolean[] result = new boolean[row.length];
            for (j = 0; j < row.length; j++) {
               result[j] = row[j] | row_k[j];
            }
            resultRow.setValue(result);
         } catch (InterruptedException ex) {
         }
      }
   }

   public boolean[][] closure(boolean[][] a) {
      int i, k;
      RunQueue rq = new RunQueue(numThreads);
      FutureFactory ff = new FutureFactory(rq);
      Future[] srcRows = new Future[a.length];
      for (i = 0; i < srcRows.length; ++i) {
         srcRows[i] = ff.make();
         srcRows[i].setValue(a[i]);
      }
      for (k = 0; k < a.length; k++) {
         Future[] dstRows = new Future[a.length];
         for (i = 0; i < a.length; i++) {
            if (i == k)
               dstRows[i] = srcRows[i];
            else {
               dstRows[i] = ff.make();
               srcRows[i].runDelayed(new RowUpdate(srcRows[i], srcRows[k], k,
                     dstRows[i]));
            }
         }
         srcRows = dstRows;
      }
      boolean[][] result = new boolean[a.length][];
      try {
         for (i = 0; i < a.length; i++)
            result[i] = (boolean[]) srcRows[i].getValue();
      } catch (InterruptedException ex) {
      }
      // System.out.println(rq.getNumThreadsWaiting());
      rq.setMaxThreadsWaiting(0);
      rq.setWaitTime(0);
      // System.out.println(rq.getNumThreadsWaiting());
      return result;
   }

   public static class Test1 {
      public static void main(String[] args) {
         int N = 10;
         int nt = 3;
         int i;
         boolean a[][] = new boolean[N][N];
         WarshallDF1 w = new WarshallDF1(nt);
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

         WarshallDF1 w = new WarshallDF1(nt);

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
            System.out.println("Usage: java WarshallDF1$TestTime2 N nt");
            System.exit(0);
         }
         int N = Integer.parseInt(args[0]);
         int nt = Integer.parseInt(args[1]);
         int i, j;
         double probTrue = 0.3;
         boolean a[][] = new boolean[N][N];

         WarshallDF1 w = new WarshallDF1(nt);

         Random rand = new Random();
         for (i = 0; i < N; i++) {
            for (j = 0; j < N; j++) {
               a[i][j] = (rand.nextDouble() <= probTrue);
            }
         }
         long start = System.currentTimeMillis();
         a = w.closure(a);
         System.out.println("WarshallDF1\t" + N + "\t" + nt + "\t"
               + (System.currentTimeMillis() - start));
      }
   }
}
