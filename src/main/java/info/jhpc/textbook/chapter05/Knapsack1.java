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
package info.jhpc.textbook.chapter05;

import info.jhpc.thread.Future;
import info.jhpc.thread.SharedTerminationGroup;

import java.util.BitSet;

class Knapsack1 {
    BitSet selected;
    int capacity;
    float bestProfit = 0;
    Item[] item;
    Future done = new Future();
    SharedTerminationGroup tg = new SharedTerminationGroup(done);

    public Knapsack1(int[] weights, int[] profits, int capacity) {
        if (weights.length != profits.length)
            throw new IllegalArgumentException(
                    "0/1 Knapsack: differing numbers of weights and profits");
        if (capacity <= 0)
            throw new IllegalArgumentException("0/1 Knapsack: capacity<=0");
        item = new Item[weights.length];
        int i;
        for (i = 0; i < weights.length; i++) {
            item[i] = new Item();
            item[i].profit = profits[i];
            item[i].weight = weights[i];
            item[i].pos = i;
            item[i].profitPerWeight = ((float) profits[i]) / weights[i];
        }
        int j;
        for (j = 1; j < item.length; j++) {
            for (i = j; i > 0
                    && item[i].profitPerWeight > item[i - 1].profitPerWeight; i--) {
                Item tmp = item[i];
                item[i] = item[i - 1];
                item[i - 1] = tmp;
            }
        }
        new Thread(new Search(0, capacity, 0, new BitSet(item.length), tg))
                .start();
    }

    public BitSet getSelected() throws InterruptedException {
        done.getValue();
        BitSet s = new BitSet(item.length);
        for (int i = 0; i < item.length; i++) {
            if (selected.get(i))
                s.set(item[i].pos);
        }
        return s;
    }

    public int getProfit() throws InterruptedException {
        done.getValue();
        return (int) bestProfit;
    }

    private static class Item {
        int profit, weight, pos;

        float profitPerWeight;
    }

    public static class Test1 {
        public static void main(String[] args) {
            try {
                int num = 20;
                int max = 100;
                int[] p = new int[num];
                int[] w = new int[num];
                int capacity = (int) (num * (max / 2.0) * 0.7);
                int i;
                for (i = p.length - 1; i >= 0; i--) {
                    p[i] = 1 + (int) (Math.random() * (max - 1));
                    w[i] = 1 + (int) (Math.random() * (max - 1));
                }
                System.out.print("p:");
                for (i = p.length - 1; i >= 0; i--) {
                    System.out.print(" " + p[i]);
                }
                System.out.println();
                System.out.print("w:");
                for (i = p.length - 1; i >= 0; i--) {
                    System.out.print(" " + w[i]);
                }
                System.out.println();
                Knapsack1 ks = new Knapsack1(w, p, capacity);
                BitSet s = ks.getSelected();
                System.out.print("s:");
                for (i = p.length - 1; i >= 0; i--) {
                    System.out.print(" " + (s.get(i) ? "1" : "0") + " ");
                }
                System.out.println();
                System.out.println("Profit: " + ks.getProfit());
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    class Search implements Runnable {
        BitSet selected;

        int from;

        int startWeight = 0;

        int startProfit = 0;

        SharedTerminationGroup tg;

        Search(int from, int remainingWeight, int profit, BitSet selected,
               SharedTerminationGroup tg) {
            this.from = from;
            startWeight = remainingWeight;
            startProfit = profit;
            this.selected = selected;
            this.tg = tg;
        }

        void dfs(int i, int rw, int p) {
            if (i >= item.length) {
                if (p > bestProfit) {
                    bestProfit = p;
                    Knapsack1.this.selected = (BitSet) selected.clone();
                    System.out.println("new best: " + p);
                }
                return;
            }
            if (p + rw * item[i].profitPerWeight < bestProfit)
                return;
            if (rw - item[i].weight >= 0) {
                selected.set(i);
                dfs(i + 1, rw - item[i].weight, p + item[i].profit);
            }
            selected.clear(i);
            dfs(i + 1, rw, p);
            return;
        }

        public void run() {
            dfs(from, startWeight, startProfit);
            tg.terminate();
        }
    }

}
