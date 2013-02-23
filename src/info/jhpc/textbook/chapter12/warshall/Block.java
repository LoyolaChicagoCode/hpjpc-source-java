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

import java.io.*;

import info.jhpc.memo.*;
import info.jhpc.thread.*;

import java.util.Random;

public class Block extends Thread implements Serializable {
   boolean[][] block;

   public int r, c; // upperleft

   int nr, nc;

   int N;

   int blkSize;

   IndexedKey rows, cols;

   transient Barrier done;

   transient MemoClient space;

   Block(boolean[][] a, int r, int c, IndexedKey rows, IndexedKey cols,
         int blkSize) {
      this.r = r;
      this.c = c;
      N = a.length;
      this.rows = rows;
      this.cols = cols;
      this.done = null;
      this.space = null;
      this.blkSize = blkSize;

      nr = Math.min(blkSize, a.length - r);
      nc = Math.min(blkSize, a[0].length - c);
      this.block = new boolean[nr][nc];
      for (int i = 0; i < nr; i++)
         for (int j = 0; j < nc; j++)
            block[i][j] = a[r + i][c + j];
   }

   public void setBarrier(Barrier done) {
      this.done = done;
   }

   public void setMemoClient(MemoClient space) {
      this.space = space;
   }

   public void run() {
      int i, j;
      int k;
      boolean IHaveRow, IHaveColumn;
      boolean[] row = null, col = null;
      try {
         for (k = 0; k < N; k++) {
            IHaveRow = k - r >= 0 && k - r < nr;
            IHaveColumn = k - c >= 0 && k - c < nc;
            if (IHaveRow) {
               space.put(rows.at(k + c * N), new BooleanArray(block[k - r]));
               row = block[k - r];
            }
            if (IHaveColumn) {
               col = new boolean[nr];
               for (j = 0; j < nr; j++)
                  col[j] = block[j][k - c];
               space.put(cols.at(k + r * N), new BooleanArray(col));
            }
            if (!IHaveRow) {
               BooleanArray brow = (BooleanArray) space.get(rows.at(k + c * N));
               System.out.println("got row " + brow.getClass().getName());
               row = brow.getData();
               if (row == null)
                  System.out.println("Got null row. Oops.");

            }
            if (!IHaveColumn) {
               BooleanArray bcol;
               bcol = (BooleanArray) space.get(cols.at(k + r * N));
               System.out.println("got col " + bcol.getClass().getName());
               col = bcol.getData();
               if (col == null)
                  System.out.println("Got null column. Oops.");
            }
            if (row == null || col == null) {
               System.out.println("Row null? " + (row == null));
               System.out.println("Col null? " + (col == null));
            }
            for (i = 0; i < nr; i++)
               if (col[i])
                  for (j = 0; j < nc; j++)
                     block[i][j] |= row[j];
         }// end for k

         System.out.println("Writing partial result of closure. r=" + r
               + " c = " + c);
         space.put("blockClosure", this);
      } catch (Exception e) {
         // In the event that this happens, even a prayer won't help.
      }
   }

   /* This must be done to put the result back together. In the master. */
   public void merge(boolean[][] a) {
      for (int i = 0; i < nr; i++)
         for (int j = 0; j < nc; j++)
            a[r + i][c + j] = block[i][j];
   }
}
