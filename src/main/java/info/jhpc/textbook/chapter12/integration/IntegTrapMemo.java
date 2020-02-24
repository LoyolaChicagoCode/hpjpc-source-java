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
/**
 * This class manages a choosen number of threads to complete the
 * integeral computation of a defined, single variable function
 * by means of trapazoidal areas.
 *
 * @author John Shafaee
 * Date: July 5, 1999
 */

package info.jhpc.textbook.chapter12.integration;

import info.jhpc.memo.MemoClient;

public class IntegTrapMemo {

    /**
     * Variable used to track a running total of the area under the curve in a
     * specified range
     */
    public static double totalArea = 0.0d;

    /**
     * MAIN
     */

    public static void main(String[] args) {
        try {
            if (args.length < 7) {
                System.err
                        .println("usage: ITM host port part# parts start end gran");
                return;
            }
            String memoHost = args[0];
            int memoPort = Integer.parseInt(args[1]);
            int partNumber = Integer.parseInt(args[2]);
            int num_t = Integer.parseInt(args[3]);
            double start = new Double(args[4]).doubleValue();
            double end = new Double(args[5]).doubleValue();
            int gran = Integer.parseInt(args[6]);

            MemoClient mc = new MemoClient(memoHost, memoPort, "memo");
            if (partNumber == 0) {
                System.out.println("master: Partitioning work.");
                createWork(mc, num_t, start, end, gran);

                System.out.println("master: doing some of the  work.");
                doWork(mc, partNumber);

                System.out.println("master: collating results.");
                double result = mergeResults(mc, num_t);

                System.out.println("Inner product = " + result);
                mc.goodbye();
            } else {
                doWork(mc, partNumber);
                mc.goodbye();
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            return;
        }
    }

    public static void createWork(MemoClient mc, int num_t, double start,
                                  double end, int gran) {

        /**
         * Define the function to integrate as a class that implements F_of_x.
         * This endures that the object will provide the proper method for
         * computing the result of a true function.
         */
        function fn = new function();

        partition2(mc, num_t, start, end, gran, fn);
    }

    /**
     * Constructor - creates and initiates child threads for performing the
     * calculation.
     */
    public static void partition2(MemoClient space, int numThreads, double a,
                                  double b, int granularity, F_of_x fn) {

        System.out.println("Partitioning regions...");
        // check for invalid integration options
        try {
            if (numThreads < 1)
                throw new BadThreadCountException();

            if (a > b)
                throw new BadRangeException();

            if (a == b)
                throw new NoRangeException();

            if (granularity < 1)
                throw new BadGranularityException();
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(1);
        }

        // initiate the array for managing child threads
        // childThreads = new IntegTrapRegion[ numThreads ];

        try {
            double range = b - a;
            double start = a;
            double end = a + ((1.0d) / numThreads * range);

            for (int i = 0; i < numThreads; i++) {
                // create and start new child threads
                // childThreads[i] = new IntegTrapRegion( start, end , granularity,
                // fn );
                // childThreads[i].start();
                System.out.println("\tPutting a new IntegTrapRegion into space...");
                space.put("region" + i, new IntegTrapRegion(start, end,
                        granularity, fn));
                System.out.println("\tNew IntegTrapRegion inserted!");

                // set the range for the next thhread
                start = end;
                end = a + ((i + 2.0d) / numThreads * range);
            }
        } catch (Exception e) {
            System.out
                    .println("Exception occured in creating and initializing thread.\n"
                            + e.toString());
        }
    }

    public static void doWork(MemoClient space, int partNumber) throws Exception {
        IntegTrapRegion region = (IntegTrapRegion) space.get("region"
                + partNumber);
        region.run();
        space.put("partial" + partNumber, region);
    }

    public static double mergeResults(MemoClient space, int num_t)
            throws Exception {
        double totalArea = 0.0;
        for (int i = 0; i < num_t; i++) {
            System.out.println("doing get on part " + i + " of " + num_t);
            IntegTrapRegion partialArea = (IntegTrapRegion) space.get("partial"
                    + i);
            totalArea += partialArea.getArea();
        }
        return totalArea;
    }
}
