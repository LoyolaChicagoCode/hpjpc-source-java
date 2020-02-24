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
package info.jhpc.textbook.chapter06;

import info.jhpc.thread.Accumulator;
import info.jhpc.thread.DynAlloc;
import info.jhpc.thread.DynAllocShare;
import info.jhpc.thread.SimpleBarrier;

import java.util.Random;

// dynamic allocation
class Warshall2 {
    int numThreads;

    public Warshall2(int numThreads) {
        this.numThreads = numThreads;
    }

    public void closure(boolean[][] a) {
        int i;
        Accumulator done = new Accumulator(numThreads);
        SimpleBarrier b = new SimpleBarrier(numThreads);
        DynAllocShare d = new DynAllocShare(a.length, numThreads, 2);
        for (i = 0; i < numThreads; i++) {
            new Close(a, d, b, done).start();
        }
        try {
            done.getFuture().getValue();
        } catch (InterruptedException ex) {
        }
    }

    public static class Test1 {
        public static void main(String[] args) {
            int N = 10;
            int nt = 3;
            int i;
            boolean[][] a = new boolean[N][N];
            Warshall2 w = new Warshall2(nt);
            for (i = 0; i < N; i++)
                a[i][(i + 1) % N] = true;
            show(a);
            System.out.println();
            w.closure(a);
            show(a);
        }

        static void show(boolean[][] a) {
            int i, j;
            for (i = 0; i < a.length; i++) {
                for (j = 0; j < a.length; j++) {
                    System.out.print(a[i][j] ? '1' : '0');
                }
                System.out.println();
            }
        }
    }

    public static class Test2 {
        public static void main(String[] args) {
            int N = 10;
            int nt = 3;
            int i, j;
            double probTrue = 0.3;
            boolean[][] a = new boolean[N][N];

            Warshall2 w = new Warshall2(nt);

            Random rand = new Random();
            for (i = 0; i < N; i++) {
                for (j = 0; j < N; j++) {
                    a[i][j] = (rand.nextDouble() <= probTrue);
                }
            }
            show(a);
            System.out.println();
            w.closure(a);
            show(a);
        }

        static void show(boolean[][] a) {
            int i, j;
            for (i = 0; i < a.length; i++) {
                for (j = 0; j < a.length; j++) {
                    System.out.print(a[i][j] ? '1' : '0');
                }
                System.out.println();
            }
        }
    }

    public static class TestTime2 {
        public static void main(String[] args) {
            if (args.length < 2) {
                System.out.println("Usage: java Warshall2$TestTime2 N nt");
                System.exit(0);
            }
            int N = Integer.parseInt(args[0]);
            int nt = Integer.parseInt(args[1]);
            int i, j;
            double probTrue = 0.3;
            boolean[][] a = new boolean[N][N];

            Warshall2 w = new Warshall2(nt);

            Random rand = new Random();
            for (i = 0; i < N; i++) {
                for (j = 0; j < N; j++) {
                    a[i][j] = (rand.nextDouble() <= probTrue);
                }
            }
            long start = System.currentTimeMillis();
            w.closure(a);
            System.out.println("Warshall2\t" + N + "\t" + nt + "\t"
                    + (System.currentTimeMillis() - start));
        }
    }

    private class Close extends Thread {
        boolean[][] a;

        DynAlloc d;

        SimpleBarrier b;

        Accumulator done;

        Close(boolean[][] a, DynAlloc d, SimpleBarrier b, Accumulator done) {
            this.a = a;
            this.d = d;
            this.b = b;
            this.done = done;
        }

        public void run() {
            try {
                int i, j, k;
                DynAlloc.Range r = new DynAlloc.Range();
                for (k = 0; k < a.length; k++) {
                    while (d.alloc(r)) {
                        for (i = r.start; i < r.end; i++) {
                            if (a[i][k])
                                for (j = 0; j < a.length; j++) {
                                    a[i][j] = a[i][j] | a[k][j];
                                }
                        }
                    }
                    b.gather();
                }
                done.signal();
            } catch (InterruptedException ex) {
            }
        }
    }
}
