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
//Test SharedTableOfQueues
//one thread will write memos into a single folder
//another will remove them and check their order
package info.jhpc.textbook.chapter04;

class TestSTOQ4 extends Thread {
   final static int maxMsg = 1000;

   static SharedTableOfQueues stoq = new SharedTableOfQueues();

   int myId, next;

   boolean receiver;

   public static void main(String[] x) {
      System.out.println("one thread will write strings into a single queue");
      System.out.println("another will remove them and check their order");
      Thread worker = new TestSTOQ4(false);
      worker.start();
      worker = new TestSTOQ4(true);
      worker.start();
   }

   TestSTOQ4(boolean Ireceive) {
      receiver = Ireceive;
   }

   public void run() {
      try {
         String s, nextstr;
         next = 0;
         while (next <= maxMsg) {
            nextstr = "" + next;
            if (receiver) {
               s = (String) stoq.get("queue");
               if (!s.equals(nextstr))
                  System.out.println("received " + s + " not " + nextstr);
            } else
               stoq.put("queue", nextstr);
            next++;
         }
         System.out.println((receiver ? "receiver " : "sender ") + " done");
      } catch (InterruptedException ie) {
      }
   }
}