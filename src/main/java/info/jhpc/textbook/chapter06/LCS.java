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
import info.jhpc.thread.Semaphore;

public class LCS {

    // begin-LCS-vars
    int numThreads;
    char[] c0;
    char[] c1;
    int[][] a;
    Accumulator done;

    // end-LCS-vars

    // begin-LCS-constructor1
    public LCS(char[] c0, char[] c1, int numThreads) {
        this.numThreads = numThreads;
        this.c0 = c0;
        this.c1 = c1;
        int i;
        done = new Accumulator(numThreads);

        a = new int[c0.length + 1][c1.length + 1];

        Semaphore left = new Semaphore(c0.length), right;
        for (i = 0; i < numThreads; i++) {
            right = new Semaphore();
            new Band(startOfBand(i, numThreads, c1.length), startOfBand(i + 1,
                    numThreads, c1.length) - 1, left, right).start();
            left = right;
        }
    }

    // end-LCS-constructor1

    // begin-LCS-constructor2
    public LCS(String s0, String s1, int numThreads) {
        this(s0.toCharArray(), s1.toCharArray(), numThreads);
    }

    // end-LCS-constructor2

    // begin-LCS-startOfBand
    int startOfBand(int i, int nb, int N) {
        return 1 + i * (N / nb) + Math.min(i, N % nb);
    }

    // begin-LCS-getLength
    public int getLength() {
        try {
            done.getFuture().getValue();
        } catch (InterruptedException ex) {
        }
        return a[c0.length][c1.length];
    }

    // end-LCS-startOfBand

    public int[][] getArray() {
        try {
            done.getFuture().getValue();
        } catch (InterruptedException ex) {
        }
        return a;
    }

    // end-LCS-getLength

    public static class Test1 {
        // begin-LCS-main
        public static void main(String[] args) {
            if (args.length < 2) {
                System.out.println("Usage: java LCS$Test1 string0 string1");
                System.exit(0);
            }
            int nt = 3;
            String s0 = args[0];
            String s1 = args[1];
            System.out.println(s0);
            System.out.println(s1);
            long t0 = System.currentTimeMillis();
            LCS w = new LCS(s0, s1, nt);
            long t1 = System.currentTimeMillis() - t0;
            System.out.println(w.getLength());
            System.out.println("Elapsed time " + t1 + " milliseconds");
        }
        // end-LCS-main
    }

    private class Band extends Thread {
        // begin-Band-vars
        int low;
        int high;
        Semaphore left, right;

        // end-Band-vars

        // begin-Band-constructor
        Band(int low, int high, Semaphore left, Semaphore right) {
            this.low = low;
            this.high = high;
            this.left = left;
            this.right = right;
        }

        // end-Band-constructor

        // begin-Band-run
        public void run() {
            try {
                int i, j;
                for (i = 1; i < a.length; i++) {
                    left.down();
                    for (j = low; j <= high; j++) {
                        if (c0[i - 1] == c1[j - 1])
                            a[i][j] = a[i - 1][j - 1] + 1;
                        else
                            a[i][j] = Math.max(a[i - 1][j], a[i][j - 1]);
                    }
                    right.up();
                }
                done.signal();
            } catch (InterruptedException ex) {
            }
        }
        // end-Band-run

    }
}
