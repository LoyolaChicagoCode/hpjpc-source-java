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
//create a bunch of threads to send messages to each other
package info.jhpc.textbook.chapter04;

import info.jhpc.thread.*;

class TestSTOQ1 extends Thread {
   final static int threadCount = 5, interCount = 1000;

   static SharedTableOfQueues stoq = new SharedTableOfQueues();

   int myId, numThreads, numToEach;

   public static void main(String[] x) {
      int i;
      System.out
            .println("create a bunch of threads to send messages to each other");
      System.out.println(" each thread has its own input queue");
      for (i = 0; i < threadCount; i++) {
         Thread worker = new TestSTOQ1(i, threadCount, interCount);
         worker.start();
      }
   }

   TestSTOQ1(int me, int num, int times) {
      myId = me;
      numThreads = num;
      numToEach = times;
   }

   public void run() {
      try {
         int numRcvd, sendTo, sendIter;
         String myKey = "" + myId;
         for (sendIter = 1; sendIter <= numToEach; sendIter++) {
            for (sendTo = 0; sendTo < numThreads; sendTo++) {
               // send everyone my number as a String
               stoq.put("" + sendTo, myKey);
            }
            for (numRcvd = 0; numRcvd < numThreads; numRcvd++) {
               // String s=(String)
               stoq.get(myKey);
               // System.out.println(myId+" gets "+s);
            }
         }
         System.out.println(myId + " done");
      } catch (InterruptedException ie) {
      }
   }
}