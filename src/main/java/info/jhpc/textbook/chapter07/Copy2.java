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

import info.jhpc.thread.Future;

class Copy2 implements Runnable {
    Future src;

    StoreOp dst;

    int dstx;

    public Copy2(Future src, StoreOp dst, int dstx) {
        this.src = src;
        this.dst = dst;
        this.dstx = dstx;
    }

    public void run() {
        try {
            if (!src.isSet())
                src.runDelayed(this);
            dst.store(dstx, src.getValue());
        } catch (InterruptedException e) {
            dst.store(dstx, e);
        }
    }
}