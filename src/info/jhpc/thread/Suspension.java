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

package info.jhpc.thread;

/**
 * A suspended computation which will compute a value and assign it to a Future
 * when the value is demanded.
 * 
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */

public class Suspension implements Runnable {
   /**
    * The Future.
    */
   protected Future future = null;
   /**
    * The Runnable object to compute tha value of the Future if it is demanded.
    */
   protected Runnable runnable = null;

   /**
    * Create a Suspension.
    */
   public Suspension() {
   }

   /**
    * Create a Suspension with a runnable object to compute its value initially
    * assigned.
    * 
    * @param r
    *           The object that can compute the value of the suspension.
    */
   public Suspension(Runnable r) {
      runnable = r;
   }

   /**
    * Returns the Future associated with the Suspension.
    * 
    * @return The associated Future.
    */
   public synchronized Future getFuture() {
      if (future == null)
         future = new Future();
      if (runnable != null && runnable != this) {
         future.getRunQueue().run(runnable);
         runnable = this;
      }
      return future;
   }

   /**
    * Waits until a value has been assigned to the Suspension, then returns it.
    * 
    * @return The value assigned.
    * @throws InterruptedException
    *            if the thread is interrupted while waiting for a value to be
    *            assigned.
    */
   public Object getValue() throws InterruptedException {
      return getFuture().getValue();
   }

   /**
    * Assigns a value to the Future and notifies all waiting threads. Attempts
    * to change a previously assigned value will be ignored.
    * 
    * @param value
    *           The value to be assigned to the Future.
    */
   public synchronized void setValue(Object value) {
      runnable = this;
      getFuture().setValue(value);
   }

   /**
    * Force the suspension to be executed. (The runnable object, that is, that
    * is supposed to assign a value to the suspension's Future.)
    */
   public void run() {
      getFuture();
   }

   /**
    * Schedule a runnable object to execute when the suspension has its value
    * demanded (by run(), getValue(), or getFuture()).
    */
   public void runOnDemand(Runnable r) {
      if (runnable == null) {
         if (future != null) {
            future.getRunQueue().run(r);
            runnable = this;
         } else
            runnable = r;
      }
   }
}