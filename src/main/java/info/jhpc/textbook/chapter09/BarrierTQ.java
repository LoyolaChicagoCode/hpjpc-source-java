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

class BarrierTQ {
    private IndexedKey initialKey = IndexedKey.unique(0);

    private SharedTableOfQueues stoq = new SharedTableOfQueues();

    private int stillToRegister;

    public BarrierTQ(int num) {
        stillToRegister = num;
        stoq.put(initialKey, new X(num));
    }

    public Handle register() {
        if (stillToRegister-- > 0)
            return new Handle();
        else
            throw new IllegalStateException();
    }

    public String toString() {
        return "BarrierTQ(" + initialKey.getId() + ")";
    }

    public static class Test1 extends Thread {
        static BarrierTQ bar;
        static int iters = 10;
        int me;

        Test1(int me) {
            this.me = me;
        }

        public static void main(String[] args) {
            try {
                int i;
                int num = Integer.parseInt(args[0]);
                bar = new BarrierTQ(num + 1);
                BarrierTQ.Handle b = bar.register();
                for (i = num; i > 0; i--)
                    new Test1(i).start();
                for (i = 1; i <= iters; i++) {
                    b.gather();
                    System.out.println();
                    b.gather();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void run() {
            BarrierTQ.Handle b = bar.register();
            for (int i = 1; i <= iters; i++) {
                System.out.print(me);
                b.gather();
                b.gather();
            }
        }
    }

    private class X {
        public int remaining, count;

        X(int c) {
            remaining = count = c;
        }
    }

    public class Handle {
        private IndexedKey current = initialKey;

        public void gather() {
            try {
                X x = (X) stoq.get(current);
                x.remaining--;
                if (x.remaining == 0) {
                    x.remaining = x.count;
                    current = current.add(1);
                    stoq.put(current, x);
                } else {
                    stoq.put(current, x);
                    current = current.add(1);
                    stoq.look(current);
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
