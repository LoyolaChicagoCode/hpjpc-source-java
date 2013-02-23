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

class ShellsortBarrier {
   static int minDivisible = 3;

   int numThreads;

   public ShellsortBarrier(int numThreads) {
      this.numThreads = numThreads;
   }

   private class Sort implements Runnable {
      int[] a;

      int i, h;

      SimpleBarrier b;

      Sort(int[] a, int i, int h, SimpleBarrier b) {
         this.a = a;
         this.i = i;
         this.h = h;
         this.b = b;
      }

      public void run() {
         try {
            while (h > 0) {
               if (h == 2)
                  h = 1;
               for (int m = i; m < h; m += numThreads) {
                  isort(a, i, h);
               }
               h = (int) (h / 2.2);
               b.gather();
            }
         } catch (Exception ex) {
         }
      }
   }

   static void isort(int[] a, int m, int h) {
      int i, j;
      for (j = m + h; j < a.length; j += h) {
         for (i = j; i > m && a[i] > a[i - h]; i -= h) {
            int tmp = a[i];
            a[i] = a[i - h];
            a[i - h] = tmp;
         }
      }
   }

   public void sort(int[] a) {
      if (a.length < minDivisible) {
         isort(a, 0, 1);
         return;
      }
      SimpleBarrier b = new SimpleBarrier(numThreads);
      for (int i = numThreads - 1; i > 0; i--)
         new Thread(new Sort(a, i, a.length / minDivisible, b)).start();
      new Sort(a, 0, a.length / minDivisible, b).run();
   }

   public static class Test1 {
      public static void main(String[] args) {
         int[] a = new int[20];
         int i;
         for (i = a.length - 1; i >= 0; i--) {
            a[i] = (int) (Math.random() * 100);
         }
         for (i = a.length - 1; i >= 0; i--) {
            System.out.print(" " + a[i]);
         }
         System.out.println();
         ShellsortBarrier s = new ShellsortBarrier(3);
         s.sort(a);
         for (i = a.length - 1; i >= 0; i--) {
            System.out.print(" " + a[i]);
         }
         System.out.println();
      }
   }

   public static class TestTime1 {
      public static void main(String[] args) {
         if (args.length < 2) {
            System.out.println("Usage: java ShellsortBarrier$TestTime1 N T");
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
         ShellsortBarrier s = new ShellsortBarrier(T);
         time = System.currentTimeMillis();
         s.sort(a);
         time = System.currentTimeMillis() - time;
         // for (i=a.length-1;i>=0;i--) {
         // System.out.print(" "+a[i]);
         // }
         // System.out.println();
         System.out.println("ShellsortBarrier\t" + N + "\t" + T + "\t" + time);
      }
   }
}