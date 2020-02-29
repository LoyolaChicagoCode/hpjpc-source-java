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
 * This class defines the operations that a single thread performs
 * in calulcating the area under a function based on a specified reange
 *
 * @author John Shafaee
 * Date: July 5, 1999
 */

package info.jhpc.textbook.chapter12.integration;

import java.io.Serializable;

public class IntegTrapRegion implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8937266464553439210L;

    // Private variables used in calculating a specified region
    private double x_start, x_end;

    private int granularity;

    private double areaOfRegion = 0;

    private F_of_x f;

    /**
     * Constructor
     */
    public IntegTrapRegion(double x_start, double x_end, int granularity,
                           F_of_x f) {

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

        double area = 0.0d;
        double range = x_end - x_start;
        double x0 = x_start;
        double x1 = x_start + ((1.0d / granularity) * range);

        for (int i = 0; i < granularity; i++) {
            area = area + calcArea(x0, x1);
            x0 = x1;
            x1 = x_start + ((i + 2.0d) / granularity * range);
        }

        areaOfRegion = area;

        // indicate to the user that the thread is done
    }

    /**
     * Method that returns the final computed region size. Implemented to avoid
     * the dependency on other classes.
     */
    public double getArea() {
        return areaOfRegion;
    }

    /**
     * Calculates the are of a trapazoid given the beginning and ending x
     * coordinates. The method uses the defind f(x) function for calculating the
     * height of the trapazoid legs (l1, l2).
     */
    private double calcArea(double x0, double x1) {
        double base = x1 - x0;
        double l1 = f.f(x0);
        double l2 = f.f(x1);

        // Calculates and returns the area of the specified trapazoid
        return (base * .5d * (max(l1, l2) + min(l1, l2)));
    }

    private double max(double a, double b) {
        return ((a > b) ? a : b);
    }

    private double min(double a, double b) {
        return ((a > b) ? b : a);
    }

}
