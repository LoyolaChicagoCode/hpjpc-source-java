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
package info.jhpc.textbook.chapter04;

import java.util.*;
import java.io.*;
import info.jhpc.thread.*;

class TestSimpleFuture {
   static class Add extends Thread {
      SimpleFuture result, left, right;

      Add(SimpleFuture LL, SimpleFuture RR, SimpleFuture DST) {
         left = LL;
         right = RR;
         result = DST;
      }

      protected void op() {
         try {
            result.setValue(new Long(((Long) (left.getValue())).longValue()
                  + ((Long) (right.getValue())).longValue()));
         } catch (InterruptedException ie) {
         }
         ;
      }

      public void run() {
         op();
      }
   }

   public static void main(String[] args) {
      int i;

      SimpleFuture f0, f1, f2;
      SimpleFuture i0 = f0 = new SimpleFuture();
      SimpleFuture i1 = f1 = new SimpleFuture();
      for (i = 2; i <= 20; i++) {
         f2 = new SimpleFuture();
         new Add(f0, f1, f2).start();
         f0 = f1;
         f1 = f2;
      }
      i0.setValue(new Long(0));
      i1.setValue(new Long(1));
      try {
         System.out.println(f1.getValue());
      } catch (Exception e) {
      }
   }
}
