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
package info.jhpc.textbook.chapter07;

import info.jhpc.thread.*;

class TestVadd2 {
   public static void main(String[] args) throws InterruptedException {
      double[] x = { 1.0, 2.0, 3.0 };
      double[] y = { 4.0, 5.0, 6.0 };
      DFFuture1 f1 = new DFFuture1();
      DFFuture1 f2 = new DFFuture1();
      DFFuture1 f3 = new DFFuture1();
      Binop2 bop = new Binop2(new Vadd(f3));
      f1.runDelayed(new Fetch(f1, new Store(bop, 0)));
      f2.runDelayed(new Fetch(f2, new Store(bop, 1)));
      f1.setValue(x);
      f2.setValue(y);
      double[] z = (double[]) f3.getValue();
      for (int i = 0; i < z.length; ++i)
         System.out.print(z[i] + " ");
      System.out.println();
      Future.getClassRunQueue().setMaxThreadsWaiting(0);
   }
}