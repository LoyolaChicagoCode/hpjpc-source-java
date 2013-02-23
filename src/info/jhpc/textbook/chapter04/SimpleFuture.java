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

/* snipon: info.jhpc.textbook.chapter04.SimpleFuture */

import info.jhpc.thread.*;

/* snip: all */
public class SimpleFuture extends Monitor {

   private volatile Object value;

   private Condition is_set = new Condition();

   public SimpleFuture() {
      value = this;
   }

   public SimpleFuture(Object val) {
      value = val;
   }

   public Object getValue() throws InterruptedException {
      try {
         enter();
         if (value == this)
            is_set.await();
         is_set.leaveWithSignal();
         return value;
      } catch (InterruptedException ie) {
         leave();
         throw ie;
      }
   }

   public boolean isSet() {
      enter();
      try {
         return (value != this);
      } finally {
         leave();
      }
   }

   public void setValue(Object val) {
      enter();
      if (value != this) {
         leave();
         return;
      }
      value = val;
      is_set.leaveWithSignal();
   }

}
/* pins: all */