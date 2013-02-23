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
 ShellSort6.
 */
package info.jhpc.textbook.chapter08;

import info.jhpc.thread.*;

public class ShellSort6 {
   int numThreads = 8;

   int minDivisible = 16;

   class SortPass implements Runnable {
      int ary[], i, k, n;

      Accumulator finish;

      SortPass(int ary[], int i, int k, int n, RunDelayed start,
            Accumulator finish) {
         this.ary = ary;
         this.i = i;
         this.k = k;
         this.n = n;
         this.finish = finish;
         start.runDelayed(this);
      }

      public void run() {
         isort(ary, i, k, n);
         finish.signal();
      }
   }

   class IMerge implements Runnable {
      int ary[], i, k, m, n;

      Accumulator finish;

      IMerge(int ary[], int i, int k, int m, int n, RunDelayed start,
            Accumulator finish) {
         this.ary = ary;
         this.i = i;
         this.k = k;
         this.m = m;
         this.n = n;
         this.finish = finish;
         start.runDelayed(this);
      }

      public void run() {
         imerge(ary, i, k, m, n);
         finish.signal();
      }
   }

   int numInSequence(int i, int k, int n) {
      return (n - i + k - 1) / k;
   }

   int midpoint(int i, int k, int n) {
      return i + numInSequence(i, k, n) / 2 * k;
   }

   void setupSequence(int ary[], int i, int k, int n, RunDelayed start,
         Accumulator finish, AccumulatorFactory af) {
      if (numInSequence(i, k, n) <= minDivisible)
         new SortPass(ary, i, k, n, start, finish);
      else {
         Accumulator a = af.make(2);
         int m = midpoint(i, k, n);
         setupSequence(ary, i, k, m, start, a, af);
         setupSequence(ary, m, k, n, start, a, af);
         new IMerge(ary, i, k, m, n, a, finish);
      }
   }

   Accumulator setupPass(int ary[], RunDelayed start, int k,
         AccumulatorFactory af) {
      Accumulator finish = af.make(k);
      for (int i = 0; i < k; i++) {
         setupSequence(ary, i, k, ary.length, start, finish, af);
      }
      return finish;
   }

   public ShellSort6(int numThreads) {
      this.numThreads = numThreads;
   }

   public void sort(int a[]) {
      int N = a.length;
      if (N < minDivisible) {
         isort(a, 0, 1, N);
         return;
      }
      RunQueue rq = new RunQueue();
      rq.setMaxThreadsCreated(numThreads);
      FutureFactory ff = new FutureFactory(rq);
      AccumulatorFactory af = new AccumulatorFactory(ff);
      Accumulator waitFor = af.make(1);
      waitFor.signal();
      int k, m;
      k = N / 5;
      waitFor = setupPass(a, waitFor, k, af);
      k = N / 7;
      waitFor = setupPass(a, waitFor, k, af);
      for (k = (int) (k / 2.2); k > 0; k = (int) (k / 2.2)) {
         if (k == 2)
            k = 1;
         waitFor = setupPass(a, waitFor, k, af);
      }
      try {
         waitFor.getFuture().getValue();
      } catch (InterruptedException ie) {
      }
      ff.getRunQueue().setMaxThreadsWaiting(0);
   }

   void isort(int[] a, int m, int k, int n) {
      int i, j;
      for (j = m + k; j < n; j += k) {
         for (i = j; i >= m + k && a[i] < a[i - k]; i -= k) {
            int tmp = a[i];
            a[i] = a[i - k];
            a[i - k] = tmp;
         }
      }
   }

   void imerge(int[] a, int m, int k, int mid, int n) {
      int i, j;
      for (j = mid; j < n; j += k) {
         if (a[j] >= a[j - k])
            return;
         for (i = j; i >= m + k && a[i] < a[i - k]; i -= k) {
            int tmp = a[i];
            a[i] = a[i - k];
            a[i - k] = tmp;
         }
      }
   }

   public static class Test1 {
      public static void main(String[] args) {
         int[] a = new int[25];
         int i;
         for (i = a.length - 1; i >= 0; i--) {
            a[i] = (int) (Math.random() * 100);
         }
         for (i = 0; i < a.length - 1; i++) {
            System.out.print(" " + a[i]);
         }
         System.out.println();
         ShellSort6 s = new ShellSort6(3);
         s.sort(a);
         for (i = 0; i < a.length - 1; i++) {
            System.out.print(" " + a[i]);
         }
         System.out.println();
      }
   }

   public static class TestTime1 {
      public static void main(String[] args) {
         if (args.length < 2) {
            System.out.println("Usage: java ShellSort6$TestTime1 n nt");
            System.exit(0);
         }
         int N = Integer.parseInt(args[0]);
         int T = Integer.parseInt(args[1]);
         int[] a = new int[N];
         int i;
         long time;
         for (i = a.length - 1; i >= 0; i--) {
            a[i] = (int) (Math.random() * N);
         }
         // for (i=a.length-1;i>=0;i--) {
         // System.out.print(" "+a[i]);
         // }
         // System.out.println();
         ShellSort6 s = new ShellSort6(T);
         time = System.currentTimeMillis();
         s.sort(a);
         time = System.currentTimeMillis() - time;
         // for (i=a.length-1;i>=0;i--) {
         // System.out.print(" "+a[i]);
         // }
         // System.out.println();
         System.out.println("ShellSort6\t" + N + "\t" + T + "\t" + time);
      }
   }

}
