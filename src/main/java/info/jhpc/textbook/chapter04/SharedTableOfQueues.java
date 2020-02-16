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

/* snipon: info.jhpc.textbook.chapter04.SharedTableOfQueues */

import info.jhpc.thread.*;
import java.util.*;

/* snip: all */
class SharedTableOfQueues extends Monitor {

   Hashtable<Object, Folder> tbl = new Hashtable<Object, Folder>();

   private class Folder {
      volatile QueueComponent q = new QueueComponent();

      volatile Condition notEmpty = new Condition();

      volatile int numWaiting = 0;
   }

   /* snip: put */
   public void put(Object key, Object value) {
      enter();
      Folder f = tbl.get(key);
      if (f == null)
         tbl.put(key, f = new Folder());
      f.q.put(value);
      f.notEmpty.leaveWithSignal();
   }

   /* pins: put */

   /* snip: get */
   public Object get(Object key) throws InterruptedException {
      Folder f = null;
      enter();
      try {
         f = tbl.get(key);
         if (f == null)
            tbl.put(key, f = new Folder());
         f.numWaiting++;
         if (f.q.isEmpty())
            f.notEmpty.await();
         f.numWaiting--;
         return f.q.get();
      } finally {
         if (f != null && f.q.isEmpty() && f.numWaiting == 0)
            tbl.remove(key);
         leave();
      }
   }

   /* pins: get */

   /* snip: getSkip */
   public Object getSkip(Object key) {
      Folder f = null;
      enter();
      try {
         f = tbl.get(key);
         if (f == null || f.q.isEmpty()) {
            return null;
         }
         return f.q.get();
      } finally {
         if (f != null && f.q.isEmpty() && f.numWaiting == 0)
            tbl.remove(key);
         leave();
      }
   }
   /* pins: getSkip */

}
/* pins: all */
