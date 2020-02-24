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
 Warshall's algorithm using Dataflow. 
 Warshall's algorithm using chores to handle rows. Uses a class for each continuation. Chores are always places in a run queue.
 */
package info.jhpc.textbook.chapter09;

import info.jhpc.thread.Accumulator;
import info.jhpc.thread.IndexedKey;
import info.jhpc.thread.SharedTableOfQueues;

import java.util.Random;

class WarshallTQ {
    int blkSize = 8; // 8x8 blocks

    public WarshallTQ(int blkSize) {
        this.blkSize = blkSize;
    }

    public void closure(boolean[][] a) {
        int i, j, NR, NC;
        SharedTableOfQueues tbl = new SharedTableOfQueues();
        IndexedKey kthRows = IndexedKey.unique(0);
        IndexedKey kthCols = IndexedKey.unique(0);
        NR = a.length;
        NC = a[0].length;
        int nt = ((NR + blkSize - 1) / blkSize) * ((NC + blkSize - 1) / blkSize);
        Accumulator done = new Accumulator(nt);
        for (i = 0; i < NR; i += blkSize)
            for (j = 0; j < NC; j += blkSize) {
                new Block(a, i, j, tbl, kthRows, kthCols, done).start();
            }
        try {
            done.getFuture().getValue();
        } catch (InterruptedException ex) {
        }
    }

    public static class Test1 {
        public static void main(String[] args) {
            int N = 10;
            int bsize = 3;
            int i;
            boolean[][] a = new boolean[N][N];
            WarshallTQ w = new WarshallTQ(bsize);
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
            int bsize = 3;
            int i, j;
            double probTrue = 0.3;
            boolean[][] a = new boolean[N][N];

            WarshallTQ w = new WarshallTQ(bsize);

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
                System.out.println("usage: java WarshallTQ$TestTime2 N blksize");
                System.exit(0);
            }
            int N = Integer.parseInt(args[0]);
            int bsize = Integer.parseInt(args[1]);
            int i, j;
            double probTrue = 0.3;
            boolean[][] a = new boolean[N][N];

            WarshallTQ w = new WarshallTQ(bsize);

            Random rand = new Random();
            for (i = 0; i < N; i++) {
                for (j = 0; j < N; j++) {
                    a[i][j] = (rand.nextDouble() <= probTrue);
                }
            }
            long start = System.currentTimeMillis();
            w.closure(a);
            System.out.println("WarshallTQ\t" + N + "\t" + bsize + "\t"
                    + (System.currentTimeMillis() - start));
        }
    }

    private class Block extends Thread {
        boolean[][] a;

        boolean[][] block;

        int r, c; // upperleft

        int nr, nc;

        int N;

        SharedTableOfQueues tbl;

        IndexedKey rows, cols;

        Accumulator done;

        Block(boolean[][] a, int r, int c, SharedTableOfQueues tbl,
              IndexedKey rows, IndexedKey cols, Accumulator done) {
            this.a = a;
            this.r = r;
            this.c = c;
            N = a.length;
            this.tbl = tbl;
            this.rows = rows;
            this.cols = cols;
            this.done = done;
        }

        public void run() {
            int i, j;
            int k;
            boolean IHaveRow, IHaveColumn;
            boolean[] row = null, col = null;
            nr = Math.min(blkSize, a.length - r);
            nc = Math.min(blkSize, a[0].length - c);
            this.block = new boolean[nr][nc];
            for (i = 0; i < nr; i++)
                for (j = 0; j < nc; j++)
                    block[i][j] = a[r + i][c + j];
            try {
                for (k = 0; k < N; k++) {
                    IHaveRow = k - r >= 0 && k - r < nr;
                    IHaveColumn = k - c >= 0 && k - c < nc;
                    if (IHaveRow) {
                        tbl.put(rows.at(k + c * N), block[k - r].clone());
                        row = block[k - r];
                    }
                    if (IHaveColumn) {
                        col = new boolean[nr];
                        for (j = 0; j < nr; j++)
                            col[j] = block[j][k - c];
                        tbl.put(cols.at(k + r * N), col);
                    }
                    if (!IHaveRow) {
                        row = (boolean[]) tbl.look(rows.at(k + c * N));
                    }
                    if (!IHaveColumn) {
                        col = (boolean[]) tbl.look(cols.at(k + r * N));
                    }
                    for (i = 0; i < nr; i++)
                        if (col[i])
                            for (j = 0; j < nc; j++)
                                block[i][j] |= row[j];
                }// end for k

                for (i = 0; i < nr; i++)
                    for (j = 0; j < nc; j++)
                        a[r + i][c + j] = block[i][j];
                done.signal();
            } catch (InterruptedException iex) {
            }
        }
    }
}
