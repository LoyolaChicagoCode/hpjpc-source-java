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
 * performs in calulcating the area under a function based
 * on a specified range
 *
 * @author John Shafaee & Thomas Christopher
 * Date: July 5, 1999
 */

package info.jhpc.textbook.chapter05.integration.threaded;

public class IntegTrap1Region extends Thread {

    // Private variables used in calculating a specified region
    private double x_start, x_end;

    private int granularity;

    private double areaOfRegion = 0;

    private F_of_x f;

    /**
     * Constructor
     */
    public IntegTrap1Region(double x_start, double x_end, int granularity,
                            F_of_x f) {

        super(x_start + "-" + x_end);

        this.x_start = x_start;
        this.x_end = x_end;
        this.granularity = granularity;
        this.f = f;

    }

    /**
     * This is the method that is overloaded from the Thread class. The code
     * within this method is called when the thread is started. All of the
     * calculations that the thread will perform are defined within this method.
     * The equation used to calculate the area of the trapazoid is defined as a
     * seperate provate method.
     */
    public void run() {

        System.out.println("Thread: " + this.getName() + " started!");

        double area = 0.0d;
        double range = x_end - x_start;
        double g = granularity;

        for (int i = granularity - 1; i > 0; i--) {
            area += f.f((i / g) * range + x_start);
        }
        area += (f.f(x_start) + f.f(x_end)) / 2.0;
        area = area * (range / g);

        areaOfRegion = area;

        // indicate to the user that the thread is done
        System.out.println("Thread: " + this.getName() + " completed!");
    }

    /**
     * Method that returns the final computed region size. Implemented to avoid
     * the dependency on other classes.
     */
    public double getArea() {
        return areaOfRegion;
    }

}
