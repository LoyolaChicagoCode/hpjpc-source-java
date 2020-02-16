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
 * by means of trapazoidal areas. This is the second version of the 
 * IntegTrap3 program.  It utlizes the concurrent abstractions: 
 * SimpleFutures and RunQueues.
 *
 * @author John Shafaee & Thomas Christopher
 * Date: July 22, 1999
 * 
 */
package info.jhpc.textbook.chapter05.integration.accumulator;

import info.jhpc.thread.*;

public class IntegTrap3 {

   int numThreads;

   int numRegions;

   int granularity;

   /**
    * MAIN
    */
   public static void main(String[] args) {

      if (args.length != 5) {
         System.out.println("\nUSAGE: IntegTrap3 <num threads>"
               + " <num regions> <range start> <range end> <granularity>");
         System.out.println("version: 3\n");
         System.exit(1);
      }

      int num_t = (new Integer(args[0])).intValue();
      int num_r = (new Integer(args[1])).intValue();
      double start = (new Double(args[2])).doubleValue();
      double end = (new Double(args[3])).doubleValue();
      int gran = (new Integer(args[4])).intValue();

      System.out.println("Starting calculations...");

      /**
       * Define the function to integrate as a class that implements F_of_x.
       * This endures that the object will provide the proper method for
       * computing the result of a true function.
       */
      class function implements F_of_x {
         public function() {
         }

         public double f(double x) {
            return x * x; // a parabola F(x) = x^2
         }
      }

      function fn = new function();

      // begin timing the process from thread creation time
      long begin_time = System.currentTimeMillis();

      IntegTrap3 integeral = new IntegTrap3(num_t, num_r, gran);
      double area = integeral.integrate(start, end, fn);

      // stop timing; all threads have completed
      long end_time = System.currentTimeMillis();

      // output results
      printResults(area, num_t, num_r, gran, (end_time - begin_time));

   }

   /**
    * Constructor - creates and initiates child threads for performing the
    * calculation.
    */
   public IntegTrap3(int numThreads, int numRegions, int granularity) {

      // check for invalid integration options
      try {
         if (numThreads < 1)
            throw new BadThreadCountException();

         if (numRegions < 1)
            throw new BadRegionCountException();

         if (granularity < 1)
            throw new BadGranularityException();
      } catch (Exception e) {
         System.out.println(e.toString());
         System.exit(1);
      }
      this.numThreads = numThreads;
      this.numRegions = numRegions;
      this.granularity = granularity;
   }

   public double integrate(double a, double b, F_of_x fn) {
      int i;

      // area under curve
      double totalArea = 0.0d;

      Accumulator acc = null;

      if (a > b)
         throw new BadRangeException();
      if (a == b)
         throw new NoRangeException();

      // create a RunQueue with the defined max. number of threads
      RunQueue regionQueue = new RunQueue(numThreads);

      try {
         double range = b - a;
         double start = a;
         double end = a + ((1.0d) / numRegions * range);

         acc = new Accumulator(numRegions, new Double(0.0));

         for (i = 0; i < numRegions; i++) {

            // create a IntegTrap3Region with the designated
            // Accumulator and pass it to the RunQueue
            regionQueue.put(new IntegTrap3Region(start, end, granularity, fn,
                  acc));

            // set the range for the next thhread
            start = end;
            end = a + ((i + 2.0d) / numRegions * range);
         }
      } catch (Exception e) {
         System.out.println("Exception occured in creating "
               + "and initializing thread.\n" + e.toString());
      }

      try {
         totalArea = ((Double) acc.getFuture().getValue()).doubleValue();
      } catch (Exception e) {
         System.out
               .println("Could not retrieve value from Accumulator's Future.");
         System.exit(1);
      }
      regionQueue.setMaxThreadsWaiting(0);

      return totalArea;
   }

   /**
    * Report the final results of the calculation.
    */
   public static void printResults(double totalArea, int threadCount,
         int numRegions, int granularity, long run_time) {

      System.out.println("\n             RESULTS           ");
      System.out.println("===============================");
      System.out.println("Total area under curve : " + totalArea);
      System.out.println("Number of threads used : " + threadCount);
      System.out.println("Number of regions      : " + numRegions);
      System.out.println("Granularity of calc.   : " + granularity
            + " trapaziods per sub-region");
      System.out.println("Total run time         : " + run_time + " msec.");
      System.out.println("===============================\n");

   }
}
