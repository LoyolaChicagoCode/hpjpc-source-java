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
 * @author John Shafaee & Thomas Christopher
 * Date: July 5, 1999
 * 
 * 
 */

package info.jhpc.textbook.chapter05.integration.threaded;

/* snipon: info.jhpc.textbook.chapter05.integration.threaded.IntegTrap1 */

/* snip: one */
public class IntegTrap1 {
   /**
    * number of threads to compute concurrently.
    */

   int numThreads;

   /**
    * number of trapazoids to compute per thread.
    */

   int granularity;

   public static class Test1 {
      /**
       * MAIN
       */
      public static void main(String[] args) {

         if (args.length != 4) {
            System.out.println("\nUSAGE: IntegTrap1$Test1 <num threads>"
                  + " <range start> <range end> <granularity>");
            System.out.println("version: 1\n");
            System.exit(1);
         }

         int num_t = (new Integer(args[0])).intValue();
         double start = (new Double(args[1])).doubleValue();
         double end = (new Double(args[2])).doubleValue();
         int gran = (new Integer(args[3])).intValue();

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

         // initiate the start and finish time variable
         long begin_time = 0;
         long end_time = 0;

         // begin timing the process from thread creation time
         begin_time = System.currentTimeMillis();

         IntegTrap1 integeral = new IntegTrap1(num_t, gran);
         double area = integeral.integrate(start, end, fn);

         // stop timing; all threads have completed
         end_time = System.currentTimeMillis();

         // output results
         printResults(area, num_t, gran, (end_time - begin_time));

      }
   }

   /**
    * Constructor - creates and initiates child threads for performing the
    * calculation.
    */
   public IntegTrap1(int numThreads, int granularity) {

      // check for invalid integration options
      try {
         if (numThreads < 1)
            throw new BadThreadCountException();

         if (granularity < 1)
            throw new BadGranularityException();
      } catch (Exception e) {
         System.out.println(e.toString());
         System.exit(1);
      }
      this.numThreads = numThreads;
      this.granularity = granularity;

   }

   /* snip: integrate */
   public double integrate(double a, double b, F_of_x fn) {
      int i;
      // initiate the array for managing child threads
      Thread[] childThreads;
      childThreads = new IntegTrap1Region[numThreads];
      // area under curve
      double totalArea = 0.0d;

      if (a > b)
         throw new BadRangeException();
      if (a == b)
         throw new NoRangeException();
      try {
         double range = b - a;
         double start = a;
         double end = a + ((1.0d) / numThreads * range);

         for (i = 0; i < numThreads; i++) {
            // create and start new child threads
            childThreads[i] = new IntegTrap1Region(start, end, granularity / numThreads, fn);
            childThreads[i].start();

            // set the range for the next thhread
            start = end;
            end = a + ((i + 2.0d) / numThreads * range);
         }
      } catch (Exception e) {
         System.out.println("Exception occured in creating and"
               + " initializing thread.\n" + e.toString());
      }

      for (i = 0; i < numThreads; i++) {
         try {
            childThreads[i].join();
            totalArea += ((IntegTrap1Region) childThreads[i]).getArea();
         } catch (Exception e) {
            System.out.println("Could not join with child threads!");
            System.exit(1);
         }
      }
      return totalArea;
   }

   /* pins: integrate */

   /**
    * Report the final results of the calculation.
    */
   public static void printResults(double totalArea, int threadCount,
         int granularity, long run_time) {

      System.out.println("\n             RESULTS           ");
      System.out.println("===============================");
      System.out.println("Total area under curve : " + totalArea);
      System.out.println("Number of threads used : " + threadCount);
      System.out.println("Granularity of calc.   : " + granularity / threadCount
            + " trapaziods per sub-region");
      System.out.println("Total run time         : " + run_time + " msec.");
      System.out.println("===============================\n");

   }

}
/* pins: one */