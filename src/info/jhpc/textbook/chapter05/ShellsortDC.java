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
package info.jhpc.textbook.chapter05;

import info.jhpc.thread.*;

class ShellsortDC {
   static int minDivisible = 3;

   private static class Sort implements Runnable {
      int[] a;

      int i, h;

      SimpleFuture f;

      RunQueue rq;

      Sort(int[] a, int i, int h, SimpleFuture f, RunQueue rq) {
         this.a = a;
         this.i = i;
         this.h = h;
         this.f = f;
         this.rq = rq;
      }

      void sort(int i, int h) {
         if (numInSequence(i, h, a.length) <= minDivisible)
            isort(a, i, h);
         else {
            SimpleFuture nf = new SimpleFuture();
            Sort s = new Sort(a, i + h, 2 * h, nf, rq);
            rq.run(s);
            sort(i, 2 * h);
            try {
               nf.getValue();
            } catch (InterruptedException iex) {
            }
            isort(a, i, h);
         }
      }

      public void run() {
         sort(i, h);
         f.setValue(null);
      }
   }

   static int numInSequence(int i, int k, int n) {
      return (n - i + k - 1) / k;
   }

   static void isort(int[] a, int m, int k) {
      int i, j;
      for (j = m + k; j < a.length; j += k) {
         for (i = j; i > m && a[i] > a[i - k]; i -= k) {
            int tmp = a[i];
            a[i] = a[i - k];
            a[i - k] = tmp;
         }
      }
   }

   public static void sort(int[] a) {
      SimpleFuture f = new SimpleFuture();
      RunQueue rq = new RunQueue();
      rq.run(new Sort(a, 0, 1, f, rq));
      try {
         f.getValue();
      } catch (Exception ex) {
      }
      rq.setMaxThreadsWaiting(0);
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
         sort(a);
         for (i = a.length - 1; i >= 0; i--) {
            System.out.print(" " + a[i]);
         }
         System.out.println();
      }
   }
}