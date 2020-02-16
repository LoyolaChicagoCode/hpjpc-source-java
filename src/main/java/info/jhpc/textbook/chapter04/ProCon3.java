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
// show producer & consumer synchronized by wait() and notifyAll()
package info.jhpc.textbook.chapter04;

import info.jhpc.thread.*;

class BoundedBuffer3 extends Monitor {
   Condition notEmpty = new Condition();

   Condition notFull = new Condition();

   volatile int hd = 0, tl = 0;

   Object[] buffer;

   public BoundedBuffer3(int size) {
      buffer = new Object[size];
   }

   public void put(Object v) throws InterruptedException {
      enter();
      if (tl - hd >= buffer.length)
         notFull.await();
      buffer[tl++ % buffer.length] = v;
      notEmpty.signal();
      leave();
   }

   public Object get() throws InterruptedException {
      enter();
      Object v;
      if (tl == hd)
         notEmpty.await();
      v = buffer[hd++ % buffer.length];
      notFull.signal();
      leave();
      return v;
   }
}

public class ProCon3 {
   public static void main(String[] x) {
      BoundedBuffer3 b = new BoundedBuffer3(3);
      Thread pro1 = new Producer3(b);
      Thread con1 = new Consumer3(b);
      Thread pro2 = new Producer3(b);
      Thread con2 = new Consumer3(b);
      pro1.start();
      pro2.start();
      con1.start();
      con2.start();
      try {
         pro1.join();
         con1.join();
         pro2.join();
         con2.join();
      } catch (InterruptedException e) {
         return;
      }
   }
}

class Producer3 extends Thread {
   BoundedBuffer3 buf;

   public void run() {
      int i;
      try {
         for (i = 0; i < 10; i++) {
            buf.put(new Integer(i));
            // buf.put(new Integer(i));
            // yield();
         }
         buf.put(new Integer(-1));
         // buf.put(new Integer(-1));
      } catch (InterruptedException e) {
         return;
      }
   }

   public Producer3(BoundedBuffer3 b) {
      buf = b;
   }
}

class Consumer3 extends Thread {
   BoundedBuffer3 buf;

   public void run() {
      Object j;
      try {
         j = buf.get();
         while (((Integer) j).intValue() != -1) {
            synchronized (System.out) {
               System.out.println(j);
            }
            yield();
            j = buf.get();
         }
      } catch (InterruptedException e) {
         return;
      }
   }

   public Consumer3(BoundedBuffer3 b) {
      buf = b;
   }
}
