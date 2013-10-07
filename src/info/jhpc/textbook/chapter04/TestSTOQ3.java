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
//create a bunch of threads to put strings into and take them out of queues
// use SharedTableOfQueues.getSkip to receive
package info.jhpc.textbook.chapter04;

class TestSTOQ3 extends Thread {
   final static int numSenders = 2, numReceivers = 3, maxMsg = 1000;

   static SharedTableOfQueues stoq = new SharedTableOfQueues();

   int myId, next, step;

   boolean receiver;

   public static void main(String[] x) {
      int i;
      System.out.println("Hi!");
      System.out.println("create a bunch of threads to put strings into");
      System.out.println(" and take them out of an array");
      System.out.println(" one queue per array element");
      System.out.println(" use getSkip to receive");
      for (i = 0; i < numSenders; i++) {
         Thread worker = new TestSTOQ3(i, numSenders, false);
         worker.start();
      }
      for (i = 0; i < numReceivers; i++) {
         Thread worker = new TestSTOQ3(i, numReceivers, true);
         worker.start();
      }
   }

   public TestSTOQ3(int me, int stride, boolean Ireceive) {
      myId = next = me;
      step = stride;
      receiver = Ireceive;
   }

   public void run() {
      while (next <= maxMsg) {
         if (receiver)
            while (stoq.getSkip("" + next) == null)
               yield();
         else
            stoq.put("" + next, "" + next);
         next += step;
      }
      System.out.println((receiver ? "receiver " : "sender ") + myId + " done");
   }
}