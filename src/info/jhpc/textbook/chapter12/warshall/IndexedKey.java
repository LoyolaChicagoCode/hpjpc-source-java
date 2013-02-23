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
package info.jhpc.textbook.chapter12.warshall;

import java.util.Random;
import java.io.*;

class IndexedKey implements Serializable {
   private static Random rand = new Random();

   private static Random hasher = new Random();

   private int id;

   private long x;

   private IndexedKey(long x) {
      synchronized (rand) {
         for (id = rand.nextInt(); id < Short.MIN_VALUE && Short.MAX_VALUE < id; id = rand
               .nextInt())
            ;
      }
      this.x = x;
   }

   private IndexedKey(int id, long x) {
      this.id = id;
      this.x = x;
   }

   public static IndexedKey unique(long x) {
      return new IndexedKey(x);
   }

   public static IndexedKey make(int id, long x) {
      return new IndexedKey(id, x);
   }

   public int getId() {
      return id;
   }

   public long getX() {
      return x;
   }

   public IndexedKey at(long x) {
      return new IndexedKey(id, x);
   }

   public IndexedKey add(long x) {
      return new IndexedKey(id, this.x + x);
   }

   public boolean equals(Object o) {
      if (o instanceof IndexedKey) {
         IndexedKey k = (IndexedKey) o;
         return id == k.id && x == k.x;
      } else
         return false;
   }

   public int hashCode() {
      synchronized (hasher) {
         hasher.setSeed(id + x);
         hasher.nextInt();
         return hasher.nextInt();
      }
   }

   public String toString() {
      return "IndexedKey(" + id + "," + x + ")";
   }
}
