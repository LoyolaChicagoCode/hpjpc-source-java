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
 * Memo version of Warshall
 */

package info.jhpc.textbook.chapter12.warshall;

import info.jhpc.memo.MemoClient;

import java.io.Serializable;
import java.util.Random;

class WarshallTQ {
    private int blkSize = 8; // 8x8 blocks

    private int numBlocks;

    private String memoHost;

    private int memoPort;

    private MemoClient space;

    public WarshallTQ(String memoHost, int memoPort, String name, int blkSize) {
        this.blkSize = blkSize;
        this.numBlocks = 0;
        this.memoHost = memoHost;
        this.memoPort = memoPort;
    }

    public void connectToMemo() throws Exception {
        space = new MemoClient(memoHost, memoPort, "memo");
    }

    public void disconnect() throws Exception {
        space.goodbye();
    }

    public MemoClient getMemoClient() {
        return space;
    }

    public void setMemoClient(MemoClient space) {
        this.space = space;
    }

    public void closureCreateWork(boolean[][] a) throws Exception {
        int i, j, NR, NC;

        IndexedKey kthRows = IndexedKey.unique(0);
        IndexedKey kthCols = IndexedKey.unique(0);
        NR = a.length;
        NC = a[0].length;
        for (i = 0; i < NR; i += blkSize)
            for (j = 0; j < NC; j += blkSize) {
                Block b = new Block(a, i, j, kthRows, kthCols, blkSize);
                space.put("block", b);
                numBlocks++;
            }
        space.put("blockCount", new Integer(numBlocks));
    }

    public void closureDoWork(int workerNumber, int numberOfWorkers)
            throws Exception {
        int numBlocksToWorkOn;
        Integer globalBlockCount = (Integer) space.getCopy("blockCount");
        numBlocksToWorkOn = 1 + globalBlockCount.intValue() / numberOfWorkers;

        Block[] block = new Block[numBlocksToWorkOn];
        MemoClient[] spaces = new MemoClient[numBlocksToWorkOn];

        int i = 0;
        for (int j = workerNumber; j < globalBlockCount.intValue(); j += numberOfWorkers) {
            Object entry = space.get("block");
            if (entry instanceof Block) {
                spaces[i] = new MemoClient(memoHost, memoPort, "memo");
                block[i] = (Block) entry;
                // block[i].setBarrier(done);
                block[i].setMemoClient(spaces[i]);
                block[i].start();
            } else {
                System.out.println("type of entry = " + entry.getClass());
            }
            i++;
        }

        i = 0;
        for (int j = workerNumber; j < globalBlockCount.intValue(); j += numberOfWorkers) {
            try {
                block[i].join();
            } catch (Exception e) {
            }
            spaces[i].goodbye();
            i++;
        }
    }

    public void closureMergeResults(boolean[][] a) throws Exception {
        System.out.println("block count = " + numBlocks);
        for (int i = 0; i < numBlocks; i++) {

            Block result = (Block) space.get("blockClosure");
            System.out.println("Read a Block successfully.");
            System.out.println("r = " + result.r + " c = " + result.c);
            // This should be merged into the matrix "a".
            result.merge(a);
        }

    }

    public static class MemoTest1 implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = -347843593802114356L;

        public static void main(String[] args) {
            if (args.length < 6) {
                System.err
                        .println("usage: ITM host port part# parts start end gran");
                return;
            }

            try {
                int i, j;
                double probTrue = 0.3;
                String memoHost = args[0];
                int memoPort = Integer.parseInt(args[1]);
                int partNumber = Integer.parseInt(args[2]);
                int numParts = Integer.parseInt(args[3]);
                int N = Integer.parseInt(args[4]);
                int bsize = Integer.parseInt(args[5]);

                boolean[][] a = new boolean[N][N];
                Random rand = new Random();
                for (i = 0; i < N; i++) {
                    for (j = 0; j < N; j++) {
                        a[i][j] = (rand.nextDouble() <= probTrue);
                    }
                }

                // show(a);
                // System.out.println();

                // /MemoClient mc = new MemoClient(memoHost, memoPort, "memo");
                //
                // Obviously--there is a problem with the underlying stream
                // getting confused. Signals are getting crossed, so to speak.
                // We need to maintain separate connections to the memo system.

                WarshallTQ w = new WarshallTQ(memoHost, memoPort, "memo", bsize);
                if (partNumber == 0) {
                    w.connectToMemo();
                    w.closureCreateWork(a);
                    w.closureDoWork(partNumber, numParts);
                    w.closureMergeResults(a);
                    System.out.println("We are done. Yippy!");
                    w.disconnect();
                } else {
                    w.connectToMemo();
                    w.closureDoWork(partNumber, numParts);
                    w.disconnect();
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

}
