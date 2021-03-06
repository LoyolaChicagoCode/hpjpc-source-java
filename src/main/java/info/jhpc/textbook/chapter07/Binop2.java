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
package info.jhpc.textbook.chapter07;

public class Binop2 implements /* Runnable, */StoreOp {
    Object lopnd, ropnd;

    Op2 continuation;

    int needed = 2;

    public Binop2(Op2 continuation) {
        this.continuation = continuation;
    }

    public void store(int i, Object value) {
        if (i == 0)
            lopnd = value;
        else
            ropnd = value;
        if (--needed == 0)
            continuation.op(lopnd, ropnd);
    }
    // public void run() {
    // continuation.op(lopnd,ropnd);
    // }
}
