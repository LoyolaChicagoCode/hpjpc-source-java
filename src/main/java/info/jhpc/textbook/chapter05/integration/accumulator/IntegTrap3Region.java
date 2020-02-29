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
 * This class defines the operations that a single thread
 * performsin calulcating the area under a function based
 * on a specified range. The class implements Runnable as
 * opposed to extending thread in order to be compatible
 * with the RunQueue abstratction.  This is part of the
 * second version of the ItegTrap program.
 *
 * @author John Shafaee & Thomas Christopher
 * Date: July 22, 1999
 */

package info.jhpc.textbook.chapter05.integration.accumulator;

import info.jhpc.thread.Accumulator;

public class IntegTrap3Region implements Runnable {

    // Privte variables used in calculating a specified region
    private String name;

    private double x_start, x_end;

    private int granularity;

    private F_of_x f;

    private Accumulator result;

    /**
     * Constructor
     */
    public IntegTrap3Region(double x_start, double x_end, int granularity,
                            F_of_x f, Accumulator result) {

        this.name = x_start + "-" + x_end;
        this.x_start = x_start;
        this.x_end = x_end;
        this.granularity = granularity;
        this.f = f;
        this.result = result;
    }

    /**
     * This is the method that is implemented as directed by the Runnable
     * interface. The code within this method is called when the thread is
     * started. All of the calculations that the thread will perform are defined
     * within this method. The equation used to calculate the area of the
     * trapazoid is defined as a seperate provate method.
     */
    public void run() {

        System.out.println("Thread: " + this.name + " started!");

        double area = 0.0d;
        double range = x_end - x_start;
        double g = granularity;

        for (int i = granularity - 1; i > 0; i--) {
            area += f.f((i / g) * range + x_start);
        }
        area += (f.f(x_start) + f.f(x_end)) / 2.0;
        area = area * (range / g);

        synchronized (result) {
            result.setData(new Double(area
                    + ((Double) result.getData()).doubleValue()));
        }
        result.signal();

        System.out.println("Thread: " + this.name + " completed! ");
    }

}
