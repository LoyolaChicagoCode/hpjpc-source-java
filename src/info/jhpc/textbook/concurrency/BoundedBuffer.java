/*
Copyright (c) 2000, Thomas W. Christopher and George K. Thiruvathukal

Java and High Performance Computing (JHPC) Organzization
Tools of Computing LLC

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

The names Java and High-Performance Computing (JHPC) Organization,
Tools of Computing LLC, and/or the names of its contributors may not
be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This license is based on version 2 of the BSD license. For more
information on Open Source licenses, please visit
http://opensource.org.
 */

package info.jhpc.textbook.concurrency;

/**
 * A FIFO queue of Objects for communication between producer and consumer
 * threads.
 * 
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */
public class BoundedBuffer {
   /**
    * Position of first object in the buffer. (Actually hd%buffer.length.)
    * Buffer is empty if hd==tl.
    */
   protected int hd = 0;
   /**
    * Position of next empty slot in the buffer. (Actually tl%buffer.length.)
    * Buffer is empty if hd==tl.
    */
   protected int tl = 0;
   /**
    * Array conbtaining FIFO queue of objects.
    */
   protected Object[] buffer;

   /**
    * Create a buffer of the specified size.
    * 
    * @param size
    *           Length of the buffer.
    */
   public BoundedBuffer(int size) {
      buffer = new Object[size];
   }

   /**
    * Put an object into the buffer. Wait until there is an empty slot
    * available.
    * 
    * @param v
    *           Object to insert into the buffer.
    * @throws InterruptedException
    *            If interrupted while waiting.
    */
   public synchronized void put(Object v) throws InterruptedException {
      while (tl - hd >= buffer.length)
         wait();
      buffer[tl++ % buffer.length] = v;
      notifyAll();
   }

   /**
    * Remove and return an object from the buffer. Wait until there is an object
    * in the buffer to remove and return.
    * 
    * @return The object removed.
    * @throws InterruptedException
    *            If interrupted while waiting.
    */
   public synchronized Object get() throws InterruptedException {
      Object v;
      while (tl == hd)
         wait();
      v = buffer[hd % buffer.length];
      buffer[hd++ % buffer.length] = null;
      notifyAll();
      return v;
   }
}
