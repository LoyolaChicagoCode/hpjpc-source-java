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
package info.jhpc.textbook.chapter09;

import info.jhpc.thread.IndexedKey;
import info.jhpc.thread.SharedTableOfQueues;

class BBuffer {
    private IndexedKey fulls = IndexedKey.unique(0);

    private IndexedKey empties = fulls.at(1);

    private SharedTableOfQueues stoq = new SharedTableOfQueues();

    public BBuffer(int num) {
        for (int i = num; i > 0; i--)
            stoq.put(empties, "X");
    }

    public void put(Object x) {
        try {
            stoq.get(empties);
            stoq.put(fulls, x);
        } catch (InterruptedException e) {
        }
    }

    public Object get() {
        Object x = null;
        try {
            x = stoq.get(fulls);
            stoq.put(empties, "X");
        } catch (InterruptedException e) {
        }
        return x;
    }

    public String toString() {
        return "BBuffer(" + fulls + ")";
    }

    public static class Test1 extends Thread {
        static BBuffer b = new BBuffer(5);

        public static void main(String[] args) {
            new Test1().start();
            Object o;
            while ((o = b.get()) != null)
                System.out.println(o);
        }

        public void run() {
            for (int i = 1; i <= 10; i++)
                b.put(new Integer(i));
            b.put(null);
        }
    }
}
