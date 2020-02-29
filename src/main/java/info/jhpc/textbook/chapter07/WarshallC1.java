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
//Warshall's algorithm using threads
package info.jhpc.textbook.chapter07;

import info.jhpc.thread.Accumulator;
import info.jhpc.thread.Future;

import java.util.Random;

public class WarshallC1 {

    public boolean[][] closure(boolean[][] a) {
        int i;
        Future[] kthRows = new Future[a.length];
        for (i = 0; i < kthRows.length; ++i)
            kthRows[i] = new Future();
        Accumulator done = new Accumulator(a.length, new boolean[a.length][]);
        for (i = 0; i < a.length; i++) {
            new Row(a[i].clone(), i, kthRows, done).start();
        }
        boolean[][] result = null;
        try {
            result = (boolean[][]) done.getFuture().getValue();
        } catch (InterruptedException ex) {
        }
        return result;
    }

    public static class Test1 {
        public static void main(String[] args) {
            int N = 10;
            int i;
            boolean[][] a = new boolean[N][N];
            WarshallC1 w = new WarshallC1();
            for (i = 0; i < N; i++)
                a[i][(i + 1) % N] = true;
            show(a);
            System.out.println();
            a = w.closure(a);
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
            int i, j;
            double probTrue = 0.3;
            boolean[][] a = new boolean[N][N];

            WarshallC1 w = new WarshallC1();

            Random rand = new Random();
            for (i = 0; i < N; i++) {
                for (j = 0; j < N; j++) {
                    a[i][j] = (rand.nextDouble() <= probTrue);
                }
            }
            show(a);
            System.out.println();
            a = w.closure(a);
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
            if (args.length < 1) {
                System.out.println("usage: java WarshallC1$TestTime2 N");
                System.exit(0);
            }
            int N = Integer.parseInt(args[0]);
            int i, j;
            double probTrue = 0.3;
            boolean[][] a = new boolean[N][N];

            WarshallC1 w = new WarshallC1();

            Random rand = new Random();
            for (i = 0; i < N; i++) {
                for (j = 0; j < N; j++) {
                    a[i][j] = (rand.nextDouble() <= probTrue);
                }
            }
            long start = System.currentTimeMillis();
            a = w.closure(a);
            System.out.println("WarshallC1\t" + N + "\t"
                    + (System.currentTimeMillis() - start));
        }
    }

    private class Row extends Thread {
        boolean[] row;

        int myRowNumber;

        Future[] row_k_step_k;

        Accumulator done;

        Row(boolean[] row, int myRowNumber, Future[] row_k_step_k,
            Accumulator done) {
            this.row = row;
            this.myRowNumber = myRowNumber;
            this.row_k_step_k = row_k_step_k;
            this.done = done;
        }

        public void run() {
            try {
                int j, k;
                boolean[] row_k;
                for (k = 0; k < row_k_step_k.length; k++) {
                    if (k == myRowNumber)
                        row_k_step_k[k].setValue(row.clone());
                    else if (row[k]) {
                        row_k = (boolean[]) row_k_step_k[k].getValue();
                        for (j = 0; j < row.length; j++) {
                            row[j] |= row_k[j];
                        }
                    }
                }
                boolean[][] result = (boolean[][]) done.getData();
                result[myRowNumber] = row;
                done.signal();
            } catch (InterruptedException ex) {
            }
        }
    }
}
