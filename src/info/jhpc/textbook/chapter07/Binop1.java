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

class Binop1 implements Runnable {
   Future lopnd, ropnd;

   Op2 continuation;

   public Binop1(Future lopnd, Future ropnd, Op2 continuation) {
      this.lopnd = lopnd;
      this.ropnd = ropnd;
      this.continuation = continuation;
   }

   public void run() {
      try {
         if (!lopnd.isSet())
            lopnd.runDelayed(this);
         if (!ropnd.isSet())
            ropnd.runDelayed(this);
         continuation.op(lopnd.getValue(), ropnd.getValue());
      } catch (InterruptedException e) {
         continuation.op(e, e);
      }
   }
}