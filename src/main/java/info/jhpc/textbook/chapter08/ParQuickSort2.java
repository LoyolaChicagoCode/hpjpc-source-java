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
/*
 ParQuickSort2 (Parallel Quick Sort).
 */

package info.jhpc.textbook.chapter08;

import info.jhpc.thread.*;

public class ParQuickSort2 {
    int numThreads;
    int minDivisible = 8;

    public ParQuickSort2(int numThreads) {
        this.numThreads = numThreads;
    }

    public void sort(int[] ary) {
        int N = ary.length;
        // System.out.println("sort()");
        TerminationGroup terminationGroup;
        RunQueue rq = new RunQueue();
        FutureFactory ff = new FutureFactory(rq);
        TerminationGroupFactory tgf = new SharedTerminationGroupFactory(ff);
        Runnable subsort;
        rq.setMaxThreadsCreated(numThreads);
        terminationGroup = tgf.make();
        subsort = new QuickSortThread2(ary, 0, N, terminationGroup, rq);
        rq.run(subsort);
        try {
            terminationGroup.awaitTermination();
        } catch (InterruptedException e) {
        }
        rq.setMaxThreadsWaiting(0);
    }

    public static class Test1 {
        public static void main(String[] args) {
            int[] a = new int[25];
            int i;
            for (i = a.length - 1; i >= 0; i--) {
                a[i] = (int) (Math.random() * 100);
            }
            for (i = 0; i < a.length - 1; i++) {
                System.out.print(" " + a[i]);
            }
            System.out.println();
            ParQuickSort2 s = new ParQuickSort2(3);
            s.sort(a);
            for (i = 0; i < a.length - 1; i++) {
                System.out.print(" " + a[i]);
            }
            System.out.println();
        }
    }

    public static class TestTime1 {
        public static void main(String[] args) {
            if (args.length < 2) {
                System.out.println("Usage: java ParQuickSort2$TestTime1 n nt");
                System.exit(0);
            }
            int N = Integer.parseInt(args[0]);
            int T = Integer.parseInt(args[1]);
            int[] a = new int[N];
            int i;
            long time;
            for (i = a.length - 1; i >= 0; i--) {
                a[i] = (int) (Math.random() * N);
            }
            // for (i=a.length-1;i>=0;i--) {
            // System.out.print(" "+a[i]);
            // }
            // System.out.println();
            ParQuickSort2 s = new ParQuickSort2(T);
            time = System.currentTimeMillis();
            s.sort(a);
            time = System.currentTimeMillis() - time;
            // for (i=a.length-1;i>=0;i--) {
            // System.out.print(" "+a[i]);
            // }
            // System.out.println();
            System.out.println("ParQuickSort2\t" + N + "\t" + T + "\t" + time);
        }
    }

    class QuickSortThread2 implements Runnable {
        int[] ary;
        int m;
        int n;
        TerminationGroup tg;
        RunQueue rq;

        public QuickSortThread2(int[] ary, int mm, int nn, TerminationGroup t,
                                RunQueue rq) {
            this.ary = ary;
            m = mm;
            n = nn;
            tg = t;
            this.rq = rq;
        }

        public void run() {
            quicksort(m, n);
            tg.terminate();
        }

        void quicksort(int m, int n) {
            // System.out.println("quicksort("+m+","+n+")");
            int i, j, pivot, tmp;
            if (n - m < minDivisible) {
                for (j = m + 1; j < n; j++) {
                    for (i = j; i > m && ary[i] < ary[i - 1]; i--) {
                        tmp = ary[i];
                        ary[i] = ary[i - 1];
                        ary[i - 1] = tmp;
                    }
                }
                return;
            }
            i = m;
            j = n;
            pivot = ary[i];
            while (i < j) {
                j--;
                while (pivot < ary[j])
                    j--;
                if (j <= i)
                    break;
                tmp = ary[i];
                ary[i] = ary[j];
                ary[j] = tmp;
                i++;
                while (pivot > ary[i])
                    i++;
                tmp = ary[i];
                ary[i] = ary[j];
                ary[j] = tmp;
            }
            Runnable subsort;
            if (i - m > n - i) {
                subsort = new QuickSortThread2(ary, m, i, tg.fork(), rq);
                rq.run(subsort);
                quicksort(i + 1, n);
            } else {
                subsort = new QuickSortThread2(ary, i + 1, n, tg.fork(), rq);
                rq.run(subsort);
                quicksort(m, i);
            }
        }
    }

}
