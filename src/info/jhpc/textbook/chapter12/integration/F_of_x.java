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
 * This is an interface used by IntegTrap region to call a programmer
 * defined function, f(x).  Any function passed to IntegTrapRegion must
 * implement this interface in order to insure that the function exists
 * and it takes a double and returns a do9uble.
 *
 * Written by: John Shafaee
 * Date: jul 21, 1999
 */

package info.jhpc.textbook.chapter12.integration;

import java.io.*;

public interface F_of_x extends Serializable {

   public double f(double x);

}
